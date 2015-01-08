/*
 * BytestreamOutputArray.java
 *
 * Created on 25 April 2004, 02:25
 */

package org.reldb.rel.external;

/**
 * A BytestreamOutput backed by an array of bytes.
 *
 * @author  dave
 */
public class BytestreamOutputArray extends BytestreamOutput {

    private final static int minimumCapacity = 1024;
    private byte[] vb = new byte[minimumCapacity];
    private int index = 0;
    
    public void reset() {
        index = 0;
    }
    
    /** Get the array of bytes that represents the stream. */
    public byte[] getBytes() {
        byte outArray[] = new byte[index];
        System.arraycopy(vb, 0, outArray, 0, index);
        return outArray;
    }
   
    public void put(int b) {
        if (index + 1 > vb.length) {
            int newCapacity = (vb.length + 1) * 2;
            if (newCapacity < 0) {
                newCapacity = Integer.MAX_VALUE;
            } else if (minimumCapacity > newCapacity) {
                newCapacity = minimumCapacity;
            }
            byte newValue[] = new byte[newCapacity];
            System.arraycopy(vb, 0, newValue, 0, index);
            vb = newValue;
        }
        vb[index++] = (byte)b;
    }
    
}
