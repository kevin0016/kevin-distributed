package com.itkevin.distributed.base.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.TimeUnit;

/**
 * @Auther: liangxuekai
 * @Date: 18/9/17 09:53
 * @Description:
 */
public class CuratorEventDemo {

    /**
     * 三种watcher来做节点的监听
     * pathcache   监视一个路径下子节点的创建、删除、节点数据更新
     * NodeCache   监视一个节点的创建、更新、删除
     * TreeCache   pathcaceh+nodecache 的合体（监视路径下的创建、更新、删除事件），
     * 缓存路径下的所有子节点的数据
     */
    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework = CuratorClientUtils.getInstance();
        //创建节点
       /* String result = curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).
                forPath("/curator/curator1/curator11", "123".getBytes());
        System.out.println(result);*/
        //节点变化NodeCache
        curatorFramework.delete().forPath("/curator");
        NodeCache nodeCache = new NodeCache(curatorFramework, "/curator", false);
        nodeCache.start(true);
        nodeCache.getListenable().addListener(() -> System.out.println("节点数据发生变化,变化后的结果" +
                "：" + new String(nodeCache.getCurrentData().getData())));
        nodeCache.getListenable().removeListener(() -> System.out.println("节点数据发生删除,变化后的结果" +
                "：" + new String(nodeCache.getCurrentData().getData())));
        curatorFramework.setData().forPath("/curator", "kevin".getBytes());
        System.in.read();

        //PathChildrenCache
        PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, "/event", true);
        pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        // Normal / BUILD_INITIAL_CACHE /POST_INITIALIZED_EVENT
        pathChildrenCache.getListenable().addListener((curatorFramework1, pathChildrenCacheEvent) -> {
            switch (pathChildrenCacheEvent.getType()) {
                case CHILD_ADDED:
                    System.out.println("增加子节点");
                    break;
                case CHILD_REMOVED:
                    System.out.println("删除子节点");
                    break;
                case CHILD_UPDATED:
                    System.out.println("更新子节点");
                    break;
                default:
                    break;
            }
        });

        curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath("/event", "event".getBytes());
        TimeUnit.SECONDS.sleep(1);
        curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath("/event/event1", "1".getBytes());
        TimeUnit.SECONDS.sleep(1);
        curatorFramework.setData().forPath("/event/event1", "22".getBytes());
        TimeUnit.SECONDS.sleep(1);
        curatorFramework.delete().forPath("/event/event1");
        System.in.read();

    }
}
