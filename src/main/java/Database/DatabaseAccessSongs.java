package Database;

import Command.FetchYouTube.SongEntry;

public interface DatabaseAccessSongs {
    public DatabaseResult store(SongEntry song);

    public DatabaseResult retrieveNext();

    public DatabaseResult retrievePlayList();

    //TODO may want to implement clears

}
