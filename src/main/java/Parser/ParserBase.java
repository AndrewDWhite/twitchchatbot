package Parser;

import Command.*;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParserBase {
    //Maintain thread safety
    ImmutableMap<String, Class> operationMapping;

    static Logger logger = LoggerFactory.getLogger("ParserBase");

    public ParserBase() {
        ImmutableMap.Builder operationMappingBuilder = new ImmutableMap.Builder();
        //TODO load from config
        operationMappingBuilder.put("!sr", SongRequest.class);
        operationMappingBuilder.put("!songrequest", SongRequest.class);
        operationMappingBuilder.put("!cs", SongPlaying.class);
        operationMappingBuilder.put("!help", Help.class);
        operationMappingBuilder.put("!playlist", CurrentPlaylist.class);

        operationMapping = operationMappingBuilder.build();
    }

    public ReadyBotOperation parse(String message, String requester) throws ParserException, IllegalAccessException, InstantiationException {
        logger.debug("Parsing: "+message);
        if (StringUtils.isBlank(message)){
            throw new ParserException("Blank command");
        }
        String[] command = message.split(" ",2);
        String commandFunctionName=command[0];

        if (commandFunctionName.length()<1 ){
            throw new ParserException("Not a command: not enough command length");
        }
        if (!operationMapping.containsKey(commandFunctionName.toLowerCase())){
            throw new ParserException("Not a command: unknown");
        }

        if (command.length>1) {
            String commandFunctionArgument = command[1];
            return new ReadyBotOperation((BotOperation)(operationMapping.get(commandFunctionName.toLowerCase()).newInstance()), commandFunctionArgument, requester);
        }
        return new ReadyBotOperation((BotOperation)(operationMapping.get(commandFunctionName.toLowerCase()).newInstance()), null, requester);


    }

    public ImmutableMap<String, Class> getOperationMapping() {
        return operationMapping;
    }
}
