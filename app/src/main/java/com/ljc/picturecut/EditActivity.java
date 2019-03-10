package com.ljc.picturecut;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.ljc.picturecut.bean.AllImage;
import com.ljc.picturecut.bean.AllOperation;
import com.ljc.picturecut.bean.ContextOfEdit;
import com.ljc.picturecut.bean.Image;
import com.ljc.picturecut.bean.Operation;
import com.ljc.picturecut.listener.OnClickListenerOfOperationPart;
import com.ljc.picturecut.tools.ImageFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class EditActivity extends AppCompatActivity {
    public static EditActivity self;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar_edit));
        self = this;
        //准备工作
        prepare();
    }

    /*
    静态方法，用于在其他活动中启动此活动，并传递必须的参数
     */
    public static void actionStartForResult(Activity activity, String imagePath){
        Intent intent = new Intent(activity,EditActivity.class);
        intent.putExtra("imagePath",imagePath);
        activity.startActivityForResult(intent,2);
    }

    //准备工作
    private void prepare(){
        //给默认操作板里的控件设置监听
        findViewById(R.id.image_in_operationpart).setOnClickListener(new OnClickListenerOfOperationPart.OnClickListenerOfChoosedImage(EditActivity.this));
        findViewById(R.id.button_in_operationpart).setOnClickListener(new OnClickListenerOfOperationPart.OnClickListenerOfCutButton(EditActivity.this));
        findViewById(R.id.button_add_edittext_x).setOnClickListener(new OnClickListenerOfOperationPart.OnClickListenerOfAddEditTextButton(EditActivity.this));
        findViewById(R.id.button_add_edittext_y).setOnClickListener(new OnClickListenerOfOperationPart.OnClickListenerOfAddEditTextButton(EditActivity.this));
        findViewById(R.id.button_reduce_edittext_x).setOnClickListener(new OnClickListenerOfOperationPart.OnClickListenerOfReduceEditTextButton(EditActivity.this));
        findViewById(R.id.button_reduce_edittext_y).setOnClickListener(new OnClickListenerOfOperationPart.OnClickListenerOfReduceEditTextButton(EditActivity.this));
        findViewById(R.id.button_add_operation).setOnClickListener(new OnClickListenerOfOperationPart.OnClickListenerOfAddOperationButton(EditActivity.this));
        findViewById(R.id.button_ok).setOnClickListener(new OnClickListenerOfOperationPart.OnClickListenerOfOKButton(EditActivity.this));
        findViewById(R.id.bar_ok).setOnClickListener(new OnClickListenerOfOperationPart.OnClickListenerOfOKButton(EditActivity.this));
        findViewById(R.id.button_reduce_operation).setOnClickListener(new OnClickListenerOfOperationPart.OnClickListenerOfReduceOperationButton(EditActivity.this));
        findViewById(R.id.bar_reduce_operation).setOnClickListener(new OnClickListenerOfOperationPart.OnClickListenerOfReduceOperationButton(EditActivity.this));
        findViewById(R.id.bar_add_operation).setOnClickListener(new OnClickListenerOfOperationPart.OnClickListenerOfAddOperationButton(EditActivity.this));

        //一进入编辑活动，就解析并处理主活动传递过来的图片路径
        Intent intent = getIntent();
        String srcImagePath = intent.getStringExtra("imagePath");
        File file = new File(srcImagePath);
        String fileName = file.getName();
        //先更新预览图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;      //只加载信息，不加载图片内容到内存
        Bitmap bitmap = BitmapFactory.decodeFile(srcImagePath,options);
        int oldHeight = options.outHeight;
        int oldWidth = options.outWidth;
        int sampleSize =1;       //压缩倍数
        boolean needToSample = false;       //是否需要压缩
        if (oldWidth>1080){
            sampleSize = oldWidth/520;
            needToSample = true;
        }else if (oldHeight>1920){
            sampleSize = oldHeight/1040;
            needToSample = true;
        }
        if (needToSample){
            BitmapFactory.Options options1 = new BitmapFactory.Options();
            options1.inJustDecodeBounds = false;
            options1.inSampleSize = sampleSize;
            bitmap = BitmapFactory.decodeFile(srcImagePath,options1);
        }else {
            bitmap = BitmapFactory.decodeFile(srcImagePath);
        }
        Image image = new Image();
        image.setSelfImage(bitmap);
        image = ImageFactory.adaptPreviewImage(image);
        ((ImageView)findViewById(R.id.image_preview)).setImageBitmap(image.getSelfImage());
        loadBitmapToImageView(BitmapFactory.decodeFile(srcImagePath),(ImageView)findViewById(R.id.image_in_operationpart));
        Image srcImage = new Image();
        srcImage.setSuperImage(null);
        srcImage.setSelfImage(BitmapFactory.decodeFile(srcImagePath));
        srcImage.setChildImages(null);
        srcImage.setX(0);
        srcImage.setY(0);
        int index = fileName.indexOf(".");
        fileName = fileName.substring(0,index);
        srcImage.setImageName(fileName);
        Log.d("ljc",fileName);
        AllImage.getObject().addImage(srcImage);    //往全部图片中添加源图片
        //设置默认的操作板和操作
        LinearLayout defaultOperationBar = (LinearLayout)findViewById(R.id.layoutOfOperationBar);
        defaultOperationBar.setTag(0);      //设置操作板的标签
        Operation defaultOperation = new Operation();
        defaultOperation.setSrcImage(srcImage);
        defaultOperation.setNumber(0);
        defaultOperation.setChooseImage(true);
        AllOperation.getObject().addOperation(defaultOperation);
    }

    //一个工具方法，用于将Bitmap对象加载到指定的图片视图上
    private void loadBitmapToImageView(Bitmap bitmap,ImageView imageView){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        Glide.with(EditActivity.this).load(bytes).into(imageView);
    }

    @Override
    public void finish() {
        //设置返回数据
        Intent intent = new Intent();
        intent.putExtra("data_return","ok");
        setResult(2,intent);
        //三个都回收
        AllImage.recycle();
        AllOperation.recycle();
        ContextOfEdit.recycle();
        super.finish();
    }
}
