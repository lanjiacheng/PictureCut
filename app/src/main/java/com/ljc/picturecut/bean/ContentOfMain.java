package com.ljc.picturecut.bean;

import com.ljc.picturecut.adapter.ProjectAdapter;

/**
 * Created by Administrator on 2018/1/20 0020.
 * 单例，主活动的环境
 */

public class ContentOfMain {
    private static ContentOfMain self;
    private ProjectAdapter projectAdapter;      //项目列表视图的适配器
    private ContentOfMain(){
        self = this;
    }

    public static ContentOfMain getObject(){
        if (self==null){
            return new ContentOfMain();
        }
        return self;
    }

    public ProjectAdapter getProjectAdapter() {
        return projectAdapter;
    }

    public void setProjectAdapter(ProjectAdapter projectAdapter) {
        this.projectAdapter = projectAdapter;
    }

    //回收
    public static void recycle(){
        self = null;
    }
}
