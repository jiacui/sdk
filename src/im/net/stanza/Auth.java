package im.net.stanza;

import im.net.core.ByteBuffer;
import im.net.core.Stanza;

/**
 * 验证请求
 * Created by hjc on 14-12-8.
 */
public class Auth extends Stanza {
    private byte protocolVersion;
    private int uid;
    private byte resource;
    private String auth;
    private String appVersion;
    private String osVersion;

    public Auth(Stanza stanza) {

    }

    public Auth(byte protocolVersion, int uid, byte resource, String auth, String appVersion, String osVersion) {
        this.protocolVersion = protocolVersion;
        this.uid = uid;
        this.resource = resource;
        this.auth = auth;
        this.appVersion = appVersion;
        this.osVersion = osVersion;
    }

    public byte getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(byte protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public byte getResource() {
        return resource;
    }

    public void setResource(byte resource) {
        this.resource = resource;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public byte[] toBuffer() {
        this.setType((short) 0x0001);
        this.setContent(new ByteBuffer()._byte(this.protocolVersion)._int(this.uid)._byte(this.resource)
                ._string(this.auth)._string(this.appVersion)._string(this.osVersion).pack());
        return super.toBuffer();
    }
}
