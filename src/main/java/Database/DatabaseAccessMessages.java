package Database;


import ChatHistory.ChatMessage;

public interface DatabaseAccessMessages {

    public DatabaseResultMessages store(String chatMessage, String requestor);

    public DatabaseResultMessages store(ChatMessage message);

    public DatabaseResultMessages getHistory();
    //TODO may want to implement clears

}
