package io.mycat.bigmem.buffer.impl;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import io.mycat.bigmem.buffer.DirectMemAddressInf;
import io.mycat.bigmem.buffer.MyCatCallbackInf;
import io.mycat.bigmem.buffer.MycatBuffer;
import io.mycat.bigmem.buffer.MycatSwapBufer;
import io.mycat.bigmem.util.UnsafeHelper;
import sun.misc.Unsafe;
import sun.nio.ch.FileChannelImpl;

/**
 * 文件映射的buffer的实现
* 源文件名：MapFileBufferImp.java
* 文件版本：1.0.0
* 创建作者：Think
* 创建日期：2016年12月25日
* 修改作者：Think
* 修改日期：2016年12月25日
* 文件描述：TODO
* 版权所有：Copyright 2016 zjhz, Inc. All Rights Reserved.
*/
@SuppressWarnings("restriction")
public class MapFileBufferImp implements MycatSwapBufer, DirectMemAddressInf {

    /**
     * 内存控制的对象信息 
    * @字段说明 unsafe
    */
    public static Unsafe unsafe;

    /**
     * 获得内存映射的方法
    * @字段说明 mmap
    */
    public static final Method mmap;

    // /**
    // * 解除映射的方法
    // * @字段说明 unmmap
    // */
    // public static final Method unmmap;

    /**
     * byte的内存的固定的偏移
    * @字段说明 BYTE_ARRAY_OFFSET
    */
    public static final int BYTE_ARRAY_OFFSET;

    /**
     * 内存映射地址信息
    * @字段说明 addr
    */
    private final long addr;

    /**
     * 随机文件读写信息
    * @字段说明 randomFile
    */
    private RandomAccessFile randomFile;

    /**
     * 文件通道信息
    * @字段说明 channel2
    */
    private FileChannel channel;

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
     * 当前附着的对象
    * @字段说明 att
    */
    private Object att;

    static {
        try {
            Field singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
            singleoneInstanceField.setAccessible(true);
            unsafe = (Unsafe) singleoneInstanceField.get(null);
            mmap = getMethod(FileChannelImpl.class, "map0", int.class, long.class, long.class);
            mmap.setAccessible(true);

            // unmmap = getMethod(FileChannelImpl.class, "unmap0", long.class,
            // long.class);
            // unmmap.setAccessible(true);
            BYTE_ARRAY_OFFSET = unsafe.arrayBaseOffset(byte[].class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public MapFileBufferImp(int size) throws IOException {
        String path = MapFileBufferImp.class.getClassLoader().getResource("mapfile").getPath();

        String fileName = path + "/mapFile-" + System.nanoTime() + ".txt";

        // // 获得首地址信息
        unsafe = UnsafeHelper.getUnsafe();

        randomFile = new RandomAccessFile(fileName, "rw");

        // 设置文件大小
        randomFile.setLength(size);
        channel = randomFile.getChannel();

        // 获得内存映射的地地址
        try {
            addr = (long) mmap.invoke(channel, 1, 0, size);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IOException(e);
        }

        // 设置容量相关的东西
        this.limit = size;
        this.capacity = size;
    }

    public MapFileBufferImp(MapFileBufferImp dirbuffer, int position, int limit, long address) {
        this.putPosition = position;
        this.limit = limit;
        // 设置容量
        this.capacity = limit;
        this.addr = address;
        this.att = dirbuffer;
    }

    /**
     * 获取写入的索引编号
    * 方法描述
    * @param offset
    * @return
    * @创建日期 2016年12月24日
    */
    private long getIndex(long offset) {
        if (limit < offset)
            throw new BufferOverflowException();
        return addr + offset;
    }

    /**
     * 将添加的指针加1
    * 方法描述
    * @return
    * @创建日期 2016年12月23日
    */
    private long addPutPos() {
        if (this.putPosition > this.limit)
            throw new BufferOverflowException();
        return this.putPosition++;
    }

    /**
     * 将获取的指针加1
    * 方法描述
    * @return
    * @创建日期 2016年12月23日
    */
    private long addGetPos() {
        if (this.getPosition > this.limit)
            throw new BufferOverflowException();
        return this.getPosition++;
    }

    @Override
    public void setByte(int offset, byte value) throws IOException {
        // 获取文件的游标
        int position = (int) channel.position();
        // 进行内存数据写入
        unsafe.putByte(addr + offset + position, value);
    }

    @Override
    public MycatBuffer putByte(byte b) throws IOException {
        // 获取文件的游标
        int filePosition = (int) channel.position();
        // 计算写入的游标
        long currPostision = addPutPos();
        // 进行内存数据写入
        unsafe.putByte(addr + currPostision, b);
        // 将新的文件游标写入到文件中,当前为写入单byte文件
        channel.position(filePosition + 1);

        return this;
    }

    @Override
    public byte getByte(int offset) {
        return unsafe.getByte(getIndex(offset));
    }

    @Override
    public byte get() {
        return unsafe.getByte(getIndex(addGetPos()));
    }

    @Override
    public void copyTo(ByteBuffer buffer) {
        if (buffer.capacity() < this.limit) {
            throw new BufferOverflowException();
        }
        // 获取当前堆外的内存的地址
        long buffAddress = ((sun.nio.ch.DirectBuffer) buffer).address();
        // 进行内存的拷贝
        unsafe.copyMemory(null, addr, null, buffAddress, this.limit);
    }

    @Override
    public void recycleUnuse() {
        this.limit(this.putPosition);

        try {
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int limit() {
        return this.limit;
    }

    @Override
    public void limit(int limit) {
        this.limit = limit;
    }

    @Override
    public int putPosition() {
        return this.putPosition;
    }

    @Override
    public void putPosition(int putPosition) {
        this.putPosition = putPosition;
    }

    @Override
    public int getPosition() {
        return this.getPosition;
    }

    @Override
    public void getPosition(int getPosition) {
        this.getPosition = getPosition;
    }

    @Override
    public int capacity() {
        return this.capacity;
    }

    @Override
    public MycatBuffer slice() {

        int currPosition = this.getPosition;
        int cap = this.limit - currPosition;
        long address = this.addr + currPosition;
        // 生新新的引用对象
        return new MapFileBufferImp(this, 0, cap, address);
    }

    private static Method getMethod(Class<?> cls, String name, Class<?>... params) throws Exception {
        Method m = cls.getDeclaredMethod(name, params);
        m.setAccessible(true);
        return m;
    }

    @Override
    public long address() {
        return this.addr;
    }

    @Override
    public Object getAttach() {
        return this.att;
    }

    public static void main(String[] args) throws IOException {
        final MapFileBufferImp mybuffer = new MapFileBufferImp(1024);

        mybuffer.putByte((byte) 10);
        mybuffer.putByte((byte) 12);
        mybuffer.putByte((byte) 120);
        mybuffer.putByte((byte) 100);
        mybuffer.putByte((byte) 90);

        for (int i = 0; i < mybuffer.putPosition; i++) {
            System.out.println(mybuffer.get());
        }

        System.out.println("当前写入的游标：" + mybuffer.putPosition);
        System.out.println("当前读取的游标：" + mybuffer.getPosition);

        ByteBuffer bufferValue = ByteBuffer.allocateDirect(1024);

        mybuffer.copyTo(bufferValue);

        System.out.println(bufferValue);

        for (int i = 0; i < bufferValue.position(); i++) {
            System.out.println(bufferValue.get(i));
        }

        // 进行内存的翻译
        mybuffer.recycleUnuse();
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

}
