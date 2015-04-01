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
import java.util.Timer;
import java.util.TimerTask;

/**
 * IM 连接，封装 TCP/UDP 网络连接内容
 * 处理 IM 通讯协议封包解包，建立、保持、重建网络连接。
 *
 * Created by hjc on 14-12-7.
 */
public class Connection implements Runnable{
    private String ip;  // server ip 地址
    private int port;   // server 端口
    private long sesssionStamp; // 客户端消息状态时间戳
    private boolean authenticate; // 是否验证通过


    private Parser parser;  // 数据解析器
    private ConnectionListener connectionListener;  // 连接监听器，处理连接状态变化、收到消息的情况
    private Timer keepAliveTimer; // 定时任务，检查网络状态
    private Thread mainThread;  // 连接主线程
    private Socket socket;
    private OutputStream os;
    private InputStream in;

    private String appVersion = "im/1.0";
    private int uid;
    private String accessToken;
    private long activeStamp;
    private int connectFailure = 0;
    private int status = 0; // 0:Connecting,1:Binding,2:Connected,3:Disconnected

    private static final Logger log = Logger.getLogger(Connection.class);

    public Connection(String ip, int port, String appVersion) {
        this.ip = ip;
        this.port = port;
        this.appVersion = appVersion;
    }

    /**
     * 建立网络连接，通过用户 id 和 accessToken
     * @param uid  用户 id
     * @param accessToken   密码
     * @param stamp 消息时间戳，可以传 0，用于服务器知道客户端本地消息状态。
     */
    public void connect(int uid, String accessToken, long stamp) {
        this.uid = uid;
        this.accessToken = accessToken;
        this.sesssionStamp = stamp;
        this.mainThread = new Thread(this);
        this.mainThread.start();
        log.info("connect...");
    }

    /**
     * 向服务器发送消息
     * @param message 消息对象
     * @return
     */
    public boolean sendMessage(Message message) {
        return this.authenticate && this.send(message);
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        try {
            os.write(new End().toBuffer());
        } catch (IOException e) {
            log.info(e.getMessage());
        } finally {
            try {
                this.socket.close();
            } catch (IOException e) {
                log.info(e.getMessage());
            }
        }
        log.info("disconnect...");
    }

    private void bind() {
        status = 1;
        Session session = new Session((byte)1, sesssionStamp);
        this.send(session);
        log.info("bind...");
        this.connectFailure = 0;
    }

    private void authenticateFail() {
        connectionListener.connectionClosedOnError(new Exception("authenticate error"));
    }

    private void onSession(long stamp) {
        status = 2;
        this.connectionListener.connectionSuccessful();
        this.keepAlive();
        log.info("connected");
    }

    private void keepAlive() {
        final Connection self = this;
        this.activeStamp = System.currentTimeMillis();
        this.keepAliveTimer = new Timer();
        this.keepAliveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                int activeInterval = 250 * 1000; // 250s
                if (self.activeStamp + activeInterval < System.currentTimeMillis()) {
                    self.send(new Ping());
                }

                int timeout = 30 * 1000; // 30s
                if (self.activeStamp + activeInterval + timeout < System.currentTimeMillis()) {
                    self.reconnect();
                }

            }
        }, 10 * 1000, 250 * 1000);
    }

    private boolean send(Stanza stanza) {
        try {
            os.write(stanza.toBuffer());
            this.activeStamp = System.currentTimeMillis();
            return true;
        } catch (IOException e) {
            if(e instanceof ConnectException) {
                log.warn(e.getMessage());
                this.reconnect();
            } else if(e instanceof SocketException) {
                log.warn(e.getMessage());
                this.reconnect();
            } else {
                log.warn(e.getMessage());
            }
        }
        return false;
    }

    private void end() {
        try {
            this.socket.close();
        } catch (IOException e) {
            log.info(e.getMessage());
        }

        if (this.keepAliveTimer != null) {
            this.keepAliveTimer.cancel();
        }
        if (this.mainThread != null && this.mainThread.isAlive()) {
            this.mainThread.interrupt();
        }
        this.status = 3;
        this.authenticate = false;
    }

    private void reconnect() {
        this.end();
        this.connectFailure++;
        int sleepInterval = this.getSleepInterval();
        this.connectionListener.reconnectingIn(sleepInterval);

        log.info("reconnect in " + sleepInterval + " second");
        final Connection self = this;
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                self.connect(self.uid, self.accessToken, self.sesssionStamp);
            }
        }, sleepInterval * 1000);
    }

    private int getSleepInterval() {
        if (this.connectFailure < 3) {
            return 1;
        } else if (this.connectFailure < 10) {
            return 5;
        } else {
            return 30;
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
            if(e instanceof ConnectException) {
                log.warn(e.getMessage());
                this.reconnect();
            } else if(e instanceof SocketException){
                log.warn(e.getMessage());
                this.reconnect();
            } else {
                log.warn(e.getMessage());
            }
        }
    }

    private void startStream() {
        this.initParser();
        String osVersion = "android/4.0";
        this.send(new Auth((byte)1, uid, (byte)2, accessToken, appVersion, osVersion));
    }

    private void initParser() {
        final Connection self = this;
        this.parser = new Parser(new StanzaListener() {
            @Override
            public void onStanza(Stanza stanza) {
                if(!authenticate) {
                    if(stanza.is("AuthResp")) {
                        AuthResp authResp = new AuthResp(stanza);
                        if (authResp.getCode() == 0) {
                            authenticate = true;
                            self.bind();
                        } else {
                            //login error
                            self.authenticateFail();
                        }
                    }
                }

                if(authenticate) {
                    if(stanza.is("Session")) {
                        Session session = new Session(stanza);
                        if (session.getStep() == 2) {
                            self.onSession(session.getStamp());
                        }
                    } else {
//                    self.stanzaListener.onStanza(stanza);
                        self.onStanza(stanza);
                    }
                }
            }
        });
    }

    private void onStanza(Stanza stanza) {
        if(this.authenticate) {
            //收到消息
            if(stanza.is("Message")) {
                Message message = new Message(stanza);
                log.debug("receive msg:" + message.getBody() + " from " + message.getFrom());

                if (message.getType() == 1) {
                    // send receipt back
                    this.send(new Receipt(message.getId()));
                    this.sesssionStamp = message.getStamp();

                    this.connectionListener.recvMessage(message);
                } else if (message.getType() == 2) {
                    // send receipt back
                    this.send(new Receipt(message.getId()));
                    this.sesssionStamp = message.getStamp();

                    this.connectionListener.recvGroupMessage(message);
                } else if (message.getType() == 3) {
                    this.connectionListener.recvNotice(message);
                }

                this.activeStamp = System.currentTimeMillis();
            }

            if(stanza.is("Conflict")) {
                this.disconnect();
                log.warn("account conflict");
                this.connectionListener.recvConflict();
            }

            if(stanza.is("Ping")) {
                this.activeStamp = System.currentTimeMillis();
                log.info("receive pong");
            }

            if(stanza.is("Receipt")) {
                this.activeStamp = System.currentTimeMillis();
                log.info("receive pong");
            }

            if(stanza.is("End")) {
                this.connectionListener.connectionClosed();
            }
        }
    }

    public ConnectionListener getConnectionListener() {
        return connectionListener;
    }

    public void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    /**
     * 获取连接状态
     * @return int 0:Connecting,1:Binding,2:Connected,3:Disconnected
     */
    public int getStatus() {
        return this.status;
    }
}
