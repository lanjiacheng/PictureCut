package com.ljc.picturecut.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.ljc.picturecut.EditActivity;
import com.ljc.picturecut.R;
import com.ljc.picturecut.bean.AllOperation;
import com.ljc.picturecut.bean.ContextOfEdit;
import com.ljc.picturecut.bean.Image;
import com.ljc.picturecut.bean.Operation;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created by Administrator on 2018/1/16 0016.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private Context mContent;
    private Dialog mDialog;
    private List<Image> mImageList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView;
            imageView = (ImageView)itemView.findViewById(R.id.image_to_choose);
        }
    }

    public ImageAdapter(List<Image> imageList,Dialog dialog){
        mImageList = imageList;
        mDialog = dialog;
    }

    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContent == null){
            mContent = parent.getContext();
        }
        View view = LayoutInflater.from(mContent).inflate(R.layout.item_chooseimage,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Image image = mImageList.get(position);
                //获取当前点击的图片
                ImageView currentImageView = ContextOfEdit.getObject().getCurrentClickedImageView();
                //获取操作板的视图
                LinearLayout parentOfImage = (LinearLayout)currentImageView.getParent();
                LinearLayout layoutOfOperationBar = (LinearLayout)parentOfImage.getParent();
                int n = (Integer) layoutOfOperationBar.getTag();    //获取操作板的标签，该标签在添加操作时设置
                //操作板的标签与操作的序号是对应的
                Operation operation = AllOperation.getObject().getOperationByNumber(n);     //获取相应操作
                if(!operation.isRuned()) {      //如果对应操作没有执行过，就设置
                    loadBitmapToImageView(image.getSelfImage(), currentImageView);   //加载选择的图片到当前点击图片控件
                    operation.setSrcImage(image);       //把选择的图片传给操作
                    operation.setChooseImage(true);     //将操作标记为已选择图片
                }
                //选择完毕，将对话框关闭
                mDialog.dismiss();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ImageAdapter.ViewHolder holder, int position) {
        Image image = mImageList.get(position);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = image.getSelfImage();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        Glide.with(mContent).load(bytes).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }
    //一个工具方法，用于将Bitmap对象加载到指定的图片视图上
    private void loadBitmapToImageView(Bitmap bitmap,ImageView imageView){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        Glide.with(mContent).load(bytes).into(imageView);
    }
}
