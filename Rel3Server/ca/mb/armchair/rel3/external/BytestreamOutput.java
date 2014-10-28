/*
 * BytestreamOutput.java
 *
 * Based, in part, on components of SleepyCat's Java Berkeley DB.
 *
 * Created on 24 April 2004, 19:24
 */

package ca.mb.armchair.rel3.external;

/**
 * A class to support streaming ValueS into streams of bytes.
 *
 * @author  dave
 */
public abstract class BytestreamOutput {

    /** Output a byte. */
    public abstract void put(int b);

    /**
     * Write an unsigned byte.
     */
    private final void putUnsignedByte(int val) {
        put(val);
    }
  
    /** Output an array of bytes. */
    public void put(byte[] b) {
        for (int i=0; i<b.length; i++)
            putUnsignedByte(b[i]);
    }
    
    /** Output a subsection of an array of bytes. */
    public void put(byte[] b, int offset, int length) {
        int count = 0;
        for (int i=offset; i<b.length && count++ < length; i++)
            putUnsignedByte(b[i]);
    }
    
    /**
     * Writes an unsigned short.
     */
    private final void putUnsignedShort(short val) {
        putUnsignedByte((byte) (val >>> 8));
        putUnsignedByte((byte) val);
    }

    /**
     * Writes an unsigned int 
     */
    private final void putUnsignedInt(int val) {
        putUnsignedByte((byte) (val >>> 24));
        putUnsignedByte((byte) (val >>> 16));
        putUnsignedByte((byte) (val >>> 8));
        putUnsignedByte((byte) val);
    }

    /** Output unsigned long */
    private final void putUnsignedLong(long val) {
        putUnsignedByte((byte) (val >>> 56));
        putUnsignedByte((byte) (val >>> 48));
        putUnsignedByte((byte) (val >>> 40));
        putUnsignedByte((byte) (val >>> 32));
        putUnsignedByte((byte) (val >>> 24));
        putUnsignedByte((byte) (val >>> 16));
        putUnsignedByte((byte) (val >>> 8));
        putUnsignedByte((byte) val);    
    }
        
    /**
     * Write a String.
     */
    public final void putString(String val) {
        put(val.getBytes());
        putUnsignedByte((byte)0);
    }

    /**
     * Write an array of bytes.
     */
    public final void putbytes(byte[] b) {
        putInt(b.length);
        put(b);
    }
    
    /**
     * Write a char.
     */
    public final void putChar(char val) {
        putUnsignedByte((byte) (val >>> 8));
        putUnsignedByte((byte) val);
    }

    /**
     * Write a Character.
     */
    public final void putChar(Character val) {
        putChar(val.charValue());
    }
    
    /**
     * Write a boolean.
     */
    public final void putBoolean(boolean val) {
        putUnsignedByte((byte)(val ? 1 : 0));
    }

    /**
     * Write a Boolean.
     */
    public final void putBoolean(Boolean val) {
        putBoolean(val.booleanValue());
    }
    
    /**
     * Writes a byte.
     */
    public final void putByte(int b) {
        if (b < 0)
            b &= (byte) ~0x80;
        else
            b |= (byte) 0x80;
        putUnsignedByte(b);
    }

    /**
     * Writes a Byte.
     */
    public final void putByte(Byte b) {
        putByte(b.byteValue());
    }
    
    /**
     * Writes a short.
     */
    public final void putShort(int s) {
        if (s < 0)
            s &= (short) ~0x8000;
        else
            s |= (short) 0x8000;
        putUnsignedShort((short)s);
    }

    /**
     * Writes a Short.
     */
    public final void putShort(Short s) {
        putShort(s.shortValue());
    }
    
    /**
     * Writes an int.
     */
    public final void putInt(long val) {
        if (val < 0)
            val &= ~0x80000000;
        else
            val |= 0x80000000;
        putUnsignedInt((int)val);
    }

    /**
     * Writes an Integer.
     */
    public final void putInteger(Integer i) {
        putInt(i.intValue());
    }
    
    /**
     * Writes a long.
     */
    public final void putLong(long val) {
        if (val < 0)
            val &= ~0x8000000000000000L;
        else
            val |= 0x8000000000000000L;
        putUnsignedLong(val);
    }

    /**
     * Writes a Long.
     */
    public final void putLong(Long val) {
        putLong(val.longValue());
    }
    
    /**
     * Writes a float.
     */
    public final void putFloat(float val) {
        putUnsignedInt(Float.floatToIntBits(val));
    }

    /**
     * Writes a Float.
     */
    public final void putFloat(Float val) {
        putFloat(val.floatValue());
    }
    
    /**
     * Writes a double.
     */
    public final void putDouble(double val) {
        putUnsignedLong(Double.doubleToLongBits(val));
    }
    
    /**
     * Writes a Double.
     */
    public final void putDouble(Double val) {
        putDouble(val.doubleValue());
    }
}