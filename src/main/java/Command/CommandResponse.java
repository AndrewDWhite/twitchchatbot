package Command;

public class CommandResponse {
    private Exception exception;
    private String output;

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public CommandResponse(Exception exception){
        this.exception = exception;
        output = null;
    }

    public CommandResponse(String output){
        this.exception = null;
        this.output = output;
    }

    public CommandResponse(){
        exception = null;
        output = null;
    }


    @Override
    public String toString() {
        return "CommandResponse{" +
                "exception=" + exception +
                ", output='" + output + '\'' +
                '}';
    }
}
