package io.mycat.bigmem.buffer;

/**
 * 可以被移动的buffer操作,即允许进行内存的整理
 * 
 * 实现参考：服务端收到beginOp的时候，
 * 需要标记此Buffer不可被碎片整理线程搬动。。完成commitOp()以后，
 * 则可以被搬动，如果正在搬动中，
 * 下次客户端调用beginOp会阻塞一段时间，
 * 等待搬动结束才操作

 * 
 * 
* 源文件名：MycatMovableBufer.java
* 文件版本：1.0.0
* 创建作者：Think
* 创建日期：2016年12月21日
* 修改作者：Think
* 修改日期：2016年12月21日
* 文件描述：TODO
* 版权所有：Copyright 2016 zjhz, Inc. All Rights Reserved.
*/
public interface MycatMovableBufer extends MycatBuffer {

    /**
     * 内存可以被管理器移动，首次访问（一个begin & commit操作之后）之前，
     * 用户需要先要调用beginOp()，即完整用法如下   
     *  Buf.beginOp();   
     *   Read or write    
     *   Buf.commitOp();    
     */
    public void beginOp();

    /**
     * 
     */
    public void commitOp();

    /**
     * 向目标的buffer拷贝数据
    * 方法描述
    * @param buffer
    * @创建日期 2016年12月21日
    */
    public void copyTo(MycatMovableBufer buffer);

    /**
     * 得到内存地址
    * 方法描述
    * @return
    * @创建日期 2016年12月22日
    */
    public long getAddress();

}
