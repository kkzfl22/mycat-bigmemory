package io.mycat.bigmem.cacheway.mapfile;

import java.nio.ByteBuffer;

import io.mycat.bigmem.cacheway.CacheOperatorInf;

public class MapfileOperatorImpl implements CacheOperatorInf {

    @Override
    public ByteBuffer allocationMemory(int size) {
        return null;
    }

    @Override
    public boolean recycleAll(ByteBuffer bufer) {
        return false;
    }

    @Override
    public boolean recycleNotUse(ByteBuffer bufer) {
        // TODO Auto-generated method stub
        return false;
    }

}
