package im.net.core;

/**
 * Created by koujc on 14-12-7.
 */
public class Stanza {
    private short type;
    private byte[] content = new byte[0];

    public Stanza() {
    }

    public Stanza(short type, byte[] content) {
        this.type = type;
        this.content = content;
    }

    public boolean is(String type) {
        switch (this.type) {
            case 0x0001:return type == "Auth";
            case 0x0002:return type == "AuthResp";
            case 0x0003:return type == "Session";
            case 0x0011:return type == "Message";
            case 0x0022:return type == "presence";
            case 0x0033:return type == "Ping";
            case 0x0034:return type == "Conflict";
            case 0x0035:return type == "Overload";
            case 0x0041:return type == "End";
            default:return false;
        }
    }

    public void setType(short type) {
        this.type = type;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }

    public int getType() {
        return type;
    }

    public byte[] toBuffer() {
        byte[] bytes = new byte[4 + this.content.length];
        System.arraycopy(new ByteBuffer()._short(this.type)._short((short) this.content.length).pack(), 0, bytes, 0, 4);
        System.arraycopy(this.content, 0, bytes, 4, this.content.length);
        return bytes;
    }
}
