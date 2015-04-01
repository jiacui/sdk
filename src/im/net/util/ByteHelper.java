package im.net.util;

/**
 * Created by hjc on 14-12-8.
 */
public class ByteHelper {
    /**
     * 将一个单字节的byte转换成32位的int
     *
     * @param b
     *            byte
     * @return convert result
     */
    public static int unsignedByte2Int(byte b) {
        return (int) b & 0xFF;
    }

    /**
     * 将一个单字节的Byte转换成十六进制的数
     *
     * @param b
     *            byte
     * @return convert result
     */
    public static String byte2Hex(byte b) {
        int i = b & 0xFF;
        return Integer.toHexString(i);
    }

    /**
     * 将一个4byte的数组转换成32位的int
     *
     * @param buf
     *            bytes buffer
     * @param byte[]中开始转换的位置
     * @return convert result
     */
    public static long unsigned4Bytes2Int(byte[] buf, int pos) {
        int firstByte = 0;
        int secondByte = 0;
        int thirdByte = 0;
        int fourthByte = 0;
        int index = pos;
        firstByte = (0x000000FF & ((int) buf[index]));
        secondByte = (0x000000FF & ((int) buf[index + 1]));
        thirdByte = (0x000000FF & ((int) buf[index + 2]));
        fourthByte = (0x000000FF & ((int) buf[index + 3]));
        index = index + 4;
        return ((long) (firstByte << 24 | secondByte << 16 | thirdByte << 8 | fourthByte)) & 0xFFFFFFFFL;
    }

    /**
     * 将16位的short转换成byte数组
     *
     * @param s
     *            short
     * @return byte[] 长度为2
     * */
    public static byte[] short2ByteArray(short s) {
        byte[] targets = new byte[2];
        for (int i = 0; i < 2; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((s >>> offset) & 0xff);
        }
        return targets;
    }

    /**
     * 将32位整数转换成长度为4的byte数组
     *
     * @param s
     *            int
     * @return byte[]
     * */
    public static byte[] int2ByteArray(int s) {
        byte[] targets = new byte[4];
        for (int i = 0; i < 4; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((s >>> offset) & 0xff);
        }
        return targets;
    }

    /**
     * long to byte[]
     *
     * @param s
     *            long
     * @return byte[]
     * */
    public static byte[] long2ByteArray(long s) {
        byte[] targets = new byte[8];
        for (int i = 0; i < 8; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((s >>> offset) & 0xff);
        }
        return targets;
    }

    /**32位int转byte[]*/
//
//    public static byte[] int2byte(int res) {
//        byte[] targets = new byte[4];
//        targets[0] = (byte) (res & 0xff);// 最低位
//        targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
//        targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
//        targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。
//        return targets;
//    }

    /**
     * 将长度为4的byte数组转换为16位int
     *
     * @param res
     *            byte[]
     * @return int
     * */
    public static int byte2int(byte[] res) {
        return (0xff & res[3]) |
                (0xff00 & (res[2] << 8)) |
                (0xff0000 & (res[1] << 16)) |
                (0xff000000 & (res[0] << 24));
    }

    /**
     * 将长度为2的byte数组转换为8位short
     *
     * @param res
     *            byte[]
     * @return int
     * */
    public static short byte2short(byte[] bytes)
    {
        return (short) ((0xff & bytes[1]) | (0xff00 & (bytes[0] << 8)));
    }

    public static long byte2long(byte[] bytes)
    {
        return(0xffL & (long)bytes[7]) |
                (0xff00L & ((long)bytes[6] << 8)) |
                (0xff0000L & ((long)bytes[5] << 16)) |
                (0xff000000L & ((long)bytes[4] << 24)) |
                (0xff00000000L & ((long)bytes[3] << 32)) |
                (0xff0000000000L & ((long)bytes[2] << 40)) |
                (0xff000000000000L & ((long)bytes[1] << 48)) |
                (0xff00000000000000L & ((long)bytes[0] << 56));
    }

    public static float byte2float(byte[] bytes)
    {
        return Float.intBitsToFloat(ByteHelper.byte2int(bytes));
    }

    public static double byte2double(byte[] bytes)
    {
        return Double.longBitsToDouble(ByteHelper.byte2long(bytes));
    }

    /**
     * 从指定数组的copy一个子数组并返回
     *
     * @param original of type byte[] 原数组
     * @param from 起始点
     * @param to 结束点
     * @return 返回copy的数组
     */

    public static byte[] copyOfRange(byte[] original, int from, int to) {
        int len = to - from;
        byte[] copy = new byte[len];
        System.arraycopy(original, from, copy, 0, Math.min(original.length - from, len));
        return copy;
    }

    public static short readUInt16(byte[] bytes, int from, int to) {
        return ByteHelper.byte2short(ByteHelper.copyOfRange(bytes, from, to));
    }

    /* *
    * Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)
    * 来转换成16进制字符串。
    * @param src byte[] data
    * @return hex string
    */
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}