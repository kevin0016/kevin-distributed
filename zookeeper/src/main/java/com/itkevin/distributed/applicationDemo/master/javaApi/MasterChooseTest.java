package com.itkevin.distributed.applicationDemo.master.javaApi;

import org.apache.zookeeper.ZooKeeper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MasterChooseTest {
    private final static String CONNECTSTRING = "111.231.94.46:2181";

    private static int sessionTimeout = 5000;

    public static void main(String[] args) {

        List<MasterSelector> selectorList = new ArrayList<>();
        try {
            for (int i = 0; i < 10; i++) {
                ZooKeeper zooKeeper = new ZooKeeper(CONNECTSTRING, sessionTimeout, new MasterSelectorWatcher());
                UserCenter userCenter = new UserCenter();
                userCenter.setMc_id(i);
                userCenter.setMc_name("客户端：" + i);
                MasterSelector masterSelector = new MasterSelector(zooKeeper, userCenter);
                selectorList.add(masterSelector);
                masterSelector.start();//触发选举
                TimeUnit.SECONDS.sleep(1);

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            for (MasterSelector selector : selectorList) {
                selector.stop();
            }
        }
    }
}
