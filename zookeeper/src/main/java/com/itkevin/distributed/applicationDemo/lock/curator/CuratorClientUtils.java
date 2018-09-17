package com.itkevin.distributed.applicationDemo.lock.curator;

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
    public final static int SESSIONTIMEOUT = 5000;//会话超时时间

    public static CuratorFramework getInstance() {
        curatorFramework = CuratorFrameworkFactory
                .builder()
                .connectString(CONNECTSTRING)
                .sessionTimeoutMs(SESSIONTIMEOUT)
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                .build();
        curatorFramework.start();
        return curatorFramework;
    }

}
