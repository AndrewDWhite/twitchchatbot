package Database;

import Command.FetchYouTube.SongEntry;
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

public class RedisStoreSongs implements DatabaseAccessSongs {

    static Logger logger = LoggerFactory.getLogger("RedisStoreSongs");

    JedisPool redisPool;
    RedisConfiguration config;

    public RedisStoreSongs(){
        Yaml configuration = new Yaml();
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("redisConfig.yaml");
        config = configuration.loadAs(inputStream, RedisConfiguration.class);

        redisPool = new JedisPool(new JedisPoolConfig(), config.getHost(), config.getPort());
    }

    @Override
    public DatabaseResult store(SongEntry song) {
        logger.info("Storing: "+song.toString());
        try (Jedis jedis = redisPool.getResource()) {
            Gson gson = new Gson();
            jedis.rpush("playlist", gson.toJson(song));
            return new DatabaseResult();
        } catch (Exception exception){
            logger.error("Error: ", exception);
            return new DatabaseResult(exception);
        }
    }

    @Override
    public DatabaseResult retrieveNext() {
        try (Jedis jedis = redisPool.getResource()) {
            Gson gson = new Gson();
            String result = jedis.lpop("playlist");
            SongEntry resultSong = gson.fromJson(result, SongEntry.class);
            logger.info(resultSong.toString());
            return new DatabaseResult(resultSong);
        } catch (Exception exception){
            logger.error("Error: ", exception);
            return new DatabaseResult(exception);
        }
    }

    @Override
    public DatabaseResult retrievePlayList() {
        try (Jedis jedis = redisPool.getResource()) {
            Gson gson = new Gson();
            Long size = jedis.llen("playlist");
            logger.info("Playlist size: "+ size);
            if (size<=0)
            {
                return new DatabaseResult(new Exception("no results to return"));//TODO make specific class
            }
            List<String> result = jedis.lrange("playlist", 0, size);//TODO make sure this is sorted
            List<SongEntry> entries = new ArrayList<>();
            for (String currentEntry : result ) {
                entries.add(gson.fromJson(currentEntry, SongEntry.class));
            }
            logger.info("Returning playlist :"+ entries.toString());
            return new DatabaseResult(entries);
        } catch (Exception exception){
            logger.error("Error: ", exception);
            return new DatabaseResult(exception);
        }
    }
}
