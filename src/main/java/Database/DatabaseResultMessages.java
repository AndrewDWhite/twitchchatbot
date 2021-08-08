package Database;

import ChatHistory.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class DatabaseResultMessages {
    private Exception exception;

    private List<ChatMessage> messages;

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    DatabaseResultMessages(){
        this.exception = null;
        this.messages = null;
    }

    DatabaseResultMessages(Exception exception) {
        this.exception = exception;
        this.messages = null;
    }
    DatabaseResultMessages(ChatMessage message){
        this.exception = null;
        List<ChatMessage> messageToAdd = new ArrayList<>();
        messageToAdd.add(message);
        messages = messageToAdd;
    }

    DatabaseResultMessages(List<ChatMessage> messages) {
        this.exception = null;
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "DatabaseResult{" +
                "exception=" + exception +
                ", messages=" + messages +
                '}';
    }
}
