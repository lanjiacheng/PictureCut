package com.ljc.picturecut.listener;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import java.io.FileNotFoundException;

/**
 * Created by Administrator on 2018/1/15 0015.
 * 这是一个监听器，用于监听添加按钮被点击的事件
 */

public class OnClickListenerOfAddImageButton implements View.OnClickListener {
    private static Activity mActivity;
    /*
    构造函数，需要传入一个事件源所在的活动
     */
    public OnClickListenerOfAddImageButton(Activity activity){
        mActivity = activity;
    }

    @Override
    public void onClick(View v) {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(mActivity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }else{
            openAlbum();
        }
    }
    public static void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");      //设置intent类型，用于相册来解析
        mActivity.startActivityForResult(intent,1);
    }

}
