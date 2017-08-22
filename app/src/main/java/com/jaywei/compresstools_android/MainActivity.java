package com.jaywei.compresstools_android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.bither.util.NativeUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import news.jaywei.com.compresslib.ImageUtils;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.compress)
    TextView compress;
    @BindView(R.id.image1)
    ImageView image1;
    @BindView(R.id.image2)
    ImageView image2;
    @BindView(R.id.activity_main)
    LinearLayout activityMain;
    Bitmap bmp2;
    @BindView(R.id.size)
    TextView size;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        bmp2 = BitmapFactory.decodeResource(getResources(), R.mipmap.fengjing2);
////        image1.setImageBitmap(bmp2);
//        Log.e("getHeight》》》", bmp2.getHeight() + "");
//        Log.e("getWidth》》》", bmp2.getWidth() + "");
    }


    /**
     * description:
     * author: liujie
     * date: 2017/8/21 15:26
     */
    private Boolean saveBitmapToCamera(Context context, Bitmap bm, String name) {
        if (name == null) {
            // 文件名为空报错失败
            return false;
        }
        final File file = new File(name);
        ;
        if (file.exists()) {
            file.delete();
        }
        int quality = 50;
        // 压缩保存
        NativeUtil.compressBitmap(bm, quality, file.getAbsolutePath(), true);

        Log.e("saveBitmapToCamera>>>>>", "saveBitmapToCamera");
        // 释放内存，回收bitmap
        bm.recycle();
        bm = null;
        // 广播通知扫描文件，可以在图库中显示
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        sendBroadcast(intent);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                size.setText(FileSizeUtil.getAutoFileOrFilesSize(file.getAbsolutePath()));
            }
        });
        return true;
    }

    @OnClick(R.id.compress)
    public void onViewClicked() {
        Intent intent = new Intent(MainActivity.this, Main2Activity.class);
        startActivity(intent);
//        if (bmp2 == null) {
//            Toast.makeText(MainActivity.this, "图片为空", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                saveBitmapToCamera(MainActivity.this, bmp2, getPathRoad());
//            }
//        }).start();
    }

    /**
     * @Title: getPathRoad
     * @Description: TODO(使用userid加addtime作为分享大图地址路径) liujie
     * @date 2016-3-10 下午6:57:11
     */
    private String getPathRoad() {
        String path = ImageUtils.getPhotoFileDir() + "/" + System.currentTimeMillis()
                + ".jpg";
        Log.e("path>>>>>", path);
        return path;
    }


    public void takePhoto(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

}
