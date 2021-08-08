package Command;

public interface  BotOperation {

    public CommandResponse runCommand(String argument, String requester);

    public String getUsage();
}
