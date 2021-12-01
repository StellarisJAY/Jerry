package com.jay.jerry.http.nio.common;

/**
 * <p>
 *  动态Byte数组
 * </p>
 *
 * @author Jay
 * @date 2021/12/1
 **/
public class AppendableByteArray {
    private byte[] array;
    private int pos;

    public AppendableByteArray() {
        array = new byte[16];
        pos = 0;
    }

    public void append(byte b){
        if(pos == array.length){
            byte[] old = array;
            // 扩容，每次扩容1倍
            array = new byte[array.length << 1];
            System.arraycopy(old, 0, array, 0, old.length);
        }
        array[pos++] = b;
    }

    public int size(){
        return pos;
    }

    public int capacity(){
        return array.length;
    }

    public byte[] array(){
        return array;
    }

    public AppendableByteArray clear(){
        array = new byte[0];
        pos = 0;
        return this;
    }
}
