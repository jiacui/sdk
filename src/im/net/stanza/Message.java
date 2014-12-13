package im.net.stanza;

import im.net.core.ByteBuffer;
import im.net.core.Stanza;

import java.util.List;

/**
 * Created by koujc on 14-12-8.
 */
public class Message extends Stanza{
    private int from = 0;
    private int to = 0;
    private int target = 0;
    private byte type;
    private long stamp;
    private String body;

    public final static byte CHAT = 1;
    public final static byte GROUP_CHAT = 2;

    public Message() {
        super();
    }

    public Message(int from, int to, int target, byte type, long stamp, String body) {
        this.from = from;
        this.to = to;
        this.target = target;
        this.type = type;
        this.stamp = stamp;
        this.body = body;
    }

    public Message(Stanza stanza) {
        List values = new ByteBuffer(stanza.getContent())._int()._int()._int()._byte()._long()._string().unpack();
        this.from = (Integer)values.get(0);
        this.to = (Integer)values.get(1);
        this.target = (Integer)values.get(2);
        this.type = (Byte)values.get(3);
        this.stamp = (Long)values.get(4);
        this.body = (String)values.get(5);
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getType() {
        return this.type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getStamp() {
        return stamp;
    }

    public void setStamp(long stamp) {
        this.stamp = stamp;
    }

    public byte[] toBuffer() {
        this.setType((short) 0x0011);
        this.setContent(new ByteBuffer()._int(this.from)._int(this.to)._int(this.target)
                ._byte(this.type)._double(this.stamp)._string(this.body).pack());
        return super.toBuffer();
    }
}