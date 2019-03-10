package com.ljc.picturecut.bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/1/16 0016.
 * 单例模式的一个类，存储所有操作
 */

public class AllOperation {
    private static AllOperation self;
    private ArrayList<Operation> allOperations;
    private AllOperation(){
        self = this;
        allOperations = new ArrayList<Operation>();
    }
    public static AllOperation getObject(){
        if (self==null){
            return new AllOperation();
        }else {
            return self;
        }
    }
    //回收
    public static void recycle(){
        self = null;
    }
    //添加一个操作
    public void addOperation(Operation operation){
        int num = operation.getNumber();
        for(Operation operation1 : allOperations){
            if(num == operation1.getNumber()){
                return;
            }
        }
        allOperations.add(operation);
    }
    //获取指定序号的操作
    public Operation getOperationByNumber(int num){
        for (int i = 0;i<allOperations.size();i++){
            Operation operation = allOperations.get(i);
            if(operation.getNumber()==num){
                return operation;
            }
        }
        return null;
    }
    //获取所有操作
    public ArrayList<Operation> getAllOperations(){
        return allOperations;
    }
    //获取操作个数
    public int getCountOfOperations(){
        return allOperations.size();
    }

    //检测是否所有操作已经执行
    public boolean isAllRunned(){
        for (Operation operation:allOperations){
            if(!operation.isRuned()){           //如果有一个没有执行，返回false
                return false;
            }
        }
        return true;
    }

    //删除指定序号的操作
    public void deleteOperationByNumber(int num){
        for (Operation operation:allOperations){
            if(operation.getNumber()==num){
                allOperations.remove(operation);
            }
        }
    }
}
