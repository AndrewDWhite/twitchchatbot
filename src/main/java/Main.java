
import Configuration.BotConfiguration;
import RunnableThread.WebRunner;
import org.pircbotx.PircBotX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import redis.embedded.RedisServer;

import java.io.InputStream;

public class Main {
    static Logger logger = LoggerFactory.getLogger("Main");

    public static PircBotX myBot;

    public static void main (String[] args) throws Exception {
        Yaml configuration = new Yaml();
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("config.yaml");
        BotConfiguration config = configuration.loadAs(inputStream, BotConfiguration.class);

        //TODO take out test implementation
        //Startup in memory database
        //RedisServer redisServer = new RedisServer(63791);
        //redisServer.start();

        myBot = new PircBotX(config.getBotConfigurationBuilder().addListener(new MainBot(config)).buildConfiguration());

        try {
            WebRunner threadWeb = new WebRunner();
            threadWeb.run(args);

            myBot.startBot();


            //TODO take out once using persistant database instance
            //redisServer.stop();

        } catch (Exception exception) {
            logger.error("Exception in main: ", exception);
            throw exception;
        }
    }

}
