package com.ljc.picturecut.bean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.NetworkOnMainThreadException;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ljc.picturecut.MainActivity;
import com.ljc.picturecut.R;
import com.ljc.picturecut.database.Project;
import com.ljc.picturecut.listener.OnClickListenerOfThreeButton;
import com.ljc.picturecut.tools.ImageFactory;

import java.io.File;

/**
 * Created by Administrator on 2018/1/19 0019.
 * 本类代表打开项目的窗口，用来展示以一个打开的项目
 */

public class OpenProjectWindow {
    private TextView text_name;
    private TextView text_time;
    private TextView text_path;
    private Button button_open;
    private Button button_delete;
    private Button button_share;
    private ImageView image_open;
    private FrameLayout window;
    private static OpenProjectWindow self;
    private Context mContent;

    private OpenProjectWindow(Context context){
        self = this;
        mContent = context;
        text_name = (TextView)((MainActivity)mContent).findViewById(R.id.text_name_in_main);
        text_time = (TextView)((MainActivity)mContent).findViewById(R.id.text_time_in_main);
        text_path = (TextView)((MainActivity)mContent).findViewById(R.id.text_path_in_main);
        button_open = (Button)((MainActivity)mContent).findViewById(R.id.button_open_in_main);
        button_delete = (Button)((MainActivity)mContent).findViewById(R.id.button_delete_in_main);
        button_share = (Button)((MainActivity)mContent).findViewById(R.id.button_share_in_main);
        image_open = (ImageView) ((MainActivity)mContent).findViewById(R.id.image_open_in_main);
        window = (FrameLayout)((MainActivity)mContent).findViewById(R.id.window_open_image);
        button_open.setOnClickListener(new OnClickListenerOfThreeButton(mContent));
        button_delete.setOnClickListener(new OnClickListenerOfThreeButton(mContent));
        button_share.setOnClickListener(new OnClickListenerOfThreeButton(mContent));
    }
    public static OpenProjectWindow getObject(Context context){
        if (self == null){
            return new OpenProjectWindow(context);
        }
        return self;
    }

    public void open(){
        Project project = AllProject.getObject().getCurrentOpenProject();
        if (project==null){
            return;
        }
        if (window.getVisibility() == View.GONE){
            window.setVisibility(View.VISIBLE);
        }
        FrameLayout part_add = (FrameLayout)((MainActivity)mContent).findViewById(R.id.part_add);
        if (part_add.getVisibility()==View.VISIBLE){
            part_add.setVisibility(View.GONE);
        }
        File dirOfProject = new File(project.getPath());
        boolean ok = false;
        boolean isExist = dirOfProject.exists();
        if (isExist){
            ok = true;
            int count = (dirOfProject.listFiles()).length-1;
            if (project.getCount()>count){
                ok = false;
            }
            if (!ok){
                Toast.makeText(mContent,"该项目部分文件缺失，建议删除",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(mContent,"该项目部分文件缺失，建议删除",Toast.LENGTH_SHORT).show();
        }
        Bitmap bitmap = BitmapFactory.decodeFile(project.getPath()+"/preview.png");
        Image image = new Image();
        image.setSelfImage(bitmap);
        image = ImageFactory.adaptPreviewImage(image);
        image_open.setImageBitmap(image.getSelfImage());
        text_name.setText(project.getName());
        text_time.setText(project.getTime());
        text_path.setText(project.getPath());
    }

    public void close(){
        if (window.getVisibility() == View.VISIBLE){
            window.setVisibility(View.GONE);
        }
        FrameLayout part_add = (FrameLayout)((MainActivity)mContent).findViewById(R.id.part_add);
        if (part_add.getVisibility()==View.GONE){
            part_add.setVisibility(View.VISIBLE);
        }
    }

    public static void recycle(){
        self =null;
    }
}
