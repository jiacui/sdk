package im.net.stanza;

import im.net.core.ByteBuffer;
import im.net.core.Stanza;

import java.util.List;

/**
 * Created by huangjiacui on 15-3-25.
 */
public class End extends Stanza {
    public byte[] toBuffer() {
        this.setType((short) 0x0041);
        return super.toBuffer();
    }
}


