package im.net.stanza;

import im.net.core.ByteBuffer;
import im.net.core.Stanza;

import java.util.List;

/**
 * 消息
 * Created by hjc on 14-12-8.
 */
public class Message extends Stanza{
    private String id;
    private int from = 0;
    private int to = 0;
    private int target = 0;
    private byte type;
    private long stamp;
    private String body;

    public final static byte CHAT = 1;
    public final static byte GROUP_CHAT = 2;
    public final static byte NOTICE = 3;

    public Message() {
        super();

        this.id = "";
    }

    public Message(String id, int from, int to, int target, byte type, long stamp, String body) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.target = target;
        this.type = type;
        this.stamp = stamp;
        this.body = body;
    }

    public Message(Stanza stanza) {
        List values = new ByteBuffer(stanza.getContent())._string()._int()._int()._int()._byte()._long()._string().unpack();
        int idx = 0;
        this.id = (String)values.get(idx++);
        this.from = (Integer)values.get(idx++);
        this.to = (Integer)values.get(idx++);
        this.target = (Integer)values.get(idx++);
        this.type = (Byte)values.get(idx++);
        this.stamp = (Long)values.get(idx++);
        this.body = (String)values.get(idx++);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        this.setContent(new ByteBuffer()._string(this.id)._int(this.from)._int(this.to)._int(this.target)
                ._byte(this.type)._double(this.stamp)._string(this.body).pack());
        return super.toBuffer();
    }
}
