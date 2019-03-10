package com.ljc.picturecut.listener;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.ljc.picturecut.R;
import com.ljc.picturecut.bean.AllProject;
import com.ljc.picturecut.bean.ContentOfMain;
import com.ljc.picturecut.bean.OpenProjectWindow;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018/1/19 0019.
 */

public class OnClickListenerOfThreeButton implements View.OnClickListener {
    private Context mContent;

    public OnClickListenerOfThreeButton(Context context) {
        mContent = context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_open_in_main: {
                //点击打开时的操作
                File file = new File(AllProject.getObject().getCurrentOpenProject().getPath());
                if (null == file || !file.exists()) {
                    Toast.makeText(mContent, "目录不存在", Toast.LENGTH_SHORT).show();
                    return;
                }
//                ContentValues contentValues = new ContentValues(1);
//                contentValues.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
//                Uri uri = mContent.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                Uri uri;
                if (Build.VERSION.SDK_INT >= 24) {
                    uri = FileProvider.getUriForFile(mContent.getApplicationContext(), "org.litepal.LitePalApplication.fileprovider", file);
                } else {
                    uri = Uri.fromFile(file);
                }
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setDataAndType(uri, "image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                mContent.startActivity(intent);
                try {
                    mContent.startActivity(Intent.createChooser(intent, "打开图片"));
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
            break;
            case R.id.button_delete_in_main: {
                //点击删除时的操作
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContent);
                dialog.setTitle("您即将删除此项目！");
                dialog.setMessage("确定要删除吗：");
                dialog.setCancelable(true);
                dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //是就进行删除
                        try {
                            Runtime.getRuntime().exec("rm -r -f " + AllProject.getObject().getCurrentOpenProject().getPath());
                            Thread.sleep(200);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        AllProject.getObject().deleteProjectByName((AllProject.getObject().getCurrentOpenProject()).getName());
                        AllProject.getObject().setCurrentOpenProject(null);
                        OpenProjectWindow.getObject(mContent).close();
                        ContentOfMain.getObject().getProjectAdapter().notifyDataSetChanged();
                    }
                });
                dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //否
                        return;     //啥也没干
                    }
                });
                dialog.show();
            }
            break;
            case R.id.button_share_in_main: {
                //点击分享时
                ArrayList<Uri> imageUris = getUriOfCurrentProjectImages();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
                intent.setType("image/*");
                mContent.startActivity(Intent.createChooser(intent, "share"));
            }
            break;
            default:
                break;
        }
    }

    //获取要分享的项目的图片Uri集合
    private ArrayList<Uri> getUriOfCurrentProjectImages() {
        ArrayList<Uri> uriArrayList = new ArrayList<>();
        File dirOfCurrentProject = new File(AllProject.getObject().getCurrentOpenProject().getPath());
        for (File file : dirOfCurrentProject.listFiles()) {
            if (file.getName().equals("preview.png")) {       //过滤掉预览图
                continue;
            }
            Uri uri = Uri.fromFile(file);
            uriArrayList.add(uri);          //添加
        }
        return uriArrayList;                //返回
    }
}
