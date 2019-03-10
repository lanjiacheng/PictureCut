package com.ljc.picturecut;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ljc.picturecut.adapter.ProjectAdapter;
import com.ljc.picturecut.bean.AllProject;
import com.ljc.picturecut.bean.ContentOfMain;
import com.ljc.picturecut.bean.OpenProjectWindow;
import com.ljc.picturecut.listener.OnClickListenerOfAddImageButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AllProject.getObject().initialize();        //初始化项目数据
        configBar();
        configSomeView();
        configRecycleView();                //配置历史区域的列表
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    OnClickListenerOfAddImageButton.openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4系统版本以上
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                }
            }
            break;
            case 2: {
                OpenProjectWindow.getObject(this).open();
                ContentOfMain.getObject().getProjectAdapter().notifyDataSetChanged();
            }
            break;
            default:
                break;
        }
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        //这里输入对返回的图片路径的处理逻辑
        EditActivity.actionStartForResult(MainActivity.this, imagePath);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/publci_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        //这里输入对返回的图片路径的处理逻辑
        EditActivity.actionStartForResult(MainActivity.this, imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    @Override
    public void finish() {
        OpenProjectWindow.recycle();
        AllProject.recycle();
        ContentOfMain.recycle();
        super.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_project: {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
            }
            break;
            case R.id.open_project: {
                View part_history = findViewById(R.id.part_hisrory);
                View v = findViewById(R.id.button_close_history);
                part_history.setVisibility(View.VISIBLE);
                v.setRotation(0);
            }
            break;
            case R.id.help_main:{
                //帮助界面
                Intent intent = new Intent(MainActivity.this,HelpActivity.class);
                startActivity(intent);
            }break;
            case R.id.about_main:{
                //关于界面
                Intent intent = new Intent(MainActivity.this,AboutActivity.class);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 设置历史区域的适配器
     */
    private void configRecycleView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview_history);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        ProjectAdapter projectAdapter = new ProjectAdapter(AllProject.getObject().getAllProjects());
        recyclerView.setAdapter(projectAdapter);
        ContentOfMain.getObject().setProjectAdapter(projectAdapter);
    }

    /**
     * 设置一些控件的监听器
     */
    private void configSomeView() {
        findViewById(R.id.button_main_part1_add).setOnClickListener(new OnClickListenerOfAddImageButton(MainActivity.this));
        findViewById(R.id.button_close_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View part_history = findViewById(R.id.part_hisrory);
                if (part_history.getVisibility() == View.VISIBLE) {
                    part_history.setVisibility(View.GONE);
                    v.setRotation(180);
                } else {
                    part_history.setVisibility(View.VISIBLE);
                    v.setRotation(0);
                }
            }
        });
        findViewById(R.id.text_detail_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) v;
                if (textView.getText().toString().equals("详细")) {
                    textView.setText("收起");
                    findViewById(R.id.text_information_project).setVisibility(View.VISIBLE);
                } else {
                    textView.setText("详细");
                    findViewById(R.id.text_information_project).setVisibility(View.GONE);
                }
            }
        });
    }

    private void configBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");      //设置intent类型，用于相册来解析
        startActivityForResult(intent, 1);
    }
}