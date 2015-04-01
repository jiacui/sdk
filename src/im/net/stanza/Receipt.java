package im.net.stanza;

import im.net.core.ByteBuffer;
import im.net.core.Stanza;

import java.util.List;

/**
 * 消息回执
 * Created by huangjiacui on 15-3-25.
 */
public class Receipt extends Stanza {
    private String id;

    public Receipt() {
        super();

        this.id = "";
    }

    public Receipt(String id) {
        this.id = id;
    }

    public Receipt(Stanza stanza) {
        List values = new ByteBuffer(stanza.getContent())._string().unpack();
        this.id = (String)values.get(0);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] toBuffer() {
        this.setType((short) 0x0012);
        this.setContent(new ByteBuffer()._string(this.id).pack());
        return super.toBuffer();
    }
}


