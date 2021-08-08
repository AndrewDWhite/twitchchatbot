package Database;

import Command.FetchYouTube.SongEntry;

import java.util.ArrayList;
import java.util.List;

public class DatabaseResult {
    private Exception exception;

    private List<SongEntry> songEntries;

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public List<SongEntry> getSongEntries() {
        return songEntries;
    }

    public void setSongEntries(List<SongEntry> songEntries) {
        this.songEntries = songEntries;
    }

    DatabaseResult(){
        this.exception = null;
        this.songEntries = null;
    }

    DatabaseResult(Exception exception) {
        this.exception = exception;
        this.songEntries = null;
    }
    DatabaseResult(SongEntry song){
        this.exception = null;
        List<SongEntry> songToAdd = new ArrayList<>();
        songToAdd.add(song);
        songEntries = songToAdd;
    }

    DatabaseResult(List<SongEntry> songs) {
        this.exception = null;
        songEntries = songs;
    }

    @Override
    public String toString() {
        return "DatabaseResult{" +
                "exception=" + exception +
                ", songEntries=" + songEntries +
                '}';
    }
}
