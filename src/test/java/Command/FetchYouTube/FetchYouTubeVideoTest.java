package Command.FetchYouTube;

import Command.CommandResponse;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class FetchYouTubeVideoTest {
    static Logger logger = LoggerFactory.getLogger("FetchYouTubeVideoTest");

    @Test
    public void testFetchIdInfo() throws IOException, FetcherException {
        FetchYouTubeVideo fetcher = new FetchYouTubeVideo();

        SongEntry entry = fetcher.fetchById("HxyXuNqOzws","anotherTestUserName", 0);
        logger.info(entry.toString());

    }

    @Test
    public void testFetchIdURL() throws IOException, FetcherException {
        FetchYouTubeVideo fetcher = new FetchYouTubeVideo();

        CommandResponse entry = fetcher.fetch("https://www.youtube.com/watch?v=5RgsYPAGH1Y", "testUserName");
        logger.info(entry.toString());

    }


}
