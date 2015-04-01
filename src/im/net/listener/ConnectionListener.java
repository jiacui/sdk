package im.net.listener;

import im.net.stanza.Message;

/**
 * 连接监听器接口
 * 定义连接状态变化、收到消息等事件
 *
 * Created by hjc on 14-12-12.
 */
public interface ConnectionListener {

    public void recvMessage(Message message);

    public void recvGroupMessage(Message message);

    public void recvNotice(Message message);

    public void recvConflict();

    public void connectionSuccessful();

    public void connectionClosed();

    public void connectionClosedOnError(Exception e);

    public void reconnectingIn(int seconds);

    public void reconnectionSuccessful();

    public void reconnectionFailed(Exception e);
}
