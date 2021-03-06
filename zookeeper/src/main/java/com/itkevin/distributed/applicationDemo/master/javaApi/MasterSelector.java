package com.itkevin.distributed.applicationDemo.master.javaApi;

import com.alibaba.fastjson.JSONArray;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MasterSelector implements Watcher {

    private final static String MASTER_PATH = "/master"; //需要争抢的节点

    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    private UserCenter server;  //其他服务器

    private UserCenter master;  //master节点

    private boolean isRunning = false;

    private ZooKeeper zooKeeper;


    public MasterSelector(ZooKeeper zooKeeper, UserCenter server) {
        System.out.println("[" + server + "] 去争抢master权限");
        this.server = server;
        this.zooKeeper = zooKeeper;
    }

    public MasterSelector() {
    }

    public void start() {
        //开始选举
        if (!isRunning) {
            isRunning = true;
            chooseMaster();
        }
    }

    public void stop() {
        //停止
        if (isRunning) {
            isRunning = false;
            scheduledExecutorService.shutdown();
            releaseMaster();
        }
    }

    private void chooseMaster() {
        if (!isRunning) {
            System.out.println("当前服务没有启动" + JSONArray.toJSON(server));
            return;
        }
        try {
            String string = JSONArray.toJSONString(server);
            String result = zooKeeper.create(MASTER_PATH, string.getBytes(), ZooDefs.Ids.
                    OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);//采用临时节点
            System.out.println("创建master_path：" + result);
            master = server;
            System.out.println(master + "->我现在已经是master，你们要听我的" + JSONArray.toJSON(master));
            //定时器
            //master释放（master出现故障）每5秒释放一次
            scheduledExecutorService.schedule(() -> {
                releaseMaster();
            }, 2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            try {
                //已经存在
                Stat stat = new Stat();
                byte[] data = zooKeeper.getData(MASTER_PATH, new MasterSelector(), stat);
                String json = new String(data);
                UserCenter userCenter = JSONArray.parseObject(json, UserCenter.class);
                if (userCenter == null) {
                    System.out.println("启动操作" + JSONArray.toJSON(server));
                    checkIsMaster();
                } else {
                    master = userCenter;
                }
            } catch (KeeperException e1) {
                e1.printStackTrace();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

        }
    }

    private void releaseMaster() {
        //释放锁(故障模拟过程)
        //判断当前是不是master，只有master才需要释放
        if (checkIsMaster()) {
            try {
                zooKeeper.delete(MASTER_PATH, -1);
                System.out.println(JSONArray.toJSON(master) + "出现故障");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (KeeperException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkIsMaster() {
        //判断当前的server是不是master
        try {
            Stat stat = new Stat();
            byte[] data = zooKeeper.getData(MASTER_PATH, new MasterSelector(), stat);
            String json = new String(data);
            UserCenter userCenter = JSONArray.parseObject(json, UserCenter.class);
            if (userCenter.getMc_name().equals(server.getMc_name())) {
                master = userCenter;
                return true;
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        switch (watchedEvent.getType()) {
            case NodeCreated:
                System.out.println("");
                break;
            case NodeDeleted:
                System.out.println("节点被删除，master断开连接，需要重新选举");
                //此处调用选举算法
                chooseMaster();
                break;
            case NodeDataChanged:
                System.out.println("");
                break;
        }
    }

}
