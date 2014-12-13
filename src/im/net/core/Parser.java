package im.net.core;

import im.net.listener.StanzaListener;
import im.net.util.ByteHelper;
import org.apache.log4j.Logger;

/**
 * Created by koujc on 14-12-7.
 */
public class Parser {
    private StanzaListener stanzaListener;
    private byte[] byteParsed = new byte[0];
    private short type;
    private int minStanzaLength = 4;

    private static final Logger log = Logger.getLogger(Connection.class);

    public Parser(StanzaListener stanzaListener) {
        this.stanzaListener = stanzaListener;
    }

    public void write(byte[] bytes) {
        byte[] buf = new byte[bytes.length + this.byteParsed.length];
        System.arraycopy(this.byteParsed, 0, buf, 0, this.byteParsed.length);
        System.arraycopy(bytes, 0, buf, this.byteParsed.length, bytes.length);

        while(buf.length >= this.minStanzaLength) {
            if(this.type == 0) {
                this.type = ByteHelper.readUInt16(buf, 0, 2);
                this.minStanzaLength = ByteHelper.readUInt16(buf, 2, 4) + 4;
            }

            if(this.type > 0) {
                if(buf.length < this.minStanzaLength){
                    break;
                }

                Stanza stanza = new Stanza(this.type, ByteHelper.copyOfRange(buf, 4, this.minStanzaLength));
                this.stanzaListener.onStanza(stanza);

//                log.debug("type:" + this.type + ", content:" + ByteHelper.bytesToHexString(stanza.getContent()));

                if(buf.length >= this.minStanzaLength) {
                    this.byteParsed = new byte[buf.length - this.minStanzaLength];
                    System.arraycopy(buf, this.minStanzaLength, this.byteParsed, 0, buf.length - this.minStanzaLength);
                    buf = this.byteParsed;
                }

                this.minStanzaLength = 4;
                this.type = 0;
            }
        }

    }

    public void setStanzaListener(StanzaListener stanzaListener) {
        this.stanzaListener = stanzaListener;
    }
}