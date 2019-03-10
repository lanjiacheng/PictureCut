package com.ljc.picturecut.bean;


import java.util.ArrayList;

/**
 * Created by Administrator on 2018/1/15 0015.
 * 这是一个bean类，用于存储一个剪切操作的相关信息，代表一个操作
 */

public class Operation {
    private Image srcImage;                          //源图片，就是对哪张图片进行操作
    private int[] lens_x = new int[100];             //剪切图片后所得子图的横向各边长比例
    private int[] lens_y = new int[100];             //剪切图片后所得子图的纵向各边长比例
    private ArrayList<Image> desImages;              //剪切图片后所得子图集合
    private int number = -1;                         //操作的序号，用于区别不同操作，-1代表没序号
    private boolean isChooseImage = false;           //本操作是否已选择图片
    private boolean isRuned = false;                 //本操作是否已执行，一个操作只能执行一次

    public Image getSrcImage() {
        return srcImage;
    }

    public void setSrcImage(Image srcImage) {
        this.srcImage = srcImage;
    }

    public ArrayList<Image> getDesImages() {
        return desImages;
    }

    public void setDesImages(ArrayList<Image> desImages) {
        this.desImages = desImages;
    }

    public int[] getLens_x() {
        return lens_x;
    }

    public void setLens_x(int[] lens_x) {
        this.lens_x = lens_x;
    }

    public int[] getLens_y() {
        return lens_y;
    }

    public void setLens_y(int[] lens_y) {
        this.lens_y = lens_y;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isChooseImage() {
        return isChooseImage;
    }

    public void setChooseImage(boolean chooseImage) {
        isChooseImage = chooseImage;
    }

    public boolean isRuned() {
        return isRuned;
    }

    public void setRuned(boolean run) {
        isRuned = run;
    }
}
