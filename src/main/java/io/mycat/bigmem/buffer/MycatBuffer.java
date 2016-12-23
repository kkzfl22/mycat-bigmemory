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
     * 按偏移量设置buffer的值，进行绝对定位数据进行写入，不改变position的值
    * 方法描述
    * @param offset
    * @param value
    * @创建日期 2016年12月21日
    */
    public void setByte(int offset, byte value);

    /**
     * 放入byte信息
    * 方法描述
    * @param b
    * @return
    * @创建日期 2016年12月23日
    */
    public MycatBuffer putByte(byte b);

    /**
     * 获取指定位置的byte值
    * 方法描述
    * @param offset
    * @return
    * @创建日期 2016年12月23日
    */
    public byte getByte(int offset);

    /**
     * 按位获取数据
     * 方法描述
     * @param b
     * @return
     * @创建日期 2016年12月23日
     */
    public byte get();

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
    public int limit();

    /**
     * 设置容量信息
     * 方法描述
     * @return
     * @创建日期 2016年12月22日
     */
    public void limit(int limit);

    /**
     * 获得当前写入指针的位置
    * 方法描述
    * @return
    * @创建日期 2016年12月22日
    */
    public int putPosition();

    /**
     * 重新定位位置
     * 方法描述
     * @return
     * @创建日期 2016年12月22日
     */
    public void putPosition(int putPosition);

    /**
     * 获得当前读取指针的位置
     * 方法描述
     * @return
     * @创建日期 2016年12月22日
     */
    public int getPosition();

    /**
     * 重新定位读取指针位置
     * 方法描述
     * @return
     * @创建日期 2016年12月22日
     */
    public void getPosition(int getPosition);

    /**
     * 获取容量信息
    * 方法描述
    * @return
    * @创建日期 2016年12月23日
    */
    public int capacity();

    /**
     * 以当前的内存生成新的引用对象,使用读取指针定位
    * 方法描述
    * @param buffer
    * @创建日期 2016年12月21日
    */
    public MycatBuffer slice();

}
