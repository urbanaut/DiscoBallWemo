import java.util.*;
import javax.mail.*;

public class WemoPlugController {

    private String wemoIp;
    private String wemoName;
    private long runDuration;
    private String mailBoxName;
    private String mailBoxPassword;

    public Message msg;
    public WemoDevice wd;

    public WemoPlugController(String wemoIp, String wemoName, long runDuration, String mailBoxName, String mailBoxPassword){

        this.wemoIp = wemoIp;
        this.wemoName = wemoName;
        this.runDuration = runDuration;
        this.mailBoxName = mailBoxName;
        this.mailBoxPassword = mailBoxPassword;
    }

    public static void main(String[] args) {

        long parsedRunDuration = Long.parseLong(args[2]);
        WemoPlugController wpc = new WemoPlugController(args[0], args[1], parsedRunDuration, args[3], args[4]);
        wpc.runController();

    }

    private void runController(){

        try {

            getEmail();

            if (msg.isSet(Flags.Flag.RECENT)) {
                wd = new WemoDevice("http://" + wemoIp + ":49153/setup.xml");
                msg.setFlag(Flags.Flag.DELETED, true);
                wd.turnOn();
                System.out.println(wemoIp + " is on");
                setSleep();
            }


        } catch (Exception ex) {
            System.out.println("No recent messages found in inbox: " + mailBoxName);
            //wd.turnOff();
            System.out.println(wemoName + " is off");
            //ex.printStackTrace();
        }
    }

    private void setSleep(){

        long interval = (runDuration);
        try {
            Thread.sleep(interval);
            wd.turnOff();
            System.out.println(wemoIp + " is off");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void getEmail(){

        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");

        try{
            Session session = Session.getInstance(props, null);

            Store store = session.getStore();
            store.connect("imap.gmail.com", mailBoxName, mailBoxPassword);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            msg = inbox.getMessage(inbox.getMessageCount());
            msg.setFlag(Flags.Flag.RECENT, true);
            System.out.println("Subject: " + msg.getSubject());
            System.out.println("Content: " + msg.getContent());
        }catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }

    }

}