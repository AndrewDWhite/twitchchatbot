package ChatHistory;

import java.util.UUID;

public class ChatMessage {
    String requestor;
    String message;
    private UUID requestUUid;

    public ChatMessage(String requestor, String message) {
        this.requestor = requestor;
        this.message = message;
        requestUUid = UUID.randomUUID();
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "requestor='" + requestor + '\'' +
                ", message='" + message + '\'' +
                ", requestUUid=" + requestUUid +
                '}';
    }

    public String getRequestor() {
        return requestor;
    }

    public void setRequestor(String requestor) {
        this.requestor = requestor;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UUID getRequestUUid() {
        return requestUUid;
    }

    public void setRequestUUid(UUID requestUUid) {
        this.requestUUid = requestUUid;
    }
}
