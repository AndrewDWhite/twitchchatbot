package Command.FetchYouTube;

public class YouTubeConfiguration {
    private String baseUrl;
    private String songListInfoUrl;
    private String infoUrlForId;
    private String searchUrl;
    private String userCountryCode;
    private String idPattern;

    public String getPlaylistPattern() {
        return playlistPattern;
    }

    public void setPlaylistPattern(String playlistPattern) {
        this.playlistPattern = playlistPattern;
    }

    private String playlistPattern;
    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getSongListInfoUrl() {
        return songListInfoUrl;
    }

    public void setSongListInfoUrl(String songListInfoUrl) {
        this.songListInfoUrl = songListInfoUrl;
    }

    public String getInfoUrlForId() {
        return infoUrlForId;
    }

    public void setInfoUrlForId(String infoUrlForId) {
        this.infoUrlForId = infoUrlForId;
    }

    public String getSearchUrl() {
        return searchUrl;
    }

    public void setSearchUrl(String searchUrl) {
        this.searchUrl = searchUrl;
    }

    public String getUserCountryCode() {
        return userCountryCode;
    }

    public void setUserCountryCode(String userCountryCode) {
        this.userCountryCode = userCountryCode;
    }

    public String getIdPattern() {
        return idPattern;
    }

    public void setIdPattern(String idPattern) {
        this.idPattern = idPattern;
    }
}
