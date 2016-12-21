package io.mycat.bigmem.buffer;

/**
 * 允许交换到磁盘的buffer
 * 
 * 内存可以被管理器移动，可以被交换到磁盘里
 * 
 * swapIn()与swapOut(CallBack）类似beginOp与commitOp操作，
 * 但多了内存加载与钝化的逻辑，客户端如果不想阻塞，
 * 则可以显示的调用swapIn(Callback notify);
 * 当服务端完成后，回调客户端，客户端后面可以发起正常操作 
 * 
 * 
* 源文件名：MycatSwapBufer.java
* 文件版本：1.0.0
* 创建作者：Think
* 创建日期：2016年12月21日
* 修改作者：Think
* 修改日期：2016年12月21日
* 文件描述：TODO
* 版权所有：Copyright 2016 zjhz, Inc. All Rights Reserved.
*/
public interface MycatSwapBufer extends MycatBuffer {

    /**
     * 与beginOp操作类型，标识开始
    * 方法描述
    * @创建日期 2016年12月21日
    */
    public void swapln();

    /**
     * 与commitOp操作类型，标识结束
    * 方法描述
    * @创建日期 2016年12月21日
    */
    public void swapOut();

    /**
     * 进行操作标识，完成调用回调通知函数
    * 方法描述
    * @param notify
    * @创建日期 2016年12月22日
    */
    public void swapIn(MyCatCallbackInf notify);

    /**
     *  与commitOp操作类型，标识结束,完成调用回调通知函数
    * 方法描述
    * @param notify
    * @创建日期 2016年12月22日
    */
    public void swapOut(MyCatCallbackInf notify);

}
