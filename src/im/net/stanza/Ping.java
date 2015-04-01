package im.net.stanza;

import im.net.core.Stanza;

/**
 * Created by hjc on 14-12-8.
 */
public class Ping extends Stanza{
    public Ping() {
        super();
    }

    public byte[] toBuffer() {
        this.setType((short) 0x0033);
        return super.toBuffer();
    }
}
