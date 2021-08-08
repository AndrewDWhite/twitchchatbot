package Command.FetchYouTube;

import java.util.UUID;

public class SongEntry {
    private String title;
    private String id;
    private int duration;
    private String requester;
    private UUID requestUUid;
    private String source;

    public SongEntry(String title, String id, int durationInSeconds, String requester, String source) {
        this.title = title;
        this.id = id;
        this.duration = durationInSeconds;
        requestUUid = UUID.randomUUID();
        this.requester = requester;
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public UUID getRequestUUid() {
        return requestUUid;
    }

    public void setRequestUUid(UUID requestUUid) {
        this.requestUUid = requestUUid;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "SongEntry{" +
                "title='" + title + '\'' +
                ", id='" + id + '\'' +
                ", duration=" + duration +
                ", requester='" + requester + '\'' +
                ", requestUUid=" + requestUUid +
                ", source='" + source + '\'' +
                '}';
    }
}
