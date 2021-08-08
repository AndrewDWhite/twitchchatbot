package Parser;

import Command.BotOperation;
import Command.CommandResponse;

public class ReadyBotOperation {
    private BotOperation operation;
    private String argument;
    private String requester;

    public CommandResponse run(){
        return operation.runCommand(argument, requester);
    }

    public ReadyBotOperation(BotOperation operation, String argument, String requester) {
        this.operation = operation;
        this.argument = argument;
        this.requester = requester;
    }

    public BotOperation getOperation() {
        return operation;
    }

    public void setOperation(BotOperation operation) {
        this.operation = operation;
    }

    public String getArgument() {
        return argument;
    }

    public void setArgument(String argument) {
        this.argument = argument;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    @Override
    public String toString() {
        return "ReadyBotOperation{" +
                "operation=" + operation +
                ", argument='" + argument + '\'' +
                ", requester='" + requester + '\'' +
                '}';
    }
}
