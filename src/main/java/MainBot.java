import ChatHistory.StoreChat;
import Command.CommandResponse;
import Configuration.BotConfiguration;
import Parser.ParserBase;
import Parser.ParserException;
import Parser.ReadyBotOperation;
import Respond.RespondToChannel;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PingEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MainBot  extends ListenerAdapter {

    private BotConfiguration config;
    ParserBase parser;
    Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    RespondToChannel responder = new RespondToChannel();
    StoreChat history = new StoreChat();

    MainBot(BotConfiguration config) {
        logger.info("Configuring bot");
        this.config = config;
        parser = new ParserBase();
    }


    /**
     * Twitch requires that a bot implements this method
     * @param event
     * @throws Exception
     */
    @Override
    public void onPing(PingEvent event) throws Exception{
        logger.info("Responding to ping");
        Main.myBot.sendRaw().rawLineNow(String.format("PONG %s\r\n", event.getPingValue()));
    }

    /**
     * API function that call us to do something with the message
     * @param event
     * @throws Exception
     */
    @Override
    public void onGenericMessage(GenericMessageEvent event) throws Exception {
        logger.info("onGenericMessage");

        String message = event.getMessage();
        User user = event.getUser();
        String requester = user.getNick();//TODO determine this another way?

        ReadyBotOperation command = null;
        try {
            history.storeMessage(message, requester);
            command = parser.parse(message, requester);//getCommandFromMessage

        } catch (ParserException parserException) {
            String response = "There was an exception parsing:\n"+event+"\n"+ ExceptionUtils.getStackTrace(parserException);
            logger.info(response);
            return ;
        } /*catch (Exception exception) {
            String response = "There was an exception parsing:\n"+event+"\n"+ ExceptionUtils.getStackTrace(exception);
            logger.info(response);
            responder.sendPublicMessage(new CommandResponse("@"+requester+" Invalid request: "+ message), Main.myBot, config.getChannelName());
            return ;
        }*/
        //No command we could recognize so we are done
        if (null==command){
            return ;
        }
        CommandResponse response = command.run();//runCommands

        responder.sendPublicMessage(response, Main.myBot, config.getChannelName());
    }




}
