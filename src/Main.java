import java.util.Calendar;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import javax.mail.*;

public class Main {

    public static String ip = "10.1.10.105";

    public static void main(String[] args) {

        Timer timer = new Timer();
        long interval = (5*60*1000); //polls for 5 mins

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runDiscoBall();
            }
        }, 0, interval);

    }

    public static void runDiscoBall(){
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

//            long startTime = System.currentTimeMillis();
//            long maxDurationInMilliseconds = 30 * 60 * 1000; //
//            long totalDuration = startTime + maxDurationInMilliseconds;

            //Run for 30 Minutes when the reader finds a recent email
//            while(System.currentTimeMillis() < totalDuration){
//                if(msg.isSet(Flags.Flag.RECENT)) {
//                    //WemoDevice wd = new WemoDevice("http://10.1.10.105:49153/setup.xml");
//                    //wd.turnOn();
//
//                }
//
//            }
            Timer timer = new Timer();
            long interval = (2*60*1000);
            if(msg.isSet(Flags.Flag.RECENT)) {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //WemoDevice wd = new WemoDevice("http://"+ ip + "/setup.xml");
                        System.out.println("Wemo is turned on");
                        try {
                            Thread.sleep(5000);
                            //wd.turnOff();
                        } catch (InterruptedException e) {
                            System.out.println("Sleep was interrupted. Could possibly be from connection timeout at: " + ip);
                        }
                    }
                },0, interval);
            }
            msg.setFlag(Flags.Flag.DELETED, true);

        } catch (Exception ex) {
            System.out.println("No recent messages found in inbox: stgnewhirediscoball@stgconsulting.com");
            System.out.println("Wemo is turned off");

        }
    }
}
