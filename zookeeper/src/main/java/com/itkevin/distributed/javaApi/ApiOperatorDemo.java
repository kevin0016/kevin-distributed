package com.itkevin.distributed.javaApi;

import com.itkevin.distributed.zkclient.ZkClientApiOperatorDemo;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Auther: liangxuekai
 * @Date: 18/9/14 14:55
 * @Description:
 */
public class ApiOperatorDemo implements Watcher {
    private final static String CONNECTSTRING = "111.231.94.46:2181";
    private static CountDownLatch countDownLatch = new CountDownLatch(1);
    private static ZooKeeper zookeeper;
    private static Stat stat = new Stat();

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        zookeeper = new ZooKeeper(CONNECTSTRING, 5000, new ApiOperatorDemo());
        countDownLatch.await();
        ACL acl = new ACL(ZooDefs.Perms.ALL, new Id("ip", "111.231.94.46"));
        List<ACL> acls = new ArrayList<>();
        acls.add(acl);
//        zookeeper.getData("/authTest", true, new Stat());


        //创建节点
        String result=zookeeper.create("/node1","123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        zookeeper.getData("/node1",new ApiOperatorDemo(),stat); //增加一个
        System.out.println("创建成功："+result);
        System.in.read();
       /* //修改数据
        zookeeper.setData("/node1","mic123".getBytes(),-1);
        Thread.sleep(2000);
        //修改数据
        zookeeper.setData("/node1","mic234".getBytes(),-1);
        Thread.sleep(2000);*/



    }

    /**
     * 功能描述: 事件监听器
     *
     * @param:
     * @return:
     * @auther:
     * @date:
     */
    @Override
    public void process(WatchedEvent event) {
        //如果当前的连接状态是连接成功的，那么通过计数器去控制
        if (event.getState() == Event.KeeperState.SyncConnected) {
            countDownLatch.countDown();
            System.out.println(event.getState() + "-->" + event.getType());
        } else if (event.getType() == Event.EventType.NodeDataChanged) {
            //节点数据发生变化
            try {
                System.out.println("数据变更触发路径：" + event.getPath() + "->改变后的值：" +
                        zookeeper.getData(event.getPath(), true, stat));
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else if (event.getType()==Event.EventType.NodeChildrenChanged){
            //自节点数据发生更改
            try {
                System.out.println("子节点数据变更路径："+event.getPath()+"->改变后的值："+
                zookeeper.getData(event.getPath(),true,stat));
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }else if(event.getType()== Event.EventType.NodeCreated){//创建子节点的时候会触发
            try {
                System.out.println("节点创建路径："+event.getPath()+"->节点的值："+
                        zookeeper.getData(event.getPath(),true,stat));
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else if(event.getType()== Event.EventType.NodeDeleted){//子节点删除会触发
            System.out.println("节点删除路径："+event.getPath());
        }
        System.out.println(event.getType());
    }
}
