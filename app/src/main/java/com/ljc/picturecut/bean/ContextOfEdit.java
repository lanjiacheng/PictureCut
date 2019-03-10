package com.ljc.picturecut.bean;

import android.widget.ImageView;

import java.io.File;

/**
 * Created by Administrator on 2018/1/17 0017.
 * 单例模式的bean类，用于记录当前编辑活动下的全局信息
 */

public class ContextOfEdit {
    private static ContextOfEdit self;
    private ImageView currentClickedImageView;      //当前点击的图片控件\
    private boolean isSaved = false;
    private Image previewImage;                 //预览图片
    private File dirOfCurrentProject;           //当前项目目录
    private ContextOfEdit(){
        self = this;
    }
    public static ContextOfEdit getObject(){
        if (self==null){
            return new ContextOfEdit();
        }else {
            return self;
        }
    }

    public ImageView getCurrentClickedImageView() {
        return currentClickedImageView;
    }

    public void setCurrentClickedImageView(ImageView currentClickedImageView) {
        this.currentClickedImageView = currentClickedImageView;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }
    public static void recycle(){
        self = null;
    }

    public Image getPreviewImage() {
        return previewImage;
    }

    public void setPreviewImage(Image previewImage) {
        this.previewImage = previewImage;
    }

    public File getDirOfCurrentProject() {
        return dirOfCurrentProject;
    }

    public void setDirOfCurrentProject(File dirOfCurrentProject) {
        this.dirOfCurrentProject = dirOfCurrentProject;
    }
}
