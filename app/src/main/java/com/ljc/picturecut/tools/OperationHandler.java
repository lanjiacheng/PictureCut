package com.ljc.picturecut.tools;

import android.content.Context;

import com.ljc.picturecut.bean.AllImage;
import com.ljc.picturecut.bean.Image;
import com.ljc.picturecut.bean.Operation;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/1/17 0017.
 * 操作处理器，提供对操作的处理方法
 */

public class OperationHandler {
    //传入一个操作，解析操作并执行
    public static void runOperation(Context context,Operation operation){
        //剪切图片，并将结果返回
        ArrayList<Image> desImages = ImageFactory.cutImageByXY(operation.getSrcImage(),operation.getLens_x(),operation.getLens_y());
        //将剪切的结果子图添加到所有图片中
        AllImage.getObject().addImages(desImages);
        //将返回的子图设置给该操作
        operation.setDesImages(desImages);
        //将此操作标记为已执行
        operation.setRuned(true);
    }
}
