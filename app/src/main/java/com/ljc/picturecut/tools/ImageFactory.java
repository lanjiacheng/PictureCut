package com.ljc.picturecut.tools;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.ljc.picturecut.EditActivity;
import com.ljc.picturecut.R;
import com.ljc.picturecut.bean.AllImage;
import com.ljc.picturecut.bean.AllProject;
import com.ljc.picturecut.bean.ContextOfEdit;
import com.ljc.picturecut.bean.Image;
import com.ljc.picturecut.database.Project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Administrator on 2018/1/15 0015.
 * 工具类，用于图片的剪切、合成与存储
 */

public class ImageFactory {
    /**
     * 按照一定的宽高比例裁剪图片
     */
    public static ArrayList<Image> cutImageByXY(Image srcImage, int[] lens_x, int[] lens_y) {
        Bitmap srcBitmap = srcImage.getSelfImage();
        ArrayList<Image> desImages = new ArrayList<Image>();
        int xOfSrcImage = srcImage.getX();
        int yOfSrcImage = srcImage.getY();
        int currentX = 0;
        int currentY = 0;
        int sumX = getSumOfAllArray(lens_x);
        int sumY = getSumOfAllArray(lens_y);
        int currentWidth = 0;
        int currentHeight = 0;
        int srcWidth = srcBitmap.getWidth();
        int srcHeight = srcBitmap.getHeight();
        for (int i = 0;lens_y[i]!=0;i++){
            for (int j = 0;lens_x[j]!=0;j++){
                currentX = getArraySumOfBeforeIndex(lens_x,j)*srcWidth/sumX;
                currentY = getArraySumOfBeforeIndex(lens_y,i)*srcHeight/sumY;
                currentWidth = lens_x[j]*srcWidth/sumX;
                currentHeight = lens_y[i]*srcHeight/sumY;
                Bitmap desBitmap = Bitmap.createBitmap(srcBitmap,currentX,currentY,currentWidth,currentHeight);
                Image desImage = new Image();
                desImage.setSuperImage(srcImage);
                desImage.setSelfImage(desBitmap);
                desImage.setChildImages(null);
                desImage.setX(currentX+xOfSrcImage);
                desImage.setY(currentY+yOfSrcImage);
                desImage.setImageName(srcImage.getImageName()+"$"+(i+1)+"_"+(j+1));
                desImages.add(desImage);
            }
        }
        srcImage.setChildImages(desImages);     //给父图片设置子图集合
        return desImages;
    }

