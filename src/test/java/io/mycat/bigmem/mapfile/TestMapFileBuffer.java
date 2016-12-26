package io.mycat.bigmem.mapfile;

import java.io.IOException;
import java.nio.ByteBuffer;

import io.mycat.bigmem.buffer.MyCatCallbackInf;
import io.mycat.bigmem.buffer.impl.MapFileBufferImp;

public class TestMapFileBuffer {

    public static void main(String[] args) throws IOException {
        final MapFileBufferImp mybuffer = new MapFileBufferImp(1024);

        mybuffer.putByte((byte) 10);
        mybuffer.putByte((byte) 12);
        mybuffer.putByte((byte) 120);
        mybuffer.putByte((byte) 100);
        mybuffer.putByte((byte) 90);

        for (int i = 0; i < mybuffer.putPosition(); i++) {
            System.out.println(mybuffer.get());
        }

        System.out.println("当前写入的游标：" + mybuffer.putPosition());
        System.out.println("当前读取的游标：" + mybuffer.getPosition());

        ByteBuffer bufferValue = ByteBuffer.allocateDirect(1024);

        mybuffer.copyTo(bufferValue);

        System.out.println(bufferValue);

        for (int i = 0; i < bufferValue.position(); i++) {
            System.out.println(bufferValue.get(i));
        }
        //
        // mybuffer.recycleUnuse();

        // 测试swapin以及swapOut
        // 数据写入交换到磁盘
        mybuffer.swapOut();

        // 加载到内存
        mybuffer.swapln();

        // 进行异步的通知
        mybuffer.swapOut(new MyCatCallbackInf() {
            @Override
            public void callBack() throws Exception {
                System.out.println("当前异步交换到磁盘");
            }
        });

        mybuffer.swapIn(new MyCatCallbackInf() {

            @Override
            public void callBack() throws Exception {
                System.out.println("异步交换到内存中j");
            }
        });

    }

}
