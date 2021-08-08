package Command.FetchYouTube;

import Database.DatabaseAccessSongs;
import Database.DatabaseResult;
import Database.RedisStoreSongs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SongsPlaylist {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    DatabaseAccessSongs database;

    public SongsPlaylist() {
        database = new RedisStoreSongs();//TODO make generic
    }

    public void addSong(SongEntry song) { //TODO could throw error?
        database.store(song);
    }

    public void addSong(List<SongEntry> songs) {
        songs.forEach(song->addSong(song));
    }


    public SongEntry getCurrentPlaying() {
        logger.info("current");
        return getPlaylistSongs().get(0);
    }

    public DatabaseResult getPlaylist() {
        return database.retrievePlayList();
    }

    public List<SongEntry> getPlaylistSongs() {
        //TODO deal with errors
        DatabaseResult playlist = getPlaylist();
        logger.info(playlist.toString());
        List<SongEntry> songs = playlist.getSongEntries();
        logger.info(songs.toString());
        return songs;
    }

    public DatabaseResult markCurrentPlayed() {
        logger.info("next");
        DatabaseResult result  = database.retrieveNext();
        if (result.getException()==null) {
            logger.info("OK: " + result.toString());
            result = database.retrievePlayList();;
        }
        logger.info(result.toString());
        return result;
    }
}
