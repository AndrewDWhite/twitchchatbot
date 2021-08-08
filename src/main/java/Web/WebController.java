package Web;

import ChatHistory.StoreChat;
import Command.FetchYouTube.SongEntry;
import Command.FetchYouTube.SongsPlaylist;
import Database.DatabaseResult;
import Database.DatabaseResultMessages;
import Parser.ParserBase;
import Parser.ParserException;
import Parser.ReadyBotOperation;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@EnableAutoConfiguration
public class WebController {

    static Logger logger = LoggerFactory.getLogger("WebController");

    static String requester = "bot";//TODO configure me?

    SongsPlaylist playlist;

    StoreChat history;

    WebController()
    {
        playlist = new SongsPlaylist();
        history = new StoreChat();
        //TODO remove
        //SongEntry song = new SongEntry("title", "id", 100);
        //database.store(song);
    }


    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "<a href = '/ui/Player.html'>Start</a>";
    }

    @RequestMapping("/next")
    @ResponseBody
    DatabaseResult next() {
        return playlist.markCurrentPlayed();
    }

    @ResponseBody
    @RequestMapping(path = "/requestId", method = RequestMethod.POST)
    String requestId(@RequestBody String id) throws IllegalAccessException, ParserException, InstantiationException {
        //TODO remove
        ParserBase parser = new ParserBase();
        ReadyBotOperation result = parser.parse("!sr "+id, requester);
        logger.info(result.toString());
        return result.run().toString();
    }

    @RequestMapping("/current")
    @ResponseBody
    SongEntry current() {
        return playlist.getCurrentPlaying();
    }

    @RequestMapping("/ui/Player.html")
    @ResponseBody
    String player() throws IOException {
        return IOUtils.toString(ClassLoader.getSystemResourceAsStream("Player.html"));
    }

    @RequestMapping("/ui/Player.js")
    @ResponseBody
    String playerJs() throws IOException {
        return IOUtils.toString(ClassLoader.getSystemResourceAsStream("Player.js"));
    }

    @RequestMapping("/ui/chat.html")
    @ResponseBody
    String chat() throws IOException {
        return IOUtils.toString(ClassLoader.getSystemResourceAsStream("chat.html"));
    }

    @RequestMapping("/ui/chat.js")
    @ResponseBody
    String chatJs() throws IOException {
        return IOUtils.toString(ClassLoader.getSystemResourceAsStream("chat.js"));
    }

    @RequestMapping("/ui/chat.css")
    @ResponseBody
    String chatCss() throws IOException {
        return IOUtils.toString(ClassLoader.getSystemResourceAsStream("chat.css"));
    }

    @RequestMapping("/ui/Player.css")
    @ResponseBody
    String playerCss() throws IOException {
        return IOUtils.toString(ClassLoader.getSystemResourceAsStream("Player.css"));
    }

    @RequestMapping(value = "/image", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    byte[] image() throws IOException {
        return IOUtils.toByteArray(ClassLoader.getSystemResourceAsStream("image.jpg"));
    }

    @RequestMapping("/playlist")
    @ResponseBody
    DatabaseResult playlist() {
        return playlist.getPlaylist();
    }

    @RequestMapping("/chat")
    @ResponseBody
    DatabaseResultMessages history() {
        DatabaseResultMessages result =history.getHistory();
        logger.info("To web client<messages>:" +result);
        return result;
    }

}
