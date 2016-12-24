package io.mycat.bigmem.buffer.impl;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import io.mycat.bigmem.buffer.DirectMemAddressInf;
import io.mycat.bigmem.buffer.MycatBuffer;
import io.mycat.bigmem.buffer.MycatMovableBufer;
import io.mycat.bigmem.util.UnsafeHelper;
import sun.misc.Unsafe;

/**
 * 进行直接内存的操作
* 源文件名：DirectMycatBufferImpl.java
* 文件版本：1.0.0
* 创建作者：liujun
* 创建日期：2016年12月22日
* 修改作者：liujun
* 修改日期：2016年12月22日
* 文件描述：TODO
* 版权所有：Copyright 2016 zjhz, Inc. All Rights Reserved.
*/
public class DirectMycatBufferImpl implements MycatMovableBufer, DirectMemAddressInf {

    /**
     * 用来进行自己内存管理的对象
    * @字段说明 unsafe
    */
    private Unsafe unsafe;

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

    /**
     * 是否进行内存整理标识,默认为true，即允许进行整理
    * @字段说明 clearFlag
    */
    private volatile boolean clearFlag = true;

    /**
     * 当前附着的对象
    * @字段说明 att
    */
    private Object att;

    /**
     * 构造方法，进行内存容量的分配操作
    * 构造方法
    * @param moneySize
    */
    public DirectMycatBufferImpl(int moneySize) {
        // // 获得首地址信息
        unsafe = UnsafeHelper.getUnsafe();
        // 进行内存分配
        address = unsafe.allocateMemory(moneySize);
        // 设置所有的内存地址都为0
        unsafe.setMemory(address, moneySize, (byte) 0);
        // 设置limit以及空量信息
        this.limit = moneySize;
        // 设置容量
        this.capacity = moneySize;
    }

    public DirectMycatBufferImpl(DirectMycatBufferImpl dirbuffer, int position, int limit, long address) {
        this.putPosition = position;
        this.limit = limit;
        // 设置容量
        this.capacity = limit;
        this.address = address;
        this.att = dirbuffer;
        this.unsafe = dirbuffer.unsafe;
    }

    private long getIndex(long offset) {
        if (limit < offset)
            throw new BufferOverflowException();
        return address + offset;
    }

    @Override
    public void setByte(int offset, byte value) {
        unsafe.putByte(getIndex(offset), value);
    }

    @Override
    public byte getByte(int offset) {
        // 仅允许同一线程操作
        return unsafe.getByte(getIndex(offset));
    }

    @Override
    public void copyTo(ByteBuffer buffer) {

        if (buffer.capacity() < this.limit) {
            throw new BufferOverflowException();
        }
        // 获取当前堆外的内存的地址
        long buffAddress = ((sun.nio.ch.DirectBuffer) buffer).address();
        // 进行内存的拷贝
        unsafe.copyMemory(null, address, null, buffAddress, this.limit);
    }

    @Override
    public void recycleUnuse() {
        // unsafe.freeMemory(getIndex(this.position));
        // 修改当前的标识
        this.limit = this.putPosition;
    }

    @Override
    public void beginOp() {

        // 标识当前正在进行内存操作，不能整理内存
        clearFlag = false;
    }

    @Override
    public void commitOp() {
        // 内存整理完毕可以进行内存整理
        clearFlag = true;
    }

    public static void main(String[] args) {
        final DirectMycatBufferImpl mybuffer = new DirectMycatBufferImpl(1024);

        mybuffer.beginOp();

        mybuffer.putByte((byte) 10);
        mybuffer.putByte((byte) 12);

        for (int i = 0; i < 2; i++) {
            System.out.println(mybuffer.get());
        }

        ByteBuffer bufferValue = ByteBuffer.allocateDirect(1024);

        mybuffer.copyTo(bufferValue);

        System.out.println(bufferValue);

        for (int i = 0; i < 5; i++) {
            System.out.println(bufferValue.get(i));
        }

        // 进行内存的翻译
        mybuffer.recycleUnuse();
        // 进行存储
        mybuffer.setByte(5, (byte) 9);

        mybuffer.commitOp();

        new Thread(new Runnable() {

            @Override
            public void run() {
                mybuffer.beginOp();

                mybuffer.setByte(0, (byte) 8);
                mybuffer.setByte(1, (byte) 9);

                for (int i = 0; i < 2; i++) {
                    System.out.println(mybuffer.getByte(i));
                }

                mybuffer.commitOp();
            }
        }).start();

    }

    @Override
    public int limit() {
        return this.limit;
    }

    @Override
    public int putPosition() {
        return this.putPosition;
    }

    @Override
    public void limit(int limit) {
        this.limit = limit;
    }

    @Override
    public void putPosition(int position) {
        this.putPosition = position;
    }

    @Override
    public MycatMovableBufer slice() {
        int currPosition = this.getPosition;
        int cap = this.limit - currPosition;
        long address = this.address + currPosition;
        // 生新新的引用对象
        return new DirectMycatBufferImpl(this, 0, cap, address);
    }

    @Override
    public int capacity() {
        return this.capacity;
    }

    @Override
    public long address() {
        return this.address;
    }

    @Override
    public Object getAttach() {
        return att;
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
    public MycatBuffer putByte(byte b) {

        unsafe.putByte(getIndex(this.addPutPos()), b);

        return this;
    }

    @Override
    public byte get() {

        return unsafe.getByte(getIndex(this.addGetPos()));

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
    public boolean getClearFlag() {
        return this.clearFlag;
    }

}
