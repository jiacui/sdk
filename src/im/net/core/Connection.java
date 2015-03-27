package im.net.core;

import im.net.listener.ConnectionListener;
import im.net.listener.StanzaListener;
import im.net.stanza.*;
import im.net.util.ByteHelper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

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
//        this.connectionListener = connectionListener;
    }

    public void connect(int uid, String accessToken) {
        this.uid = uid;
        this.accessToken = accessToken;
        new Thread(this).start();
        log.info("connect...");
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
        this.connectionListener.connectionSuccessful();
        log.info("connected");
    }

    public void send(Stanza stanza) {
        try {
            os.write(stanza.toBuffer());
        } catch (IOException e) {
            if(e instanceof SocketException) {
                this.reconnect();
            } else {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        try {
            os.write(new End().toBuffer());
        } catch (IOException e) {
        } finally {
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.info("disconnect...");
    }

    public void reconnect() {
        if(this.socket != null) {
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        new Thread(this).start();
        log.info("reconnect...");
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
            if(e instanceof ConnectException) {
                this.reconnect();
            } else if(e instanceof SocketException){
                log.info(e.getMessage());
            }
        }
    }

    private void startStream() {
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
                } else if(stanza.is("Session")) {
                    Session session = new Session(stanza);
                    if (session.getStep() == 2) {
                        self.onSession(session.getStamp());
                    }
                } else {
                    self.stanzaListener.onStanza(stanza);
                }
            }
        });

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
