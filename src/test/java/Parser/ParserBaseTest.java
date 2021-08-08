package Parser;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParserBaseTest {
    static Logger logger = LoggerFactory.getLogger("ParserBaseTest");

    @Test
    public void test() throws IllegalAccessException, ParserException, InstantiationException {
        ParserBase parser = new ParserBase();
        try {
            ReadyBotOperation result = parser.parse("!sr HxyXuNqOzws", "testUserNameHere");
            logger.info(result.toString());
        } catch (Exception exception){
            logger.error("Error: " ,exception);
            throw exception;
        }

    }
}
