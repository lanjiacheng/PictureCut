package com.ljc.picturecut.database;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018/1/19 0019.
 * 一个项目类，存储一个项目的相关数据，并与数据库映射
 */

public class Project extends DataSupport {
    private String name;
    private String time;
    private String path;
    private int count;          //子图个数

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
