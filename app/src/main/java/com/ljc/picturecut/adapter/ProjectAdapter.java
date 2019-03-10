package com.ljc.picturecut.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ljc.picturecut.EditActivity;
import com.ljc.picturecut.R;
import com.ljc.picturecut.bean.AllProject;
import com.ljc.picturecut.bean.OpenProjectWindow;
import com.ljc.picturecut.database.Project;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/19 0019.
 */

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder>{
    private Context mContent;
    private List<Project> projectList;

    class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout layout;
        ImageView imageView;
        TextView text_name;
        TextView text_time;
        TextView text_path;
        Button button_open;
        public ViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout)itemView;
            imageView = (ImageView) itemView.findViewById(R.id.image_in_projectBar);
            text_name = (TextView)itemView.findViewById(R.id.text_name_in_projectBar);
            text_time = (TextView)itemView.findViewById(R.id.text_time_in_projectBar);
            text_path = (TextView)itemView.findViewById(R.id.text_path_in_projectBar);
            button_open = (Button)itemView.findViewById(R.id.button_in_projectBar);
        }
    }
    public ProjectAdapter(List<Project> projects){
        projectList = projects;
    }

    @Override
    public ProjectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContent == null){
            mContent = parent.getContext();
        }
        View view = LayoutInflater.from(mContent).inflate(R.layout.item_project,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.button_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击打开时的处理逻辑
                AllProject.getObject().setCurrentOpenProject(projectList.get(projectList.size()-holder.getAdapterPosition()-1));
                OpenProjectWindow.getObject(mContent).open();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ProjectAdapter.ViewHolder holder, int position) {
        //设置每一项的显示内容
        Project project = projectList.get(projectList.size()-position-1);
        holder.text_name.setText(project.getName());
        holder.text_time.setText(project.getTime());
        holder.text_path.setText(project.getPath()+"/");
        Bitmap bitmap = BitmapFactory.decodeFile(project.getPath()+"/preview.png");
        if (bitmap!=null){
            loadBitmapToImageView(bitmap,holder.imageView);
        }else {
            Glide.with(mContent).load(R.drawable.img_null).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    //一个工具方法，用于将Bitmap对象加载到指定的图片视图上
    private void loadBitmapToImageView(Bitmap bitmap, ImageView imageView){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        Glide.with(mContent).load(bytes).into(imageView);
    }
}
