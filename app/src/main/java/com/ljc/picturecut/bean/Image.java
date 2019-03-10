package com.ljc.picturecut.bean;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/1/15 0015.
 * 这个类用于存储逻辑上的任意一张图片的相关信息，逻辑上可代表一张图片
 */

public class Image {
    private Image superImage;       //父图片，即本图片由父图片剪切得来
    private Bitmap selfImage;       //本图片，Bitmap代表着本图片的二进制资源
    private ArrayList<Image> childImages;  //子图片集合，由本图片剪切得到的子图，保存在其子图集合中
    private int x;                  //图片的x坐标，是图该图片在根图片（没有父图片的图片）上的绝对x坐标
    private int y;                  //图片的y坐标，是图该图片在根图片（没有父图片的图片）上的绝对y坐标
    private String imageName;       //图片名

    public ArrayList<Image> getChildImages() {
        return childImages;
    }

    public void setChildImages(ArrayList<Image> childImages) {
        this.childImages = childImages;
    }

    public Image getSuperImage() {
        return superImage;
    }

    public void setSuperImage(Image superImage) {
        this.superImage = superImage;
    }

    public Bitmap getSelfImage() {
        return selfImage;
    }

    public void setSelfImage(Bitmap selfImage) {
        this.selfImage = selfImage;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

}
