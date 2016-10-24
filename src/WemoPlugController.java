import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import javax.mail.*;

public class WemoPlugController {

    private List<String> wemoIps = new ArrayList<>();
    private String wemoPort;
    private String wemoName;
    private String runDuration;
    private String mailBoxName;
    private String mailBoxPassword;

    public Message msg;
    public WemoDevice wd;

    private WemoPlugController(String wemoIp, String wemoPort, String wemoName, String runDuration, String mailBoxName, String mailBoxPassword){
        wemoIps = Arrays.asList(wemoIp.split(","));
        this.wemoPort = wemoPort;
        this.wemoName = wemoName;
        this.runDuration = runDuration;
        this.mailBoxName = mailBoxName;
        this.mailBoxPassword = mailBoxPassword;
    }

    public static void main(String[] args) {

        WemoPlugController wpc = new WemoPlugController(args[0], args[1], args[2], args[3], args[4], args[5]);
        //System.out.println(wpc.toString());
        wpc.runController();
    }

    /** Test
     *
     */
    private void runController(){

        try {
            Properties props = new Properties();
            props.setProperty("mail.store.protocol", "imaps");

            Session session = Session.getInstance(props, null);

            Store store = session.getStore();
            store.connect("imap.gmail.com", mailBoxName, mailBoxPassword);
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            msg = inbox.getMessage(inbox.getMessageCount());
            msg.setFlag(Flags.Flag.RECENT, true);

            for (String wemoIp: wemoIps) {
                System.out.println("Wemo IP:" + wemoIp + ":" + wemoPort);
            }

            if (msg.isSet(Flags.Flag.RECENT)) {
                System.out.println("Subject: " + msg.getSubject());
                System.out.println("Content: " + msg.getContent());
                System.out.println("Connecting...");
                int ballNum = 1;
                for (String wemoIp: wemoIps) {
                    wd = new WemoDevice("http://" + wemoIp + ":" + wemoPort + "/setup.xml"); // If WeMo stops working, change port to either 49154 or 45153
                    wd.turnOn();
                    System.out.println(wemoName + " " + ballNum + "is on");
                    ballNum++;
                }
                setSleep();
                msg.setFlag(Flags.Flag.DELETED, true);
            }
            else{
                System.out.println("There are no recent messages in" + mailBoxName);
            }

        } catch (IndexOutOfBoundsException ex) {
            System.out.println("Error: " + ex.getMessage() + "\n");
            ex.printStackTrace();
            System.exit(1);
        } catch (MessagingException ex){
            System.out.println("Error: " + ex.getMessage() + "\n");
            ex.printStackTrace();
            System.exit(1);
        } catch (IOException ex){
            System.out.println("Error: " + ex.getMessage() + "\n");
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private void setSleep(){
        long interval = Long.parseLong(runDuration);
        try {
            TimeUnit.MINUTES.sleep(interval);
            wd.turnOff();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
