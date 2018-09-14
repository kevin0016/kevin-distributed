package com.itkevin.distributed.javaApi;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Auther: liangxuekai
 * @Date: 18/9/14 14:49
 * @Description: zookeeper,简单的创建zookeeper会话
 */
public class CreateSessionDemo {
    private final static String CONNECTSTRING = "111.231.94.46:2181";
    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws IOException, InterruptedException {
        ZooKeeper zooKeeper = new ZooKeeper(CONNECTSTRING, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                //如果当强的链接状态是连接成功的，那么通过计数器去控制
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    countDownLatch.countDown();
                    System.out.println(event.getState());
                }
            }
        });
        countDownLatch.await();
        System.out.println(zooKeeper.getState());
    }
}
