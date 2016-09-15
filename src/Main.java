import java.util.Calendar;
import java.util.Properties;
import javax.mail.*;

public class Main {

    public static void main(String[] args) {

        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");

        try {
            Session session = Session.getInstance(props, null);

            Store store = session.getStore();
            store.connect("imap.gmail.com", "stgnewhirediscoball@gmail.com", "!stgrocks!");

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            Message msg = inbox.getMessage(inbox.getMessageCount());
            msg.setFlag(Flags.Flag.RECENT, true);
            System.out.println("Subject: " + msg.getSubject());
            System.out.println("Content: " + msg.getContent());

            long startTime = System.currentTimeMillis();
            long maxDurationInMilliseconds = 30 * 60 * 1000;
            long totalDuration = startTime + maxDurationInMilliseconds;

//            Calendar calendar = Calendar.getInstance();
//            calendar.add(Calendar.MINUTE, 5);
//            long stopPollingTime = calendar.getTimeInMillis();

            //Run for 30 Minutes when the reader finds a recent email
            while(System.currentTimeMillis() < totalDuration){
                if(msg.isSet(Flags.Flag.RECENT)) {
                    WemoDevice wd = new WemoDevice("http://10.1.10.105:49153/setup.xml");
                    wd.turnOn();
                    try {
                        Thread.sleep(100);
                        wd.turnOff();
                    } catch (InterruptedException e) {
                    }
                }

            }

            msg.setFlag(Flags.Flag.DELETED, true);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

//    public void turnOnWemo(){
//        WemoDevice wd = new WemoDevice("http://10.1.10.100:49153/setup.xml");
//        wd.turnOn();
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
}
