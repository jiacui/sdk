package im.net.listener;

import im.net.core.Stanza;

/**
 * Created by hjc on 14-12-7.
 */
public abstract class StanzaListener {
    public abstract void onStanza(Stanza stanza);
}
