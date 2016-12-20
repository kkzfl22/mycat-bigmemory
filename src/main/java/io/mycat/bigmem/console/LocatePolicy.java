package io.mycat.bigmem.console;

public enum LocatePolicy {

    /**
     * 使用本地的内存进行缓存的策略
    * @字段说明 Core
    */
    Core(1),

    /**
     * 使用文件进行映射的缓存的策略信息
    * @字段说明 Normal
    */
    Normal(2);

    /**
     * 策略信息
    * @字段说明 policy
    */
    private int policy;

    public int getPolicy() {
        return policy;
    }

    public void setPolicy(int policy) {
        this.policy = policy;
    }

    private LocatePolicy(int policy) {
        this.policy = policy;
    }

}
