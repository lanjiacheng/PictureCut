package com.ljc.picturecut.bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/1/16 0016.
 * 单例模式的一个类，用于存储当前操作下的相关的所有图片
 */

public class AllImage {
    private static AllImage self;
    private ArrayList<Image> allImages;             //相关的所有图片

    private AllImage(){
        allImages = new ArrayList<Image>();
        self = this;
    }

    public static AllImage getObject(){
        if (self==null){
            return new AllImage();
        }else {
            return self;
        }
    }

    public static void recycle(){
        self = null;
    }

    //获取所有图片
    public ArrayList<Image> getAllImages(){
        return allImages;
    }

    //添加相关图片
    public void addImage(Image image){
        String name = image.getImageName();
        for(Image image1 : allImages){
            if(name == image1.getImageName()){      //添加图片前，先检测是否已存在，若已存在直接返回，不添加
                return;
            }
        }
        //不存在，则添加
        allImages.add(image);
    }

    //一次性添加多张图片
    public void addImages(ArrayList<Image> images){
        for (Image image : images){
            addImage(image);        //逐个调用添加单个方法来添加，自带检测重复功能
        }
    }

    //获取所有图片中没有子图片集合的图片
    public ArrayList<Image> getLittleImage(){
        ArrayList<Image> littleImages = new ArrayList<Image>();
        for(int i = 0;i<allImages.size();i++){
            Image image = allImages.get(i);
            if(image.getChildImages()==null){       //如果子集是空的，那么就是最小图片（不再切分，没子图）
                littleImages.add(image);            //添加
            }
        }
        return littleImages;
    }
    //获取所有图片中没有父图片的图片
    public Image getRootImage(){
        for (Image image : allImages){
            if (image.getSuperImage()==null){
                return image;
            }
        }
        return null;
    }

    //删除指定名称的图片
    public void deleteImageByName(String name){
        for (Image image:allImages){
            if (image.getImageName()==name){
                allImages.remove(image);
            }
        }
    }

    //批量删除指定的图片
    public void deleteBatchImages(ArrayList<Image> images){
//        for (Image image:allImages){
//            for (Image image1:images){
//                if(image1.getImageName() == image.getImageName()){
//                    deleteImageByName(image1.getImageName());       //删除
//                    image.getSuperImage().setChildImages(null);     //并将其父图的子图置为空
//                }
//            }
//        }
        Image superImage = images.get(0).getSuperImage();
        superImage.setChildImages(null);
        allImages.removeAll(images);
    }
}
