package com.itkevin.distributed.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @Auther: liangxuekai
 * @Date: 18/9/17 09:44
 * @Description:
 */
public class CuratorCreateSessionDemo {

    private final static String CONNECTSTRING = "111.231.94.46:2181";

    public static void main(String[] args) {
        //创建会话的两种方式 normal
        CuratorFramework curatorFramework= CuratorFrameworkFactory.
                newClient(CONNECTSTRING,5000,5000,
                        new ExponentialBackoffRetry(1000,3));
        curatorFramework.start(); //start方法启动连接

        //fluent风格
        CuratorFramework curatorFramework1 = CuratorFrameworkFactory.builder().connectString(CONNECTSTRING).sessionTimeoutMs(5000).
                retryPolicy(new ExponentialBackoffRetry(1000, 3)).
                namespace("/curator").build();
        curatorFramework1.start();
    }
}
