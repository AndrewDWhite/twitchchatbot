package Command;

import Command.BotOperation;
import Command.FetchYouTube.FetchYouTubeVideo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SongRequest implements BotOperation {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public CommandResponse runCommand(String argument, String requester) {
        logger.info("Song requested: " + argument);
        FetchYouTubeVideo fetchYouTubeVideo = new FetchYouTubeVideo();

        if (fetchYouTubeVideo.canFetchThisVideo(argument)){
            logger.info("Using youtube to play");
            return fetchYouTubeVideo.fetch(argument, requester);
        }

        //TODO implement me for other sources
        return new CommandResponse("@"+requester+" Cannot parse the song request: "+ argument);
    }

    @Override
    public String getUsage() {
        return "Adds a song to the playlist. Pass in a youtube link or id as the argument.";
    }
}
