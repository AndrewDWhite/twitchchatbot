package Database;

import Command.FetchYouTube.SongEntry;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.embedded.RedisServer;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class RedisStoreSongsTest {
    static Logger logger = LoggerFactory.getLogger("RedisStoreSongsTest");

    @Test
    public void test() throws IOException {
        RedisServer redisServer = new RedisServer(63791);
        redisServer.start();
        try {
            RedisStoreSongs storeSongs = new RedisStoreSongs();
            SongEntry song = new SongEntry("title", "id", 100, "user", "youtube");
            storeSongs.store(song);

            DatabaseResult retrieved = storeSongs.retrieveNext();
            logger.info(retrieved.toString());
            redisServer.stop();
        } catch (Exception exception) {
            redisServer.stop();
            throw exception;
        }

    }

    @Test
    public void testMultiple() throws IOException {
        RedisServer redisServer = new RedisServer(63791);
        redisServer.start();
        try {
            RedisStoreSongs storeSongs = new RedisStoreSongs();
            SongEntry song = new SongEntry("title", "id", 100, "anotherUserAgainHere", "youtube");
            storeSongs.store(song);

            song = new SongEntry("title2", "id2", 200, "myLittleUserName", "youtube");
            storeSongs.store(song);

            DatabaseResult retrieved = storeSongs.retrievePlayList();
            logger.info(retrieved.toString());

            assertEquals("Two", 2, retrieved.getSongEntries().size());

            retrieved = storeSongs.retrieveNext();
            logger.info(retrieved.toString());
            assertEquals("First one", "title", retrieved.getSongEntries().get(0).getTitle());
            redisServer.stop();
        } catch (Exception exception) {
            redisServer.stop();
            throw exception;
        }

    }
}
