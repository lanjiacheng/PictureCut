package com.ljc.picturecut.bean;

import com.ljc.picturecut.database.Project;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
/**
 * Created by Administrator on 2018/1/19 0019.
 * bean类，用于存储所有项目，这些项目从数据库中读取出来，
 * 同时这个类中的项目会与数据库同步，增删本类中的内容，直接影响到数据库的数据
 * 同时还是单例模式的类
 */

public class AllProject {
    private static AllProject self;    //自己
    private ArrayList<Project> allProjects;     //所有项目
    private Project currentOpenProject;         //当前打开的项目

    private AllProject(){
        self = this;
    }

    public static AllProject getObject(){
        if (self == null){
            return new AllProject();
        }
        return self;
    }

    /**
     * 初始化方法，要使用本类前，先调用此方法初始化数据
     */

    public void initialize(){
        //查询数据库的对应表中所有行，传给所有项目集合
        allProjects = (ArrayList<Project>) DataSupport.findAll(Project.class);
    }

    /**
     * 添加项目，同时同步到数据库
     */
    public void addProject(Project project){
        //先将该项目存储到数据库
        project.save();
        //没毛病再添加到所有类中
        allProjects.add(project);
    }

    /**
     * 更新项目，同时同步到数据库
     */
    public void updateProject(Project project){
        if (project.isSaved()){
            project.save();     //如果对象是已保存的就直接更新
        }else {
            try {
                throw new Exception("Can not update a unsaved project!");      //如果没保存的就直接抛异常
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 查询指定名称的项目
     */
    public Project getProjectByName(String name){
        for(Project project:allProjects){
            if (project.getName().equals(name)){
                return project;         //名字相同就返回
            }
        }
        return null;        //没找到就返回空
    }

    /**
     * 删除指定名称的项目，同时同步到数据库
     */
    public void deleteProjectByName(String name){
        Project project = getProjectByName(name);   //获取同名项目
        project.delete();
        allProjects.remove(project);
    }

    /**
     * 回收
     */
    public static void recycle(){
        self = null;
    }


    public AllProject getSelf() {
        return self;
    }

    public void setSelf(AllProject self) {
        this.self = self;
    }

    public ArrayList<Project> getAllProjects() {
        return allProjects;
    }

    public void setAllProjects(ArrayList<Project> allProjects) {
        this.allProjects = allProjects;
    }

    public Project getCurrentOpenProject() {
        return currentOpenProject;
    }

    public void setCurrentOpenProject(Project currentOpenProject) {
        this.currentOpenProject = currentOpenProject;
    }
}