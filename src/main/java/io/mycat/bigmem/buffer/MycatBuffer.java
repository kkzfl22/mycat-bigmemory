package io.mycat.bigmem.buffer;

import java.nio.ByteBuffer;

/**
 * buffer接口，类似byteBuffer接口,
* 源文件名：MycatBufer.java
* 文件版本：1.0.0
* 创建作者：Think
* 创建日期：2016年12月21日
* 修改作者：Think
* 修改日期：2016年12月21日
* 文件描述：TODO
* 版权所有：Copyright 2016 zjhz, Inc. All Rights Reserved.
*/
public interface MycatBuffer {

    /**
     * 标识起始buffer
    * 方法描述
    * @创建日期 2016年12月21日
    */
    public void beginOp();

    /**
     * 提交buffer
    * 方法描述
    * @创建日期 2016年12月21日
    */
    public void commitOp();

    /**
     * 按偏移量设置buffer的值
    * 方法描述
    * @param offset
    * @param value
    * @创建日期 2016年12月21日
    */
    public void setByte(int offset, byte value);

    /**
     * 设置偏移量位置的值
    * 方法描述
    * @param offset
    * @return
    * @创建日期 2016年12月21日
    */
    public byte getByte(int offset);

    /**
     * 向目标的buffer拷贝数据
    * 方法描述
    * @param buffer
    * @创建日期 2016年12月21日
    */
    public void copyTo(ByteBuffer buffer);

    /**
     * 释放未使用的空间
    * 方法描述
    * @创建日期 2016年12月21日
    */
    public void recycleUnuse();

    /**
     * 获取容量信息
    * 方法描述
    * @return
    * @创建日期 2016年12月22日
    */
    public long capacity();

    /**
     * 当前写入的位置
    * 方法描述
    * @return
    * @创建日期 2016年12月22日
    */
    public long position();

    /**
     * 设置容量信息
     * 方法描述
     * @return
     * @创建日期 2016年12月22日
     */
    public void capacity(long capacity);

    /**
     * 重新定位位置
     * 方法描述
     * @return
     * @创建日期 2016年12月22日
     */
    public void position(long position);

}
