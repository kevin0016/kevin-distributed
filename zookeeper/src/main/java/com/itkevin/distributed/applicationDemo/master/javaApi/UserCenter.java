package com.itkevin.distributed.applicationDemo.master.javaApi;

import java.io.Serializable;

public class UserCenter implements Serializable {

    private static final long serialVersionUID = -6573631817535508366L;
    private int mc_id; //机器信息

    private String mc_name;//机器名称

    @Override
    public String toString() {
        return "UserCenter{" +
                "mc_id=" + mc_id +
                ", mc_name='" + mc_name + '\'' +
                '}';
    }

    public int getMc_id() {
        return mc_id;
    }

    public void setMc_id(int mc_id) {
        this.mc_id = mc_id;
    }

    public String getMc_name() {
        return mc_name;
    }

    public void setMc_name(String mc_name) {
        this.mc_name = mc_name;
    }
}
