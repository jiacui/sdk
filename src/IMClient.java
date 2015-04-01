import im.net.core.Connection;
import im.net.core.Stanza;
import im.net.listener.ConnectionListener;
import im.net.listener.StanzaListener;
import im.net.stanza.Message;
import im.net.stanza.Ping;
import im.net.stanza.Receipt;
import org.apache.log4j.Logger;

import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hjc on 14-12-9.
 */
public class IMClient {
    private static IMClient instance;
    private static Connection connection;
    private static final Logger log = Logger.getLogger(IMClient.class);

    private int uid;

    private IMClient(){
        connection = new Connection("192.168.81.123", 10111, "golo/5.0");
        connection.setConnectionListener(connectionListener);
    }

    public static IMClient getInstance(){
        if(instance == null) {
            instance = new IMClient();
        }
        return instance;
    }

    private ConnectionListener connectionListener = new ConnectionListener(){

        @Override
        public void recvMessage(Message message) {
            // TO DO handle message
        }

        @Override
        public void recvGroupMessage(Message message) {
            // TO DO handle group message
        }

        @Override
        public void recvNotice(Message message) {
            // TO DO handle notice message
        }

        @Override
        public void recvConflict() {
            // TO DO handle account conflict
        }

        @Override
        public void connectionSuccessful() {

//            System.out.println("输入消息内容:");
//            new Runnable(){
//                @Override
//                public void run() {
//                    Scanner sc = new Scanner(System.in);
//                    String msg = sc.next();
//                    instance.sendChatMessage(905579, msg);
//                }
//            }.run();
        }

        @Override
        public void connectionClosed() {

        }

        @Override
        public void connectionClosedOnError(Exception e) {
            log.error(e.getMessage());
        }

        @Override
        public void reconnectingIn(int seconds) {

        }

        @Override
        public void reconnectionSuccessful() {

        }

        @Override
        public void reconnectionFailed(Exception e) {

        }
    };
    public void connect(int uid, String accessToken) {
        this.uid = uid;
//        long stamp = 1234567891234L;
        long stamp = 0;
        connection.connect(uid, accessToken, stamp);
    }

    public boolean sendChatMessage(int to, String msgBody) {
        Message message = new Message();
        message.setFrom(this.uid);
        message.setTo(to);
        message.setType(Message.CHAT);
        message.setStamp(0);
        message.setBody(msgBody);
        return this.send(message);
    }

    public boolean sendGroupMessage(int gid, String msgBody) {
        Message message = new Message();
        message.setFrom(this.uid);
        message.setTo(gid);
        message.setType(Message.GROUP_CHAT);
        message.setStamp(0);
        message.setBody(msgBody);
        return this.send(message);
    }

    private boolean send(Message message) {
        return connection.sendMessage(message);
    }

    public static void disconnect() {
        connection.disconnect();
    }
}
