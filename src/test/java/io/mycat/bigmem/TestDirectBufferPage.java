package io.mycat.bigmem;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import io.mycat.bigmem.buffer.MycatBuffer;
import io.mycat.bigmem.buffer.MycatBufferBase;
import io.mycat.bigmem.cacheway.alloctor.BufferPage;
import io.mycat.bigmem.console.LocatePolicy;

public class TestDirectBufferPage {

    public static void main(String[] args) throws IOException {
        MycatBufferBase buffer = MycatBufferBase.getMyCatBuffer(LocatePolicy.Core, 2048);

        BufferPage page = new BufferPage(buffer, 256);

        MycatBufferBase buffer1 = page.alloactionMemory(4, System.currentTimeMillis());

        for (int i = 10; i < 50; i++) {
            buffer1.putByte((byte) i);
        }

        for (int i = 0; i < buffer1.putPosition(); i++) {
            System.out.println("获取结果:" + buffer1.get());
        }

        AtomicInteger index = new AtomicInteger(1);

        Runnable runJob = new Runnable() {
            @Override
            public void run() {

                MycatBuffer buffer2 = page.alloactionMemory(1, System.currentTimeMillis());
                try {
                    index.incrementAndGet();
                    Thread.currentThread().sleep(index.get() * 1000l);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                System.out.println();
                System.out.println();

                if (null != buffer2) {

                    for (int i = 0; i < 120; i++) {
                        buffer2.putByte((byte) i);
                    }

                    for (int i = 0; i < 120; i++) {
                        System.out.print("线程id" + Thread.currentThread().getId() + "值:" + buffer2.get() + "\t");
                        if (i % 10 == 0) {
                            System.out.println();
                        }
                    }
                }

            }
        };

        new Thread(runJob).start();
        new Thread(runJob).start();
        new Thread(runJob).start();
        new Thread(runJob).start();
        new Thread(runJob).start();
    }

}
