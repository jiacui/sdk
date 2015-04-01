import im.net.stanza.Auth;
import im.net.util.ByteHelper;
import org.apache.log4j.PropertyConfigurator;

import java.util.Scanner;

/**
 * Created by hjc on 14-12-7.
 */
public class Main {
    public static void main(String ar[]) {
//        PropertyConfigurator.configure("log4j.properties");
//        ByteBuffer byteBuffer = new ByteBuffer();
//        System.out.println(bytesToHexString(byteBuffer._double(123456.789)._int(123456789)._string("hello").pack()));
//        System.out.println(bytesToHexString("hello".getBytes()));
//        byte[] b = byteBuffer._double(123456.789)._int(123456789)._string("hello").pack();
//        System.out.println(new ByteBuffer(b)._double()._int()._string().unpack().get(0));
//        System.out.println(new ByteBuffer(b)._double()._int()._string().unpack().get(1));
//        System.out.println(new ByteBuffer(b)._double()._int()._string().unpack().get(2));

//          System.out.println(System.currentTimeMillis());

        System.out.println(ByteHelper.bytesToHexString(new Auth((byte) 1, 102, (byte) 1, "1", "golo/3.1", "ios/7.1").toBuffer()));

        int uid = 102;
        String accessToken = "f8bc80013616f4ea2ce5defe7e69252a";
//        System.out.println((new Auth((byte) 1, uid, (byte) 1, accessToken, "im/1.0", "android/4.0").toBuffer()));
//        System.out.println((new Auth((byte) 1, uid, (byte) 1, accessToken, "golo/3.1", "ios/7.1").toBuffer()));

        IMClient client = IMClient.getInstance();
        client.connect(uid, accessToken);

    }

}
