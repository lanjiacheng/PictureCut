package com.ljc.picturecut.listener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ljc.picturecut.EditActivity;
import com.ljc.picturecut.R;
import com.ljc.picturecut.adapter.ImageAdapter;
import com.ljc.picturecut.bean.AllImage;
import com.ljc.picturecut.bean.AllOperation;
import com.ljc.picturecut.bean.ContextOfEdit;
import com.ljc.picturecut.bean.Image;
import com.ljc.picturecut.bean.Operation;
import com.ljc.picturecut.tools.ImageFactory;
import com.ljc.picturecut.tools.OperationHandler;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by Administrator on 2018/1/16 0016.
 * 操作部分的监听器，内部包括多个子类，各自用于监听不同的控件的点击事件
 */

public class OnClickListenerOfOperationPart{
    /**
     * 监听剪切按钮点击事件
     */
    public static class OnClickListenerOfCutButton implements OnClickListener{
        private Context mContent;
        public OnClickListenerOfCutButton(Context context){
            mContent = context;
        }
        //点击剪切按钮，就会设置相应操作的属性（操作的创建是在添加操作时候完成的），并检测属性是否完整正确
        //检验没毛病即可执行剪切
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onClick(View v) {
            int n = 0;      //记录x边比例数的下标
            int m = 0;      //记录y边比例数的下标
            Button cutButton = (Button)v;   //向下转型成按钮
            LinearLayout parentOfCutButton = (LinearLayout) cutButton.getParent();
            LinearLayout layoutOfOperationBar = (LinearLayout)parentOfCutButton.getParent();     //获取按钮的父布局的父布局，也就是操作板
            int tag = (Integer) layoutOfOperationBar.getTag();       //获取该布局对象的标签，以分辨是哪个布局对应哪个操作
            Operation operation = AllOperation.getObject().getOperationByNumber(tag);   //通过标签获取相应的操作
            if (!operation.isChooseImage()){        //如果没有选择图片
                Toast.makeText(mContent,"请为本项剪切操作选图片",Toast.LENGTH_SHORT).show();
                return;
            }
            if (operation.isRuned()){
                Toast.makeText(mContent,"此项操作已经执行，不能重复执行",Toast.LENGTH_SHORT).show();
                return;
            }
            LinearLayout linearLayout_1 = (LinearLayout)parentOfCutButton.getChildAt(2);
            //获取文本编辑框的直接父布局
            LinearLayout linearLayout_1_x = (LinearLayout)linearLayout_1.getChildAt(0);
            HorizontalScrollView scrollView_x = (HorizontalScrollView)linearLayout_1_x.getChildAt(2);
            LinearLayout parentLayoutOfEditText_x = (LinearLayout)scrollView_x.getChildAt(0);
            int count_x = parentLayoutOfEditText_x.getChildCount();     //获取子视图个数
            //遍历所有子视图，对需要的子视图进行处理
            for(int i = 0;i<count_x;i++){
                View childView = parentLayoutOfEditText_x.getChildAt(i);
                if (childView instanceof EditText){         //如果这个子视图是文本编辑框
                    if (handleEditText((EditText)childView)){       //检验编辑框内容
                        (operation.getLens_x())[n] = Integer.valueOf(((EditText)childView).getText().toString());
                        n++;
                    }else {
                        Toast.makeText(mContent,"请输入正确边长比例",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
            //获取文本编辑框的直接父布局
            LinearLayout linearLayout_1_y = (LinearLayout)linearLayout_1.getChildAt(1);
            HorizontalScrollView scrollView_y = (HorizontalScrollView)linearLayout_1_y.getChildAt(2);
            LinearLayout parentLayoutOfEditText_y = (LinearLayout)scrollView_y.getChildAt(0);
            int count_y = parentLayoutOfEditText_y.getChildCount();     //获取子视图个数
            //遍历所有子视图，对需要的子视图进行处理
            for(int i = 0;i<count_y;i++){
                View childView = parentLayoutOfEditText_y.getChildAt(i);
                if (childView instanceof EditText){         //如果这个子视图是文本编辑框
                    if (handleEditText((EditText)childView)){       //检验编辑框内容
                        (operation.getLens_y())[m] = Integer.valueOf(((EditText)childView).getText().toString());
                        m++;
                    }else {
                        Toast.makeText(mContent,"请输入正确边长比例",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
            //设置检验完毕，没毛病就执行该操作，执行该操作后，会将子图集合放入所有图片，并将此操作标记为已执行
            OperationHandler.runOperation(mContent,operation);
            Image margeImage = ImageFactory.margeImages(AllImage.getObject().getLittleImage());
            ImageView imageView_preview = (ImageView)((EditActivity)mContent).findViewById(R.id.image_preview);
            Bitmap bitmap = margeImage.getSelfImage();
            Image image = new Image();
            image.setSelfImage(bitmap);
            image = ImageFactory.adaptPreviewImage(image);
            imageView_preview.setImageBitmap(image.getSelfImage());
            ContextOfEdit.getObject().setPreviewImage(margeImage);      //将预览图片设置给编辑环境
//            loadBitmapToImageView(margeImage.getSelfImage(),imageView_preview);
            Toast.makeText(mContent,"剪切成功",Toast.LENGTH_SHORT).show();
        }
        private boolean handleEditText(EditText editText){
            String content = editText.getText().toString();
            if (!content.equals("")) {
                if (Integer.valueOf(content)<=0){
                    return false;
                }
            }else {
                return false;
            }
            return true;
        }
    }

    /**
     * 监听选择图片控件点击事件
     */
    public static class OnClickListenerOfChoosedImage implements OnClickListener{
        private Activity mActivity;
        public OnClickListenerOfChoosedImage(Activity activity){
            mActivity = activity;
        }
        @Override
        public void onClick(View v) {
            ContextOfEdit.getObject().setCurrentClickedImageView((ImageView)v);
            Dialog dialog = new Dialog(mActivity);
            dialog.setContentView(R.layout.layout_chooseimage_dialog);
            Window dialogWindow = dialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = mActivity.getWindowManager().getDefaultDisplay().getWidth(); // 宽度
            lp.height = mActivity.getWindowManager().getDefaultDisplay().getHeight()*7/12; // 高度
            lp.alpha = 0.9f; // 透明度
            dialogWindow.setAttributes(lp);
            RecyclerView recyclerView = (RecyclerView)dialog.findViewById(R.id.recyclerview_chooseimage);
            GridLayoutManager layoutManager = new GridLayoutManager(mActivity,3);
            recyclerView.setLayoutManager(layoutManager);
            ImageAdapter imageAdapter = new ImageAdapter(AllImage.getObject().getLittleImage(),dialog);
            recyclerView.setAdapter(imageAdapter);
            dialog.show();
        }
    }

    /**
     * 监听器，监听添加操作按钮的点击事件
     */
    public static class OnClickListenerOfAddOperationButton implements OnClickListener{
        private Context mContent;
        public OnClickListenerOfAddOperationButton(Context context){
            mContent = context;
        }
        @Override
        public void onClick(View v) {
            //先检测是否所有操作已经执行
            if (!AllOperation.getObject().isAllRunned()){
                Toast.makeText(mContent,"尚有未完成的操作，不能添加新的操作",Toast.LENGTH_SHORT).show();
                return;
            }
            //获取操作板的父线性布局
            LinearLayout parentOfOperationBar = (LinearLayout) ((EditActivity)mContent).findViewById(R.id.button_in_operationpart).getParent().getParent().getParent();
            //获取要添加的操作板
            LinearLayout operationBar = (LinearLayout)LayoutInflater.from(mContent).inflate(R.layout.item_operation,parentOfOperationBar,false);
            int operationCount = AllOperation.getObject().getCountOfOperations();
            //设置标签，用于标识哪个操作板对应哪个操作
            operationBar.setTag(operationCount);
            //添加到操作板的父布局的末尾
            int count = parentOfOperationBar.getChildCount();
            parentOfOperationBar.addView(operationBar,count);
            //创建与本操作板对应的操作
            Operation operation = new Operation();
            operation.setNumber(operationCount);        //设置序号，与操作板标签相同
            AllOperation.getObject().addOperation(operation);       //添加到所有操作
            //接下来设置该新添加的操作板上的监听事件
            //先获取需要设置监听的控件
            LinearLayout parentOfButtonAndImage = (LinearLayout)operationBar.getChildAt(1);
            ImageView image_choose = (ImageView) parentOfButtonAndImage.getChildAt(0);  //需设监听
            Button button_cut = (Button)parentOfButtonAndImage.getChildAt(4);   //需设监听
            LinearLayout middleLayout = (LinearLayout)parentOfButtonAndImage.getChildAt(2);
            LinearLayout parentOfButton_x = (LinearLayout)middleLayout.getChildAt(0);
            LinearLayout parentOfButton_y = (LinearLayout)middleLayout.getChildAt(1);
            Button button_x_reduce = (Button) parentOfButton_x.getChildAt(1);   //需设监听
            Button button_x_add = (Button) parentOfButton_x.getChildAt(3);      //需设监听
            Button button_y_reduce = (Button) parentOfButton_y.getChildAt(1);   //需设监听
            Button button_y_add = (Button) parentOfButton_y.getChildAt(3);      //需设监听
            //开始设置监听
            image_choose.setOnClickListener(new OnClickListenerOfChoosedImage((EditActivity)mContent));
            button_cut.setOnClickListener(new OnClickListenerOfCutButton(mContent));
            button_x_reduce.setOnClickListener(new OnClickListenerOfReduceEditTextButton(mContent));
            button_x_add.setOnClickListener(new OnClickListenerOfAddEditTextButton(mContent));
            button_y_reduce.setOnClickListener(new OnClickListenerOfReduceEditTextButton(mContent));
            button_y_add.setOnClickListener(new OnClickListenerOfAddEditTextButton(mContent));
        }
    }

    /**
     * 监听器，监听删除操作按钮的点击事件
     */
    public static class OnClickListenerOfReduceOperationButton implements OnClickListener{
        private Context mContent;
        public OnClickListenerOfReduceOperationButton(Context context){
            mContent = context;
        }
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onClick(View v) {
            //获取操作板的父线性布局
            LinearLayout parentOfOperationBar = (LinearLayout) ((EditActivity)mContent).findViewById(R.id.button_in_operationpart).getParent().getParent().getParent();
            int count = parentOfOperationBar.getChildCount();
            if (count<=2){
                Toast.makeText(mContent,"操作不能少于1个",Toast.LENGTH_SHORT).show();
                return;
            }
            LinearLayout operationBar = (LinearLayout)parentOfOperationBar.getChildAt(count-1);
            int tag = (int)operationBar.getTag();
            Operation operation = AllOperation.getObject().getOperationByNumber(tag);
            if (operation.isRuned()) {
                //如果已执行，那么会生成图片，删除操作板，同时将其相关联的图片删除
                AllImage.getObject().deleteBatchImages(operation.getDesImages());
            }
            //无论如何，删除操作是必须滴
            AllOperation.getObject().deleteOperationByNumber(operation.getNumber());
            //删除操作板
            parentOfOperationBar.removeViewAt(count-1);
            //然后更新预览图片
            Image margeImage = ImageFactory.margeImages(AllImage.getObject().getLittleImage());
            ImageView imageView_preview = (ImageView)((EditActivity)mContent).findViewById(R.id.image_preview);
            Bitmap bitmap = margeImage.getSelfImage();
            Image image = new Image();
            image.setSelfImage(bitmap);
            image = ImageFactory.adaptPreviewImage(image);
            imageView_preview.setImageBitmap(image.getSelfImage());
        }
    }

    /**
     * 监听器，监听添加编辑框按钮的点击事件
     */
    public static class OnClickListenerOfAddEditTextButton implements OnClickListener{
        private Context mContent;
        public OnClickListenerOfAddEditTextButton(Context context){
            mContent = context;
        }
        @Override
        public void onClick(View v) {
            Button addButton = (Button)v;
            LinearLayout parentOfAddButton = (LinearLayout)addButton.getParent();
            HorizontalScrollView scrollView = (HorizontalScrollView)parentOfAddButton.getChildAt(2);
            LinearLayout parentOfEditText = (LinearLayout)scrollView.getChildAt(0);
            TextView text_split = (TextView) LayoutInflater.from(mContent).inflate(R.layout.textview_split,parentOfEditText,false);
            EditText editText = (EditText)LayoutInflater.from(mContent).inflate(R.layout.edittext,parentOfEditText,false);
            editText.setText("");
            int count = parentOfEditText.getChildCount();   //获取子视图数量
            parentOfEditText.addView(text_split,count-1);
            parentOfEditText.addView(editText,count);
        }
    }


    /**
     * 监听器，监听删除编辑框按钮的点击事件
     */
    public static class OnClickListenerOfReduceEditTextButton implements OnClickListener{
        private Context mContent;
        public OnClickListenerOfReduceEditTextButton(Context context){
            mContent = context;
        }
        @Override
        public void onClick(View v) {
            Button addButton = (Button)v;
            LinearLayout parentOfAddButton = (LinearLayout)addButton.getParent();
            HorizontalScrollView scrollView = (HorizontalScrollView)parentOfAddButton.getChildAt(2);
            LinearLayout parentOfEditText = (LinearLayout)scrollView.getChildAt(0);
            int count = parentOfEditText.getChildCount();   //获取子视图数量
            if (count<=3){
                Toast.makeText(mContent,"边数不能小于1",Toast.LENGTH_SHORT).show();
                return;
            }
            parentOfEditText.removeViewAt(count-2);
            parentOfEditText.removeViewAt(count-3);
        }
    }

    /**
     * 监听保存按钮点击事件
     */

    public static class OnClickListenerOfOKButton implements OnClickListener{
        private Context mContent;
        public OnClickListenerOfOKButton(Context context){
            mContent = context;
        }
        @Override
        public void onClick(View v) {
            for (Operation operation : AllOperation.getObject().getAllOperations()) {
                if (!operation.isRuned()) {
                    Toast.makeText(mContent,"还有未执行的剪切操作，无法保存",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            //再存储子图
            ImageFactory.saveBatchImages(mContent, AllImage.getObject().getLittleImage());
        }
    }
    //一个工具方法，用于将Bitmap对象加载到指定的图片视图上
    private static void loadBitmapToImageView(Bitmap bitmap, ImageView imageView){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        Glide.with(EditActivity.self).load(bytes).into(imageView);
    }
}