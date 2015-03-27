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
 * Created by koujc on 14-12-9.
 */
public class IMClient {
    private static IMClient instance;
    private static Connection connection;
    private StanzaListener stanzaListener;

    private static final Logger log = Logger.getLogger(IMClient.class);

    private int uid;
    private boolean authenticate;

    private IMClient(){
        stanzaListener = new StanzaListener() {
            @Override
            public void onStanza(Stanza stanza) {
                //收到消息
                if(stanza.is("Message")) {
                    Message message = new Message(stanza);
                    log.info("receive msg:" + message.getBody() + " from " + message.getFrom());

                    // send receipt back
                    IMClient.connection.send(new Receipt(message.getId()));

                    // TO DO
                }

                if(stanza.is("Conflict")) {
                    IMClient.disconnect();
                    log.info("account conflict");
                }

                if(stanza.is("Ping")) {
                    log.info("receive pong");
                }
            }
        };
        connection = new Connection("192.168.81.123", 10111, stanzaListener);
        connection.setConnectionListener(connectionListener);
        connection.setStanzaListener(stanzaListener);
    }

    public static IMClient getInstance(){
        if(instance == null) {
            instance = new IMClient();
        }
        return instance;
    }

    private ConnectionListener connectionListener = new ConnectionListener(){

        @Override
        public void connectionSuccessful() {
            authenticate = true;

            //间隔 5min 的心跳包
            new Runnable(){

                @Override
                public void run() {
                    Timer timer=new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            instance.ping();
                        }
                    }, 5 * 60 * 1000, 5 * 60 * 1000);
                }
            }.run();

//            new Runnable(){
//                @Override
//                public void run() {
//                    System.out.println("输入消息内容:");
//                    Scanner sc = new Scanner(System.in);
////                    while(true) {
//                        String msg = sc.next();
//                        instance.sendChatMessage(905579, msg);
//                    }
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
        connection.connect(uid, accessToken);
    }

    public boolean sendChatMessage(int to, String msgBody) {
        if(this.authenticate) {
            Message message = new Message();
            message.setFrom(this.uid);
            message.setTo(to);
            message.setType(Message.CHAT);
            message.setStamp(System.currentTimeMillis());
            message.setBody(msgBody);
            this.send(message);
            return true;
        }
        return false;
    }

    public void send(Message message) {
        connection.send(message);
    }

    public void ping() {
        connection.send(new Ping());
    }

    public static void disconnect() {
        connection.disconnect();
    }
}
