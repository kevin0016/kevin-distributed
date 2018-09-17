package com.itkevin.distributed.applicationDemo.lock;

import com.itkevin.distributed.applicationDemo.lock.curator.CuratorClientUtils;
import com.itkevin.distributed.applicationDemo.lock.curator.LockWatcher;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: liangxuekai
 * @Date: 18/9/17 13:51
 * @Description:
 */
public class DistributeLock {

    private static final String ROOT_LOCKS = "/LOCKS";//跟节点

    private int sessionTimeout;//会话超时时间

    private String lockID;//记录锁节点ID

    private final static byte[] data = {1, 2};//节点数据

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private CuratorFramework curatorFramework;

    public DistributeLock() {
        this.sessionTimeout = CuratorClientUtils.SESSIONTIMEOUT;
        this.curatorFramework = CuratorClientUtils.getInstance();
    }

    //获取锁的方法
    public boolean lock() {
        try {
            //创建临时有序节点
            lockID = curatorFramework.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(ROOT_LOCKS + "/lock", data);
            System.out.println(Thread.currentThread().getName() + "->成功创建了lock节点[" + lockID + "], 开始去竞争锁");
            //获取到跟节点下的所有子节点
            List<String> childrenNodes = curatorFramework.getChildren().forPath(ROOT_LOCKS);
            //排序
            SortedSet<String> sortedSet = new TreeSet<>();
            childrenNodes.stream().forEach(children -> sortedSet.add(ROOT_LOCKS + "/" + children));
            //拿到最小的节点
            String first = sortedSet.first();
            //如果最小节点恰好等于当前节点，则获得锁成功
            if (lockID.equals(first)) {
                System.out.println(Thread.currentThread().getName() + "->成功获得锁，lock节点为:[" + lockID + "]");
                return true;
            }

            SortedSet<String> lessThanLockId = sortedSet.headSet(lockID);
            if (!lessThanLockId.isEmpty()) {
                String prevLockID = lessThanLockId.last();//拿到比当前LOCKID这几点更小的上一个节点
                new LockWatcher(curatorFramework, prevLockID, countDownLatch).start();
                countDownLatch.await(sessionTimeout, TimeUnit.MILLISECONDS);
                //上面这段代码意味着如果会话超时或者节点被删除(释放)了
                System.out.println(Thread.currentThread().getName() + " 成功获取锁：[" + lockID + "]");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean unlock() {
        System.out.println(Thread.currentThread().getName() + "->开始释放锁:[" + lockID + "]");
        try {
            curatorFramework.delete().forPath(lockID);
            System.out.println("节点[" + lockID + "]成功被删除");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /*public static void main(String[] args) {
        final CountDownLatch countDownLatch = new CountDownLatch(10);
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
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
    }*/
}
