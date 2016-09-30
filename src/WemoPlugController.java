import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import javax.mail.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.*;


public class WemoPlugController {

    private String wemoIp;
    private String wemoName;
    private String runDuration;
    private String mailBoxName;
    private String mailBoxPassword;

    public Message msg;
    public WemoDevice wd;

    private WemoPlugController(String wemoIp, String wemoName, String runDuration, String mailBoxName, String mailBoxPassword){

        this.wemoIp = wemoIp;
        this.wemoName = wemoName;
        this.runDuration = runDuration;
        this.mailBoxName = mailBoxName;
        this.mailBoxPassword = mailBoxPassword;
    }

    public static void main(String[] args) throws IOException, JSONException  {
        
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("http://localhost:50625/api/DeviceConfigs/1");
            HttpResponse response = client.execute(request);
            BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                System.out.println(line);
                String str = line;
                JSONObject obj = new JSONObject(str);
                String deviceName = obj.getString("deviceName");
                String ipAddress = obj.getString("ipAddress");
                String runDuration = obj.getString("runDuration");
                String emailAddress = obj.getString("emailAddress");
                String emailPassword = obj.getString("emailPassword");
                System.out.println(deviceName + "\n"
                        + ipAddress + "\n"
                        + runDuration + "\n"
                        + emailAddress + "\n"
                        + emailPassword + "\n"
                );

                WemoPlugController wpc = new WemoPlugController(ipAddress, deviceName, runDuration, emailAddress, emailPassword);
                //System.out.println(wpc.toString());
                wpc.runController();
            }


    }

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


            if (msg.isSet(Flags.Flag.RECENT)) {
                System.out.println("Subject: " + msg.getSubject());
                System.out.println("Content: " + msg.getContent());
                wd = new WemoDevice("http://" + wemoIp + ":49153/setup.xml");
                msg.setFlag(Flags.Flag.DELETED, true);
                wd.turnOn();
                System.out.println(wemoName + " is on");
                setSleep();
            }
            else{
                System.out.println("There are no recent messages in" + mailBoxName);
            }

        } catch (IndexOutOfBoundsException ex) {
            System.out.println("Error: " + ex.getMessage() + "\n");
            System.exit(0);
        } catch (MessagingException ex){
            System.out.println("Error: " + ex.getMessage() + "\n");
            System.exit(0);
        } catch (IOException ex){
            System.out.println("Error: " + ex.getMessage() + "\n");
            System.exit(0);
        }
    }

    private void setSleep(){

        long interval = Long.parseLong(runDuration);
        try {
            TimeUnit.MINUTES.sleep(interval);
            wd.turnOff();
            System.out.println(wemoIp + " is off");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}