package com.itkevin.distributed.applicationDemo.master.javaApi;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;


public class MasterSelectorWatcher implements Watcher {

    @Override
    public void process(WatchedEvent watchedEvent) {
        switch (watchedEvent.getType()){
            case NodeCreated:
                System.out.println("");
                break;
            case NodeDeleted:
                System.out.println("节点被删除，master断开连接，需要重新选举");
                //此处调用选举算法
                break;
            case NodeDataChanged:
                System.out.println("");
                break;
        }
    }
}
