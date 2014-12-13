package im.net.core;

import im.net.listener.ConnectionListener;
import im.net.listener.StanzaListener;
import im.net.stanza.Auth;
import im.net.stanza.AuthResp;
import im.net.stanza.Session;
import im.net.util.ByteHelper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by koujc on 14-12-7.
 */
public class Connection implements Runnable{
    private String ip;
    private int port;
    private Socket socket;
    private OutputStream os;
    private InputStream in;
    private Parser parser;
    private ConnectionListener connectionListener;
    private StanzaListener stanzaListener;

    private String appVersion = "im/1.0";
    private String osVersion = "android/4.0";
    private int uid;
    private String accessToken;

    public int status = 0;//0:Connecting,1:Binding,2:Connected,3:Disconnected

    private static final Logger log = Logger.getLogger(Connection.class);

    public Connection(String ip, int port, StanzaListener handler) {
        this.ip = ip;
        this.port = port;
        this.connectionListener = connectionListener;
        final Connection self = this;
        this.parser = new Parser(new StanzaListener() {
            @Override
            public void onStanza(Stanza stanza) {
                if(stanza.is("AuthResp")) {
                    AuthResp authResp = new AuthResp(stanza);
                    if (authResp.getCode() == 0) {
                        self.bind();
                    } else {
                        //login error
                        self.authenticateFail();
                    }

                }

                if(stanza.is("Session")) {
                    Session session = new Session(stanza);
                    if (session.getStep() == 2) {
                        self.onSession(session.getStamp());
                    }
                }
//                if(stanza.getType() == 17)
//                {
//                    Message *message = [[Message alloc] initWithStanza:stanza];
//                    self.receiveMessage:message];
//                }
            }
        });
    }

    public void connect(int uid, String accessToken) {
        this.uid = uid;
        this.accessToken = accessToken;
        new Thread(this).start();
        log.info("connect...");
//                System.out.println("connect...");
    }

    public void bind() {
        status = 1;
        long time = 1234567891234L;
        Session session = new Session((byte)1, time);
        this.send(session);
        log.info("bind...");
    }

    private void authenticateFail() {
        connectionListener.connectionClosedOnError(new Exception("authenticate error"));
    }

    public void onSession(long stamp) {
        status = 2;
        this.parser.setStanzaListener(this.stanzaListener);
        log.info("connected");
        this.connectionListener.connectionSuccessful();
    }

    public void send(Stanza stanza) {
        try {
            os.write(stanza.toBuffer());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try {
            this.socket = new Socket();
            SocketAddress remoteAddr = new InetSocketAddress(this.ip, this.port);
            this.socket.connect(remoteAddr);
            this.os = socket.getOutputStream();
            this.in = socket.getInputStream();

            this.startStream();

            byte[] buffer = new byte[1024];
            int len = 0;
            while((len = in.read(buffer)) != -1) {
                byte[] b = ByteHelper.copyOfRange(buffer, 0, len);
//                Logger.getLogger("socket").log(Level.INFO, "in:" + ByteHelper.bytesToHexString(b));
                parser.write(b);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startStream() {
        this.send(new Auth((byte)1, uid, (byte)1, accessToken, appVersion, osVersion));
    }

    public ConnectionListener getConnectionListener() {
        return connectionListener;
    }

    public void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    public StanzaListener getStanzaListener() {
        return stanzaListener;
    }

    public void setStanzaListener(StanzaListener stanzaListener) {
        this.stanzaListener = stanzaListener;
    }
}
