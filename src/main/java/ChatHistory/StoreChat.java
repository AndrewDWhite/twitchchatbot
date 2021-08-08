package ChatHistory;


import Database.DatabaseAccessMessages;
import Database.DatabaseResultMessages;
import Database.RedisMessages;

public class StoreChat {
    DatabaseAccessMessages database = new RedisMessages();

    public void storeMessage(String chatMessage, String requestor){
        database.store(chatMessage, requestor);
    }

    public DatabaseResultMessages getHistory() {
        return database.getHistory();
    }
}
