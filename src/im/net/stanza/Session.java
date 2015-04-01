package im.net.stanza;

import im.net.core.ByteBuffer;
import im.net.core.Stanza;

import java.util.List;

/**
 * IM 会话
 * Created by hjc on 14-12-8.
 */
public class Session extends Stanza{
    private byte step;
    private long stamp;

    public Session(byte step, long stamp) {
        this.stamp = stamp;
        this.step = step;
    }

    public Session(Stanza stanza) {
        List values = new ByteBuffer(stanza.getContent())._byte()._long().unpack();
        this.step = (Byte)values.get(0);
        this.stamp = (Long)values.get(1);
    }


    public byte getStep() {
        return step;
    }

    public void setStep(byte step) {
        this.step = step;
    }

    public long getStamp() {
        return stamp;
    }

    public void setStamp(long stamp) {
        this.stamp = stamp;
    }

    public byte[] toBuffer() {
        this.setType((short) 0x0003);
        this.setContent(new ByteBuffer()._byte(this.step)._double(this.stamp).pack());
        return super.toBuffer();
    }
}