    /**
     * 合并图片，放入一个图片集合，就会自动根据图片的属性合成一张完整的图片（图片之间有间隔）
     * 然后返回合成的图片
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static Image margeImages(ArrayList<Image> images){
        int margin = AllImage.getObject().getRootImage().getSelfImage().getWidth()/150;     //设置间隔
        ArrayList<Image> newImages = new ArrayList<>();
        for (Image image:images){
            Bitmap bitmap = Bitmap.createBitmap(image.getSelfImage());      //复制图片，以保护原来数组里面的图片
            //创建透明图片
            Bitmap bigBitmap = BitmapFactory.decodeResource(EditActivity.self.getResources(),R.drawable.imge_transparent);
            Image bigImage = new Image();
            bigImage.setSelfImage(bigBitmap);
            //缩放成bitmap的大小
            bigImage = resizeImage(bigImage,bitmap.getWidth(),bitmap.getHeight());
            bigBitmap = bigImage.getSelfImage();
            //创建透明图片的画板
            Canvas canvas = new Canvas(bigBitmap);
            int oldWidth = bitmap.getWidth();
            int oldHeight = bitmap.getHeight();
            Image image1 = new Image();
            image1.setSelfImage(bitmap);
            //缩放
            image1 = resizeImage(image1,oldWidth-margin,oldHeight-margin);
            bitmap = image1.getSelfImage();
            canvas.drawBitmap(bitmap,margin/2,margin/2,null);     //把缩小后的图片画进去
            Image newImage = new Image();
            newImage.setSelfImage(bigBitmap);
            newImage.setX(image.getX());
            newImage.setY(image.getY());
            newImages.add(newImage);
        }
        Bitmap margeBitmap = BitmapFactory.decodeResource(EditActivity.self.getResources(),R.drawable.imge_transparent);
        Image margeImage = new Image();
        margeImage.setSelfImage(margeBitmap);
        margeImage = resizeImage(margeImage,AllImage.getObject().getRootImage().getSelfImage().getWidth(),AllImage.getObject().getRootImage().getSelfImage().getHeight());
        margeBitmap = margeImage.getSelfImage();
        Canvas canvas = new Canvas(margeBitmap);
        for (Image image:newImages){
            Bitmap bitmap = image.getSelfImage();
            canvas.drawBitmap(bitmap,image.getX(),image.getY(),null);
        }
        margeBitmap = Bitmap.createBitmap(margeBitmap,margin/2,margin/2,margeBitmap.getWidth()-margin/2,margeBitmap.getHeight()-margin/2);
        margeImage.setSelfImage(margeBitmap);
        return margeImage;
    }

    public static Image resizeImage(Image image, int w, int h){
        Bitmap BitmapOrg = image.getSelfImage();
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        Image newImage = new Image();
        newImage.setSelfImage(resizedBitmap);
        return newImage;
    }

    //存储单张图片
    public static void saveImage(Context context,Image image,String dirOfProject1) {
        Bitmap bitmap = image.getSelfImage();
        File dirOfApp = new File(Environment.getExternalStorageDirectory(),"PictureCut");
        if (!dirOfApp.exists()){
            dirOfApp.mkdir();
        }
        File dirOfProject = new File(dirOfApp, dirOfProject1);
        if (!dirOfProject.exists()) {
            dirOfProject.mkdir();
        }
        File[] files = dirOfProject.listFiles();
        int n = files.length+1;
        String temp = "";
        if (n<10){
            temp = "0"+n;
        }else {
            temp = ""+n;
        }

        String fileName = "split_"+temp + ".jpg";
        File file = new File(dirOfProject, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ContextOfEdit.getObject().setDirOfCurrentProject(dirOfProject);     //将项目目录设置给编辑环境
    }

    //批量存储图片
    public static void saveBatchImages(final Context context, final ArrayList<Image> images){
        File dirOfApp = new File(Environment.getExternalStorageDirectory(),"PictureCut");
        if (!dirOfApp.exists()){
            dirOfApp.mkdir();
        }
        int count = 0;
        for (File file:dirOfApp.listFiles()){
            String name = file.getName();
            String subName = "";
            int index = name.indexOf('(');
            if (index != -1) {
                subName = name.substring(0, index);
            }else {
                subName = name;
            }
            if (subName.equals(AllImage.getObject().getRootImage().getImageName())){
                count++;
            }
        }
        final int countOfSameNameFile = count;
        final File dirOfProject = new File(dirOfApp, AllImage.getObject().getRootImage().getImageName());
        //先检测项目目录是否已存在，存在就询问是否覆盖
        if (dirOfProject.exists()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(EditActivity.self);
            dialog.setTitle("该项目已存在！");
            dialog.setMessage("您可以选择以下操作：");
            dialog.setCancelable(true);
            dialog.setPositiveButton("另存", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //另存操作
                    for (Image image : images){
                        saveImage(context,image,AllImage.getObject().getRootImage().getImageName()+"("+countOfSameNameFile+")");
                    }
                    ContextOfEdit.getObject().setSaved(true);       //设置当前编辑环境为已保存
                    Project project = new Project();
                    project.setName(ContextOfEdit.getObject().getDirOfCurrentProject().getName());
                    project.setTime(getTime());
                    project.setPath(ContextOfEdit.getObject().getDirOfCurrentProject().getAbsolutePath());
                    project.setCount(ContextOfEdit.getObject().getDirOfCurrentProject().listFiles().length);
                    AllProject.getObject().addProject(project);
                    AllProject.getObject().setCurrentOpenProject(project);
                    savePreview();
                    Toast.makeText(context,"另存成功",Toast.LENGTH_SHORT).show();
                    ((EditActivity)context).finish();          //保存后，退出
                }
            });
            dialog.setNegativeButton("覆盖",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //覆盖操作
                    try {
                        Runtime.getRuntime().exec("rm -r -f "+dirOfProject.getAbsolutePath());
                        Thread.sleep(200);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        for (Image image : images){
                            saveImage(context,image,AllImage.getObject().getRootImage().getImageName());
                        }
                        ContextOfEdit.getObject().setSaved(true);       //设置当前编辑环境已以保存
                        Project project = AllProject.getObject().getProjectByName(AllImage.getObject().getRootImage().getImageName());
                        project.setName(AllImage.getObject().getRootImage().getImageName());
                        project.setTime(getTime());
                        project.setPath(ContextOfEdit.getObject().getDirOfCurrentProject().getAbsolutePath());
                        project.setCount(ContextOfEdit.getObject().getDirOfCurrentProject().listFiles().length);
                        AllProject.getObject().updateProject(project);      //更新
                        AllProject.getObject().setCurrentOpenProject(project);
                        savePreview();
                        Toast.makeText(context,"覆盖成功",Toast.LENGTH_SHORT).show();
                        ((EditActivity)context).finish();          //保存后，退出
                    }
                }
            });
            dialog.show();
        }else {
            for (Image image : images){
                saveImage(context,image,AllImage.getObject().getRootImage().getImageName());
            }
            ContextOfEdit.getObject().setSaved(true);       //设置当前编辑环境为已保存
            //构建相应项目并保存
            Project project = new Project();
            project.setName(AllImage.getObject().getRootImage().getImageName());
            project.setTime(getTime());
            project.setPath(ContextOfEdit.getObject().getDirOfCurrentProject().getAbsolutePath());
            project.setCount(ContextOfEdit.getObject().getDirOfCurrentProject().listFiles().length);
            AllProject.getObject().addProject(project);
            AllProject.getObject().setCurrentOpenProject(project);
            savePreview();
            Toast.makeText(context,"保存成功",Toast.LENGTH_SHORT).show();
            ((EditActivity)context).finish();          //保存后，退出
        }
    }


    //求指定数组的指定下标之前（不包括此下标）对应的元素的总和
    private static int getArraySumOfBeforeIndex(int[] array,int index){
        int sum = 0;
        for(int i = 0;i<index;i++){
            sum+=array[i];
        }
        return sum;
    }
    //求指定数组的所有任意非零元素之前的元素之和（一旦出现0就不再计算）
    private static int getSumOfAllArray(int[] array){
        int sum = 0;
        for(int i = 0;array[i]!=0;i++){
            sum+=array[i];
        }
        return sum;
    }
    //获取系统时间字符串
    private static String getTime(){
        Calendar calendar = Calendar.getInstance();
        String year = calendar.get(Calendar.YEAR)+"";
        String month = (calendar.get(Calendar.MONTH)+1)>=10? ""+(calendar.get(Calendar.MONTH)+1) : "0"+(calendar.get(Calendar.MONTH)+1);
        String day = calendar.get(Calendar.DAY_OF_MONTH)>=10? ""+calendar.get(Calendar.DAY_OF_MONTH) : "0"+calendar.get(Calendar.DAY_OF_MONTH);
        String hour = calendar.get(Calendar.HOUR_OF_DAY)>=10? ""+calendar.get(Calendar.HOUR_OF_DAY) : "0"+calendar.get(Calendar.HOUR_OF_DAY);
        String minute = calendar.get(Calendar.MINUTE)>=10? ""+calendar.get(Calendar.MINUTE) : "0"+calendar.get(Calendar.MINUTE);
        return year+"/"+month+"/"+day+"/"+hour+":"+minute;
    }
    //存储预览图
    private static void savePreview(){

        try {
            File previewFile = new File(ContextOfEdit.getObject().getDirOfCurrentProject(),"preview.png");
            FileOutputStream fos = new FileOutputStream(previewFile);
            ContextOfEdit.getObject().getPreviewImage().getSelfImage().compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Image adaptPreviewImage(Image image){
        Bitmap bitmap = image.getSelfImage();
        if (bitmap==null){
            return image;
        }
        int oldHeight = bitmap.getHeight();
        int oldWidth = bitmap.getWidth();
        int newHeight = (int)(1080.0f*oldHeight/oldWidth);
        Image newImage = new Image();
        newImage.setSelfImage(bitmap);
        newImage = ImageFactory.resizeImage(newImage,1080,newHeight);
        return newImage;
    }
}
