package Command;

import Parser.ParserBase;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Help implements BotOperation {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    ParserBase base;

    public Help(){
        base = new ParserBase();
    }

    @Override
    public CommandResponse runCommand(String argument, String requester) {
        try {
            if (!StringUtils.isBlank(argument) && base.getOperationMapping().containsKey(argument)) {
                return new CommandResponse("@" + requester + " " + argument+ " " +getHelp(base.getOperationMapping().get(argument)));
            }
            return new CommandResponse("@" + requester + " !help " +getHelp(this.getClass() ) );
        } catch (Exception exception){
            logger.error("Help should not have gotten here", exception);
            return new CommandResponse((exception));
        }
    }

    private String getHelp(Class passedClass) throws IllegalAccessException, InstantiationException {
        if (BotOperation.class.isAssignableFrom(passedClass)) {
            BotOperation operation = ((BotOperation)(passedClass.newInstance()));
            return operation.getUsage();
        }
        throw new InstantiationException("Invalid help request.");
    }

    @Override
    public String getUsage() {
        StringBuilder result = new StringBuilder();
        result.append("Returns help on how to use commands. Pass a command as an argument into this command to get help:");
        for (String command: base.getOperationMapping().keySet()) {
            result.append(" \"!help " + command+"\"");
        }
        return result.toString();
    }
}
