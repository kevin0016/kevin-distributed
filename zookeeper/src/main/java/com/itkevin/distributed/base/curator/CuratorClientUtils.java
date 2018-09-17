package com.itkevin.distributed.base.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @Auther: liangxuekai
 * @Date: 18/9/17 09:33
 * @Description:
 */
public class CuratorClientUtils {
    private static CuratorFramework curatorFramework;
    private final static String CONNECTSTRING = "111.231.94.46:2181";


    public static CuratorFramework getInstance() {
        curatorFramework = CuratorFrameworkFactory
                .builder()
                .connectString(CONNECTSTRING)
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                .build();
        curatorFramework.start();
        return curatorFramework;
    }

}
