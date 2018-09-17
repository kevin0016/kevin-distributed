package com.itkevin.distributed.applicationDemo.lock.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Auther: liangxuekai
 * @Date: 18/9/17 14:56
 * @Description:
 */
public class LockWatcher {

    private CuratorFramework curatorFramework;
    private String nodePath;
    private CountDownLatch latch;

    public LockWatcher(CuratorFramework curatorFramework, String nodePath, CountDownLatch latch) {
        this.curatorFramework = curatorFramework;
        this.nodePath = nodePath;
        this.latch = latch;
    }

    public void start() {
        try {
            ExecutorService pool = Executors.newCachedThreadPool();
            TreeCache treeCache = new TreeCache(curatorFramework,nodePath);
            treeCache.getListenable().addListener((client,event)->{
                ChildData data = event.getData();
                if(data !=null){
                    switch (event.getType()) {
                        case NODE_ADDED:
                            System.out.println("NODE_ADDED : "+ data.getPath() +"  data:"+ new String(data.getData()));
                            break;
                        case NODE_REMOVED:
                            System.out.println("NODE_REMOVED : "+ data.getPath() +"  data:"+ new String(data.getData()));
                            latch.countDown();
                            break;
                        case NODE_UPDATED:
                            System.out.println("NODE_UPDATED : "+ data.getPath() +"  data:"+ new String(data.getData()));
                            break;
                        default:
                            break;
                    }
                }else{
                    System.out.println( "data is null : "+ event.getType());
                }
            },pool);
            treeCache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
