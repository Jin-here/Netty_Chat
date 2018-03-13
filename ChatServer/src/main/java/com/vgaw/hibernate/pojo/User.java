package com.vgaw.hibernate.pojo;

/**
 * Created by Administrator on 2015/9/15.
 */
public class User {
    private long id;

    private String token;
    private String name;
    private String password;

    public User(){}

    public long getId() {
        return id;
    }

    public User setId(long id) {
        this.id = id;
        return this;
    }

    public String getToken() {
        return token;
    }

    public User setToken(String token) {
        this.token = token;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }
}
