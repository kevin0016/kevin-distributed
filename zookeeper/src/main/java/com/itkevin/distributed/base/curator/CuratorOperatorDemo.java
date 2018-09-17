package com.itkevin.distributed.base.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Auther: liangxuekai
 * @Date: 18/9/17 10:35
 * @Description:
 */
public class CuratorOperatorDemo {
    public static void main(String[] args) {
        CuratorFramework curatorFramework = CuratorClientUtils.getInstance();
        System.out.println("连接成功");

        //创建节点
        try {
            String result = curatorFramework.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath("/kevin/king", "".getBytes());
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //删除节点
        try {
            //默认情况下，version为－1
            curatorFramework.delete().deletingChildrenIfNeeded().forPath("/kevin");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //查询
        try {
            Stat stat = new Stat();
            byte[] bytes = curatorFramework.getData().storingStatIn(stat).forPath("/curator");
            System.out.println(new String(bytes)+"-->"+stat);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //更新
        try {
            Stat stat = curatorFramework.setData().forPath("/curator", "123".getBytes());
            System.out.println(stat);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //异步操作
        try {
        ExecutorService service = Executors.newFixedThreadPool(1);
        CountDownLatch countDownLatch = new CountDownLatch(1);

            curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).inBackground(new BackgroundCallback() {
                @Override
                public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                    System.out.println(Thread.currentThread().getName()+"->resultCode:"+curatorEvent.getResultCode()+"->"+curatorEvent.getType());
                    countDownLatch.countDown();
                }
            },service).forPath("/kevin","123".getBytes());
            countDownLatch.await();
            service.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //事务操作（curator独有的）
        try {
            Collection<CuratorTransactionResult> resultCollection = curatorFramework.inTransaction()
                    .create().forPath("/trans", "111".getBytes()).and()
                    .setData().forPath("/curator", "111".getBytes()).and()
                    .commit();
            for (CuratorTransactionResult result:resultCollection){
                System.out.println(result.getForPath()+"->"+result.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
