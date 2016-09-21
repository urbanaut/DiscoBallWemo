import java.util.*;
import javax.mail.*;

public class Main {

    public static String wemoIp;
    public static long runDuration;
    public static String mailBoxName;
    public static String mailBoxPassword;

    public static void main(String[] args) {

        runDuration = Long.parseLong(args[0]);
        wemoIp = args[1];
        mailBoxName = args[2];
        mailBoxPassword = args[3];

        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");

        try {
            Session session = Session.getInstance(props, null);

            Store store = session.getStore();
            store.connect("imap.gmail.com", mailBoxName, mailBoxPassword);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            Message msg = inbox.getMessage(inbox.getMessageCount());
            msg.setFlag(Flags.Flag.RECENT, true);
            System.out.println("Subject: " + msg.getSubject());
            System.out.println("Content: " + msg.getContent());


            long interval = (runDuration);// Disco Ball will run for 30 minutes
            if (msg.isSet(Flags.Flag.RECENT)) {

                WemoDevice wd = new WemoDevice("http://" + wemoIp + ":49153/setup.xml");
                try {
                    msg.setFlag(Flags.Flag.DELETED, true);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                wd.turnOn();
                System.out.println(wemoIp + " is on");
                try {
                    Thread.sleep(interval);
                    wd.turnOff();
                    System.out.println(wemoIp + " is off");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        } catch (Exception ex) {
            System.out.println("No recent messages found in inbox: stgnewhirediscoball@gmail.com");
            //wd.turnOff();
            System.out.println(wemoIp + " is off");
            //ex.printStackTrace();
        }

    }
}
