package im.net.listener;

/**
 * Created by koujc on 14-12-12.
 */
public interface ConnectionListener {

    public void connectionSuccessful();

    public void connectionClosed();

    public void connectionClosedOnError(Exception e);

    public void reconnectingIn(int seconds);

    public void reconnectionSuccessful();

    public void reconnectionFailed(Exception e);
}
