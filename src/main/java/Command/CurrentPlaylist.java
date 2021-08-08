package Command;

import Command.FetchYouTube.SongsPlaylist;
import Database.DatabaseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CurrentPlaylist implements BotOperation  {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public CommandResponse runCommand(String argument, String requester) {
        logger.info("Song playlist info request");
        //TODO implement me for other sources
        SongsPlaylist playlist = new SongsPlaylist();
        DatabaseResult response = playlist.getPlaylist();
        return new CommandResponse("@"+requester+" There are "+response.getSongEntries().size()+" songs in the queue: "+response.getSongEntries().toString());
    }

    @Override
    public String getUsage() {
        return "Returns the current playlist. No additional arguments are required.";
    }
}
