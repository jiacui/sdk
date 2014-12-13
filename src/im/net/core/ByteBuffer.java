package im.net.core;

import im.net.util.ByteHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koujc on 14-12-7.
 */
public class ByteBuffer {
    private List<String> types;
    private List values;
    private byte[] content = new byte[0];

    public ByteBuffer() {
        this.types = new ArrayList();
        this.values = new ArrayList();
    }

    public ByteBuffer(byte[] content) {
        this.types = new ArrayList();
        this.values = new ArrayList();
        this.content = content;
    }

    public ByteBuffer _byte(byte b) {
        this.types.add("byte");
        this.values.add(b);
        return this;
    }

    public ByteBuffer _short(short s) {
        this.types.add("short");
        this.values.add(s);
        return this;
    }
    public ByteBuffer _int(int i) {
        this.types.add("int");
        this.values.add(i);
        return this;
    }
    public ByteBuffer _float(float f) {
        this.types.add("flaot");
        this.values.add(f);
        return this;
    }
    public ByteBuffer _long(long d) {
        this.types.add("long");
        this.values.add(d);
        return this;
    }
    public ByteBuffer _double(double d) {
        this.types.add("double");
        this.values.add(d);
        return this;
    }
    public ByteBuffer _string(String s) {
        this.types.add("string");
        this.values.add(s);
        return this;
    }

    public ByteBuffer _byte() {
        this.types.add("byte");
        return this;
    }

    public ByteBuffer _short() {
        this.types.add("short");
        return this;
    }
    public ByteBuffer _int() {
        this.types.add("int");
        return this;
    }
    public ByteBuffer _float() {
        this.types.add("flaot");
        return this;
    }
    public ByteBuffer _double() {
        this.types.add("double");
        return this;
    }
    public ByteBuffer _long() {
        this.types.add("long");
        return this;
    }
    public ByteBuffer _string() {
        this.types.add("string");
        return this;
    }


    public byte[] pack() {
        byte[] bytes = new byte[1024];
        int off = 0;
        List<String> types = this.types;
        List values = this.values;

        for(int i=0; i<types.size(); i++) {
            String key = types.get(i);
            if(key == "byte") {
                bytes[off] = ((Byte)values.get(i)).byteValue();
//                System.arraycopy((byte[])values.get(i), 0, bytes, off, 1);
                off++;
            }
            if(key == "short") {
                System.arraycopy(ByteHelper.short2ByteArray((Short) values.get(i)), 0, bytes, off, 2);
                off += 2;
            }
            if(key == "int") {
                System.arraycopy(ByteHelper.int2ByteArray((Integer) values.get(i)), 0, bytes, off, 4);
                off += 4;
            }
            if(key == "float") {
                System.arraycopy(ByteHelper.int2ByteArray(Float.floatToIntBits((Float) values.get(i))), 0, bytes, off, 4);
                off += 4;
            }
            if(key == "double") {
                System.arraycopy(ByteHelper.long2ByteArray(Double.doubleToLongBits((Double) values.get(i))), 0, bytes, off, 8);
                off += 8;
            }
            if(key == "long") {
                System.arraycopy(ByteHelper.long2ByteArray((Long) values.get(i)), 0, bytes, off, 8);
                off += 8;
            }
            if(key == "string") {
                String val = (String) values.get(i);
                System.arraycopy(ByteHelper.short2ByteArray((short) val.length()), 0, bytes, off, 2);
                off += 2;
//                System.out.print(val.getBytes());
                System.arraycopy(val.getBytes(), 0, bytes, off, val.getBytes().length);
                off += val.getBytes().length;
            }
        }

//        System.out.print(off);
        byte[] result = new byte[off];
        System.arraycopy(bytes, 0, result, 0, off);
        return result;
    }

    public List unpack() {
        int off = 0;
        for(int i=0; i<this.types.size(); i++) {
            String key = this.types.get(i);
            if(key == "byte") {
                this.values.add(this.content[off]);
                off+=1;
            }
            if(key == "short") {
                byte[] b = new byte[2];
                System.arraycopy(this.content, off, b, 0, 2);
                this.values.add(ByteHelper.byte2short(b));
                off+=2;
            }
            if(key == "int") {
                byte[] b = new byte[4];
                System.arraycopy(this.content, off, b, 0, 4);
                this.values.add(ByteHelper.byte2int(b));
                off+=4;
//                System.out.println(off);
            }
            if(key == "float") {
                byte[] b = new byte[4];
                System.arraycopy(this.content, off, b, 0, 4);
                this.values.add(ByteHelper.byte2float(b));
                off+=4;
            }
            if(key == "double") {
                byte[] b = new byte[8];
                System.arraycopy(this.content, off, b, 0, 8);
                this.values.add(ByteHelper.byte2double(b));
                off+=8;
            }
            if(key == "long") {
                byte[] b = new byte[8];
                System.arraycopy(this.content, off, b, 0, 8);
                this.values.add(ByteHelper.byte2long(b));
                off+=8;
            }
            if(key == "string") {
                byte[] b = new byte[2];
                System.arraycopy(this.content, off, b, 0, 2);
                short len = ByteHelper.byte2short(b);
                off+=2;
                b = new byte[len];
//                System.out.print(len);
                System.arraycopy(this.content, off, b, 0, len);
                this.values.add(new String(b));
                off+=len;
            }

        }

        return this.values;
    }
}
