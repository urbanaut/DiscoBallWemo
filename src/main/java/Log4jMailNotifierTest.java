import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by bill.witt on 10/26/2016.
 */
public class Log4jMailNotifierTest {
    private static Logger logger = LoggerFactory.getLogger(Log4jMailNotifierTest.class);


    /**
     * To test whether fatal log sent to email id or not.
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            // Generate exception
            throw new Exception("Generating exception to test Log4j mail notification...");
        } catch (Exception ex) {
            logger.error("Reported Error: ", ex);
        }
    }
}
