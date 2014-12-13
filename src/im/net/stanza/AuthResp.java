package im.net.stanza;

import im.net.core.ByteBuffer;
import im.net.core.Stanza;

/**
 * Created by koujc on 14-12-7.
 */
public class AuthResp extends Stanza{
    private byte code;

    public AuthResp(Stanza stanza) {
        this.code = (Byte)new ByteBuffer(stanza.getContent())._byte().unpack().get(0);
    }

    public byte getCode() {
        return code;
    }

    public void setCode(byte code) {
        this.code = code;
    }
}
