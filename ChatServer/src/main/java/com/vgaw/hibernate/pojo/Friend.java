package com.vgaw.hibernate.pojo;

import java.io.Serializable;

/**
 * Created by caojin on 2016/2/29.
 */
public class Friend implements Serializable {
    private String name;
    private String friendName;

    public Friend(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }
}
