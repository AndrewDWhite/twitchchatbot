package Command;

import Command.FetchYouTube.SongsPlaylist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SongPlaying implements BotOperation  {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public CommandResponse runCommand(String argument, String requester) {
        logger.info("Song playing request");
        //TODO implement me for other sources
        SongsPlaylist playlist = new SongsPlaylist();
        return new CommandResponse("@"+requester+" "+playlist.getCurrentPlaying());
    }

    @Override
    public String getUsage() {
        return "Returns the currently playing song. No additional arguments are required.";
    }
}
