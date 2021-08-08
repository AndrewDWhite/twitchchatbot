package Database;

import ChatHistory.ChatMessage;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RedisMessages implements DatabaseAccessMessages {

    static Logger logger = LoggerFactory.getLogger("RedisMessages");

    JedisPool redisPool;
    RedisConfiguration config;

    public RedisMessages(){
        Yaml configuration = new Yaml();
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("redisConfig.yaml");
        config = configuration.loadAs(inputStream, RedisConfiguration.class);

        redisPool = new JedisPool(new JedisPoolConfig(), config.getHost(), config.getPort());
    }

    @Override
    public DatabaseResultMessages store(String chatMessage, String requestor) {
        return store(new ChatMessage(requestor,chatMessage));
    }

    @Override
    public DatabaseResultMessages store(ChatMessage message){
        logger.info("Storing: " + message.toString());
        try (Jedis jedis = redisPool.getResource()) {
            Gson gson = new Gson();
            jedis.rpush("messages", gson.toJson(message));
            return new DatabaseResultMessages();
        } catch (Exception exception){
            logger.error("Error: ", exception);
            return new DatabaseResultMessages(exception);
        }
    }

    @Override
    public DatabaseResultMessages getHistory() {
        try (Jedis jedis = redisPool.getResource()) {
            Gson gson = new Gson();
            Long size = jedis.llen("messages");
            logger.info("Messages size: "+ size);
            if (size<=0)
            {
                return new DatabaseResultMessages(new Exception("no results to return"));//TODO make specific class
            }
            List<String> result = jedis.lrange("messages", 0, size);//TODO make sure this is sorted
            List<ChatMessage> entries = new ArrayList<>();
            for (String currentEntry : result ) {
                entries.add(gson.fromJson(currentEntry, ChatMessage.class));
            }
            logger.info("Returning messages :"+ entries.toString());
            return new DatabaseResultMessages(entries);
        } catch (Exception exception){
            logger.error("Error: ", exception);
            return new DatabaseResultMessages(exception);
        }
    }
}
