package io.mycat.bigmem.buffer.impl.directmemory;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

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
public class DirectMycatBufferImpl implements MycatMovableBufer {

    /**
     * 用来进行自己内存管理的对象
    * @字段说明 unsafe
    */
    private Unsafe unsafe;

    /**
     * 当前的编号
    * @字段说明 position
    */
    private long position;

    /**
     * 当前的的容量
    * @字段说明 limit
    */
    private long limit;

    /**
     * 地址信息
    * @字段说明 address
    */
    private long address;

    /**
     * 用来进行本地变量的存储
    * @字段说明 local
    */
    private ThreadLocal<Long> local = new ThreadLocal<>();

    /**
     * 构造方法，进行内存容量的分配操作
    * 构造方法
    * @param moneySize
    */
    public DirectMycatBufferImpl(long moneySize) {
        // // 获得首地址信息
        unsafe = UnsafeHelper.getUnsafe();
        // 进行内存分配
        address = unsafe.allocateMemory(moneySize);
        // 设置所有的内存地址都为0
        unsafe.setMemory(address, moneySize, (byte) 0);
        // 设置limit以及空量信息
        this.limit = moneySize;
        // 设置线程的默认值
        local.set(-1l);
    }

    private long getIndex(int offset) {
        if (limit - position < offset)
            throw new BufferOverflowException();
        return address + offset;
    }

    /**
     * 进行postion位置的更新
    * 方法描述
    * @param offset
    * @return
    * @创建日期 2016年12月22日
    */
    private int addPosition(int offset) {
        if (limit - position < offset)
            throw new BufferOverflowException();
        this.position = this.position + offset;
        return offset;
    }

    @Override
    public void setByte(int offset, byte value) {
        if (!this.checkThreadId()) {
            return;
        }

        unsafe.putByte(getIndex(addPosition(offset)), value);
    }

    /**
     * 进行线程的检查 
    * 方法描述
    * @return
    * @创建日期 2016年12月22日
    */
    private boolean checkThreadId() {

        if (Thread.currentThread().getId() == local.get()) {
            return true;
        }

        return false;

    }

    @Override
    public byte getByte(int offset) {

        if (!this.checkThreadId()) {
            return 0;
        }
        // 仅允许同一线程操作
        return unsafe.getByte(getIndex(offset));
    }

    @Override
    public void copyTo(ByteBuffer buffer) {

        if (!this.checkThreadId()) {
            return;
        }

        if (buffer.capacity() < this.limit) {
            throw new BufferOverflowException();
        }
        // 获取当前堆外的内存的地址
        long buffAddress = ((sun.nio.ch.DirectBuffer) buffer).address();
        // 进行内存的拷贝
        unsafe.copyMemory(null, address, null, buffAddress, this.limit);
    }

    @Override
    public void copyTo(MycatMovableBufer buffer) {

        if (!this.checkThreadId()) {
            return;
        }

        if (buffer.capacity() < this.position) {
            throw new BufferOverflowException();
        }
        // 获取当前堆外的内存的地址
        long buffAddress = buffer.getAddress();
        // 进行内存的拷贝,仅拷贝已经写入的数据
        unsafe.copyMemory(null, address, null, buffAddress, this.position);
    }

    @Override
    public void recycleUnuse() {
        if (!this.checkThreadId()) {
            return;
        }
        // unsafe.freeMemory(getIndex(this.position));
        // 修改当前的标识
        this.limit = this.position;
    }

    @Override
    public void beginOp() {
        // 写入当前的线程id的标识
        local.set(Thread.currentThread().getId());
    }

    @Override
    public void commitOp() {
        // 进行数据提交
        local.set(-1l);

    }

    public static void main(String[] args) {
        final DirectMycatBufferImpl mybuffer = new DirectMycatBufferImpl(1024);

        mybuffer.beginOp();

        mybuffer.setByte(0, (byte) 0);
        mybuffer.setByte(1, (byte) 1);
        mybuffer.setByte(2, (byte) 2);
        mybuffer.setByte(3, (byte) 3);
        mybuffer.setByte(4, (byte) 4);
        mybuffer.setByte(4, (byte) 25);

        for (int i = 0; i < 5; i++) {
            System.out.println(mybuffer.getByte(i));
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
    public long capacity() {
        return this.limit;
    }

    @Override
    public long position() {
        return this.position;
    }

    @Override
    public void capacity(long capacity) {
        this.limit = capacity;
    }

    @Override
    public void position(long position) {
        this.position = position;
    }

    @Override
    public long getAddress() {
        // TODO Auto-generated method stub
        return this.address;
    }

}
