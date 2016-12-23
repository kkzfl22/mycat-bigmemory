package io.mycat.bigmem.buffer.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import io.mycat.bigmem.buffer.MyCatCallbackInf;
import io.mycat.bigmem.buffer.MycatBuffer;
import io.mycat.bigmem.buffer.MycatSwapBufer;

public class MapFileBufferImp implements MycatBuffer, MycatSwapBufer {

    /**
     * 文件缓存块管理
    * @字段说明 file
    */
    private FileInputStream file;

    /**
     * 文件通道信息
    * @字段说明 channel
    */
    private FileChannel channel;

    /**
    * @字段说明 mappedByte
    */
    private MappedByteBuffer mappedByte;

    /**
     * 当前写入的指针位置
    * @字段说明 position
    */
    private int putPosition;

    /**
     * 当前读取指针的位置
    * @字段说明 getPosition
    */
    private int getPosition;

    /**
     * 当前的的容量
    * @字段说明 limit
    */
    private int limit;

    /**
     * 容量信息
    * @字段说明 capacity
    */
    private int capacity;

    /**
     * 地址信息
    * @字段说明 address
    */
    private long address;

    public MapFileBufferImp(long size) {
        String path = MapFileBufferImp.class.getClassLoader().getResource("/mapfile").getPath();

        String fileName = path + "/mapFile-" + System.nanoTime() + ".txt";

        try {
            file = new FileInputStream(fileName);
            channel = file.getChannel();

            mappedByte = channel.map(MapMode.PRIVATE, 0, size);

            // 设置position

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void swapln() {

    }

    @Override
    public void swapOut() {

    }

    @Override
    public void swapIn(MyCatCallbackInf notify) {
    }

    @Override
    public void swapOut(MyCatCallbackInf notify) {

    }

    @Override
    public void beginOp() {
        // TODO Auto-generated method stub

    }

    @Override
    public void commitOp() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setByte(int offset, byte value) {
        // TODO Auto-generated method stub

    }

    @Override
    public MycatBuffer putByte(byte b) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public byte getByte(int offset) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public byte get() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void copyTo(ByteBuffer buffer) {
        // TODO Auto-generated method stub

    }

    @Override
    public void recycleUnuse() {
        // TODO Auto-generated method stub

    }

    @Override
    public int limit() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void limit(int limit) {
        // TODO Auto-generated method stub

    }

    @Override
    public int putPosition() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void putPosition(int putPosition) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getPosition() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void getPosition(int getPosition) {
        // TODO Auto-generated method stub

    }

    @Override
    public int capacity() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public MycatBuffer slice() {
        // TODO Auto-generated method stub
        return null;
    }

}
