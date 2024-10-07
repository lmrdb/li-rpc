package com.li.lirpc.fault.tolerant;


/**
 * 容错策略键名常量
 */
public interface TolerantStrategyKeys {


    /**
     * 故障恢复
     */
    String FAIL_BACK="failBack";


    /**
     * 快速失败
     */
    String FAIL_FAST="failFast";


    /**
     * 静默处理
     */
    String FAIL_SAFE="failSafe";


    /**
     * 故障转移
     */
    String FAIL_OVER="failOver";
}
