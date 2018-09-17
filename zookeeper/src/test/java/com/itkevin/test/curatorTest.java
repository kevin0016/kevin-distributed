package com.itkevin.test;

import com.itkevin.distributed.applicationDemo.lock.DistributeLock;
import com.itkevin.distributed.applicationDemo.lock.curator.CuratorClientUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Auther: liangxuekai
 * @Date: 18/9/17 14:21
 * @Description:
 */
public class curatorTest {
    @Test
    public void demo1() throws Exception {
        String ROOT_LOCKS = "/LOCKS";//跟节点
        byte[] data = {1, 2};//节点数据
        CuratorFramework curatorFramework = CuratorClientUtils.getInstance();
        String result = curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(ROOT_LOCKS + "/lock", data);
        System.out.println(result);
        System.in.read();
    }

    @Test
    public void demo2() {
        DistributeLock distributeLock = new DistributeLock();
        distributeLock.lock();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            distributeLock.unlock();
        }
    }


    @Test
    public void demo3() throws IOException {
        final CountDownLatch countDownLatch = new CountDownLatch(5);
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                DistributeLock distributeLock = null;
                try {
                    distributeLock = new DistributeLock();
                    countDownLatch.countDown();
                    countDownLatch.await();
                    distributeLock.lock();
                    Thread.sleep(random.nextInt(500));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (distributeLock != null) {
                        distributeLock.unlock();
                    }
                }

            }).start();
        }
        System.in.read();
    }

    @Test
    public void demo4() throws IOException {
        CuratorFramework curatorFramework = com.itkevin.distributed.base.curator.CuratorClientUtils.getInstance();
        try {
            curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/LOCKS");
            /*NodeCache nodeCache = new NodeCache(curatorFramework, "/LOCKS", false);
            nodeCache.start(true);
            nodeCache.getListenable().addListener(() -> System.out.println("节点数据发生变化,变化后的结果" +
                    "：" + new String(nodeCache.getCurrentData().getData())));
            nodeCache.getListenable().addListener(()->{
                nodeCache.getCurrentData().getStat().
            });
//            curatorFramework.setData().forPath("/LOCKS", "kevin".getBytes());
            curatorFramework.delete().forPath("/LOCKS");*/
            ExecutorService pool = Executors.newCachedThreadPool();
            TreeCache treeCache = new TreeCache(curatorFramework,"/LOCKS");
            treeCache.getListenable().addListener((client,event)->{
                ChildData data = event.getData();
                if(data !=null){
                    switch (event.getType()) {
                        case NODE_ADDED:
                            System.out.println("NODE_ADDED : "+ data.getPath() +"  数据:"+ new String(data.getData()));
                            break;
                        case NODE_REMOVED:
                            System.out.println("NODE_REMOVED : "+ data.getPath() +"  数据:"+ new String(data.getData()));
                            break;
                        case NODE_UPDATED:
                            System.out.println("NODE_UPDATED : "+ data.getPath() +"  数据:"+ new String(data.getData()));
                            break;
                        default:
                            break;
                    }
                }else{
                    System.out.println( "data is null : "+ event.getType());
                }
            },pool);
            treeCache.start();
            curatorFramework.setData().forPath("/LOCKS", "kevin".getBytes());
            curatorFramework.delete().forPath("/LOCKS");

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.in.read();
    }
}
