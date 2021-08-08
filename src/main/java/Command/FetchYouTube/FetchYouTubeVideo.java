package Command.FetchYouTube;

import Command.CommandResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;

import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FetchYouTubeVideo {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    YouTubeConfiguration config;
    Pattern idPattern;
    Pattern playlistPattern;
    SongsPlaylist playlist;

    final private static String source = "youtube";

    public FetchYouTubeVideo() {
        Yaml configuration = new Yaml();
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("youtubeConfig.yaml");
        config = configuration.loadAs(inputStream, YouTubeConfiguration.class);
        //TODO validate this
        idPattern = Pattern.compile(config.getIdPattern());
        playlistPattern = Pattern.compile(config.getPlaylistPattern());
        playlist = new SongsPlaylist();

    }

    private TypeOfYouTubeRequest typeRequest(String request) {
        Matcher matcher = idPattern.matcher(request);
        if (matcher.matches()) {
            return TypeOfYouTubeRequest.id;
        }

        Matcher matcherPlaylist = playlistPattern.matcher(request);
        logger.info("matched:"+matcherPlaylist.matches());
        if (matcherPlaylist.matches()){
            return TypeOfYouTubeRequest.playlist;
        }

        String normalized = normalizeYoutube(request);
        logger.info("normalized: " +normalized);
        Matcher matcherNormalized = idPattern.matcher(normalized);
        if (matcherNormalized.matches()) {
            return TypeOfYouTubeRequest.url;
        }

        //TODO implement the others
        return TypeOfYouTubeRequest.none;
    }

    private String normalizeYoutubePlaylist(String request) {
        int splitpoint = request.indexOf("playlist?list=");
        return request.substring(splitpoint+"playlist?list=".length());
    }

    private String normalizeYoutube(String request) {
        if (request.contains("youtu.be/")) {
            int splitPoint = request.indexOf("e/");
            return request.substring(splitPoint+2);
        }

        if (request.contains("www.youtube.com/watch?v=")){
            int splitPoint = request.indexOf("v=");
            return request.substring(splitPoint+2);
        }

        if (request.contains("www.youtube.com/v/")) {
            int splitPoint = request.indexOf("v/");
            return request.substring(splitPoint+2);
        }
        return request;
    }


    public boolean canFetchThisVideo(String request) {

        if (typeRequest(request) != TypeOfYouTubeRequest.none) {
            return true;
        }

        return false;
    }

    public CommandResponse fetch(String request, String requester) {

        TypeOfYouTubeRequest type = typeRequest(request);
        if (type==TypeOfYouTubeRequest.id){
            try {
                SongEntry song = fetchById(request, requester, 0);
                logger.info("fetched info for id");
                playlist.addSong(song);
                return new CommandResponse("Added request: "+ request + " "+song+" to queue for "+requester);
            } catch (IOException | FetcherException exception){
                return new CommandResponse(exception);
            }
        } else if (type == TypeOfYouTubeRequest.search){
            //TODO implement
            return new CommandResponse(new Exception("TODO implement youtube search"));
        } else if (type == TypeOfYouTubeRequest.url){
            try {
                SongEntry song = fetchById(normalizeYoutube(request), requester, 0);
                logger.info("fetched info for id url");
                playlist.addSong(song);
                return new CommandResponse("Added request: "+ request + " "+song+" to queue for "+requester);
            } catch (IOException | FetcherException exception){
                return new CommandResponse(exception);
            }
        } else if (type == TypeOfYouTubeRequest.playlist){
            try {
                List<SongEntry> song = fetchPlaylist(normalizeYoutubePlaylist(request), requester);
                logger.info("fetched playlist");
                logger.info("Processed Songs:" +song.size());
                playlist.addSong(song);
                return new CommandResponse("Added request: "+ request + " "+song+" to queue for "+requester);
            } catch (IOException | FetcherException exception){
                return new CommandResponse(exception);
            }
        }
        return new CommandResponse(new Exception("Invalid youtube request: cannot fetch " + type.name()));
    }

    List<SongEntry> fetchPlaylist(String playlistId, String requester) throws IOException, FetcherException {
        logger.info("Fetch playlist: "+playlistId);
        JsonElement responseForPlaylist = getJsonForPlaylist(playlistId);
        JsonArray items = getVideoElements(responseForPlaylist);

        if (!(items.size()>0)) {
            throw new FetcherException("Response for .items was not a json array of length >0....");
        }
        logger.info("size: "+ items.size());

        return processVideoElements(items, requester);
    }

    private JsonArray getVideoElements(JsonElement response) throws FetcherException {

        if (!response.isJsonObject()) {
            throw new FetcherException("Response was not a json object....");
        }
        JsonObject myResponseObject = response.getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        logger.info(gson.toJson(myResponseObject));

        JsonElement itemsElement = myResponseObject.get("items");
        if (!itemsElement.isJsonArray()) {
            throw new FetcherException("Response .items was not a json array....");
        }

        return itemsElement.getAsJsonArray();

    }

    private List<SongEntry> processVideoElements(JsonArray items, String requester) throws FetcherException, IOException {
        List<SongEntry> songs = new ArrayList<>();
        int ctr =0;
        String ExceptionsGiven="";
        for (JsonElement itemEntry : items) {
            try {
                logger.info("processing " + ctr + ": " + itemEntry);
                if (!itemEntry.isJsonObject()) {
                    throw new FetcherException("Response for .items[" + ctr + "] was not a json object....");
                }
                JsonObject myItem = itemEntry.getAsJsonObject();

                JsonObject contentDetails = getContentDetails(myItem);

                String videoId = getId(contentDetails);

                songs.add(fetchById(videoId, requester, ctr));
                logger.info("fetched info for id");
            } catch (Exception exceptionCaught){
                logger.error(""+exceptionCaught);
                ExceptionsGiven+=exceptionCaught;
            }
            ctr++;
        }
        logger.info("returning songs :"+ songs.size()+" Exceptions: "+ExceptionsGiven);
        return songs;
    }


    private SongEntry processVideoDetails(JsonElement itemEntry, String requester, int ctr) throws FetcherException {
        logger.info("process video details");
        if (!itemEntry.isJsonObject()) {
            throw new FetcherException("Response for .items["+ctr+"] was not a json object....");
        }
        JsonObject firstItem = itemEntry.getAsJsonObject();

        JsonObject contentDetails = getContentDetails(firstItem);
        validateVideo(getStatus(firstItem), contentDetails);

        return (new SongEntry(getTitle(getSnippet(firstItem)), getCurrentId(firstItem), getDuration(contentDetails), requester, source));

    }

    private String getCurrentId(JsonObject item) {
        logger.info("get current Id");
        JsonElement videoId = item.get("id");
        return videoId.getAsString();
    }

    private String getId(JsonObject contentDetails) {
        logger.info("getId");
        JsonElement videoId = contentDetails.get("videoId");
        return videoId.getAsString();
    }


    SongEntry fetchById(String id, String requester, int ctr) throws IOException, FetcherException {
        logger.info("Fetch id: "+id);
        JsonElement responseForId = getJsonForYoutubeId(id);
        JsonArray items = getVideoElements(responseForId);

        //Validate not a playlist
        if (items.size()!=1){
            throw new FetcherException("Response for id:"+ id+".items was not a json array of length 1....");
        }

        return processVideoDetails(items.get(0), requester, ctr);
    }

    private JsonObject getStatus(JsonObject item) throws FetcherException {
        JsonElement statusEntry = item.get("status");
        if (!statusEntry.isJsonObject()||null == statusEntry){
            throw new FetcherException("Response for .items[0].status was not a json object....");
        }
        return statusEntry.getAsJsonObject();
    }

    private JsonElement getSnippet(JsonObject item) throws FetcherException {
        logger.info("get snippet");
        JsonElement snippetEntry = item.get("snippet");
        if (!snippetEntry.isJsonObject()||null == snippetEntry){
            throw new FetcherException("Response for .items[0].snippet was not a json object....");
        }
        return snippetEntry;
    }

    private String getTitle(JsonElement snippetEntry){
        logger.info("get title");
        JsonObject snippet = snippetEntry.getAsJsonObject();
        JsonElement titleElement = snippet.get("title");
        return titleElement.getAsString();
    }

    private JsonObject getContentDetails (JsonObject item) throws FetcherException {
        JsonElement contentDetailsEntry = item.get("contentDetails");
        if (!contentDetailsEntry.isJsonObject() || null == contentDetailsEntry){
            throw new FetcherException("Response for .items[0].contentDetails was not a json object....");
        }
        return contentDetailsEntry.getAsJsonObject();
    }

    private void validateVideo(JsonObject status, JsonObject contentDetails) throws FetcherException {
        logger.info("validate video");
        //Validate embadable
        JsonElement enbadableElement = status.get("embeddable");
        if (!enbadableElement.isJsonPrimitive()|| null == enbadableElement){
            throw new FetcherException("Response for .items[0].status.embeddable was not a primitive....");
        }
        Boolean embeddable = enbadableElement.getAsBoolean();

        if (!embeddable){
            throw new FetcherException("The requested video is not embeddable");
        }

        //Validate not private
        JsonElement privacyStatusElement = status.get("privacyStatus");
        String privacyStatus = privacyStatusElement.getAsString();

        if (!"public".equals(privacyStatus)) {
            throw new FetcherException("The requested video is not public");
        }

        //Validate country
        if (!countryIsAllowed(contentDetails)) {
            throw new FetcherException("The requested video is not playable in the streamer's country");
        }

        //Check video length
        if (getDuration(contentDetails)<=0) {
            throw new FetcherException("The requested video has no length... is it a live stream?");
        }
        logger.info("validated");
    }

    private int getDuration(JsonObject contentDetails){
        logger.info("get duration");
        JsonElement durationElement = contentDetails.get("duration");
        String duration = durationElement.getAsString();

        Period p = ISOPeriodFormat.standard().parsePeriod(duration);
        return p.toStandardSeconds().getSeconds();

    }

    private JsonElement getJsonForPlaylist(String playlistId)throws IOException, FetcherException {

        //https://www.googleapis.com/youtube/v3/playlistItems?part=contentDetails&playlistId=" + listPathId + "&key=" + conf.getYoutubeAccessToken() + "&maxResults=50";
        String playlistUrl = config.getBaseUrl()+config.getSongListInfoUrl()+playlistId+"&key="+config.getApiKey();
        logger.info("id: "+playlistId);

        return getJsonForYoutube(playlistUrl);
    }

    private JsonElement getJsonForYoutube(String url) throws IOException, FetcherException {
        GetMethod get;
        HttpClient client = new HttpClient();

        get = new GetMethod(url);
        int error_code = client.executeMethod(get);
        if(error_code != 200) {
            logger.info("Error code: "+error_code);
            throw new FetcherException("Http error " + error_code + " from youtube");
        }

        String resp = IOUtils.toString(get.getResponseBodyAsStream(), "utf-8");
        if(resp == null) {
            logger.info("Couldn't get detail at " + url);
            throw new FetcherException("Couldn't understand youtube's response for id ");
        }
        Gson gson = new Gson();//TODO move for optimization
        return gson.fromJson(resp, JsonElement.class);
    }

    private JsonElement getJsonForYoutubeId(String id) throws IOException, FetcherException {
        String infoUrl = config.getBaseUrl()+config.getInfoUrlForId()+id+"&key="+config.getApiKey();
        logger.info("id: "+id);
        return getJsonForYoutube(infoUrl);
    }


    //youtube enforces country restrictions.
    //we want to know about it beforehand to fail the song when requested instead of when played.
    private boolean countryIsAllowed(JsonObject contentDetails) {
        JsonElement restrictionElement = contentDetails.get("regionRestriction");
        if (restrictionElement != null && restrictionElement.isJsonObject()) {
            JsonObject regionRestriction = restrictionElement.getAsJsonObject();
            //if there's an "allowed" list and we're not in it, fail.
            //if there's a "blocked" list and we are in it, fail
            JsonElement allowedElement = regionRestriction.get("allowed");
            if (allowedElement != null && allowedElement.isJsonArray()) {
                JsonArray allowed = allowedElement.getAsJsonArray();
                boolean weAreAllowed = false;
                for (JsonElement entry : allowed) {
                    if (entry != null && entry.isJsonPrimitive()) {
                        String allowedCountry = entry.getAsString();
                        if (allowedCountry.equalsIgnoreCase(config.getUserCountryCode())) {
                            weAreAllowed = true;
                        }
                    }
                }
                if (!weAreAllowed) {
                    return false;
                }
            }

            JsonElement blockedElement = regionRestriction.get("blocked");
            if (blockedElement != null && blockedElement.isJsonArray()) {
                JsonArray blocked = blockedElement.getAsJsonArray();
                for (JsonElement blockedCountryEntry : blocked) {
                    if (blockedCountryEntry != null && blockedCountryEntry.isJsonPrimitive()) {
                        String blockedCountry = blockedCountryEntry.getAsString();
                        if (blockedCountry.equalsIgnoreCase(config.getUserCountryCode())) {
                            return false;
                        }
                    }
                }

            }
        }
        return true;

    }
}
