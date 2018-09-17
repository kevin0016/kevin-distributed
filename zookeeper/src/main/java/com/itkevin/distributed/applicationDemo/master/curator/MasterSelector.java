package com.itkevin.distributed.applicationDemo.master.curator;

import com.itkevin.distributed.base.curator.CuratorClientUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MasterSelector {
    private final static String MASTER_PATH = "/curator_master_path";

    public static void main(String[] args) throws IOException {
        CuratorFramework curatorFramework = CuratorClientUtils.getInstance();
        LeaderSelector leaderSelector = new LeaderSelector(curatorFramework, MASTER_PATH, new LeaderSelectorListenerAdapter() {
            @Override
            public void takeLeadership(CuratorFramework client) throws Exception {
                System.out.println("获得leader成功");
                TimeUnit.SECONDS.sleep(2);
            }
        });

        leaderSelector.autoRequeue();
        leaderSelector.start();//开始选举

        System.in.read();
    }

}
