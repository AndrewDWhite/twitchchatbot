package Respond;

import Command.CommandResponse;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.PircBotX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RespondToChannel {

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public void sendPublicMessage(CommandResponse message, PircBotX myBot, String channel){
        String messageToSend = "";

        if (null==message){
            logger.info("Got null message from command response");
            return;
        }

        if (message.getException()!=null){
            messageToSend = message.getException().getMessage();
            logger.info("Stack trace for message sent to user: ", message.getException());
        }
        else {
            messageToSend = message.getOutput();
        }

        if (!StringUtils.isBlank(messageToSend)) {
            logger.info("To: #" + channel + " Send:" + messageToSend);
            myBot.sendIRC().message("#" + channel, messageToSend);
        } else {
            logger.info("No message to send");
        }
    }
}
