package com.jaywei.compresstools_android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.bither.util.NativeUtil;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import news.jaywei.com.compresslib.FileUtil;
import news.jaywei.com.compresslib.ImageUtils;

import static news.jaywei.com.compresslib.FileUtil.getReadableFileSize;

public class Main2Activity extends AppCompatActivity
{
	private ImageView mImageOld;
	private ImageView mImageNew;

	private static final int PICK_IMAGE_REQUEST = 1;
	private TextView mTextOld;
	private TextView mTextNew;

	private File oldFile;
	private File newFile;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);
		initInstances();

	}

	private void initInstances()
	{
		mImageOld = (ImageView) findViewById(R.id.main_image_old);
		mImageNew = (ImageView) findViewById(R.id.main_image_new);
		mTextOld = (TextView) findViewById(R.id.main_text_old);
		mTextNew = (TextView) findViewById(R.id.main_text_new);
	}

	public void compress(View view)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
                Bitmap bitmap = ImageUtils.readBitMap(oldFile.getAbsolutePath());
                Log.e("getWidth",bitmap.getWidth()+"");
                Log.e("getHeight",bitmap.getHeight()+"");
				newFile =saveBitmapToCamera(Main2Activity.this, bitmap, getPathRoad());
				new Handler(Looper.getMainLooper()).post(new Runnable()
				{
					@Override
					public void run()
					{
						mImageNew.setImageBitmap(BitmapFactory.decodeFile(newFile.getAbsolutePath()));
						mTextNew.setText(String.format("Size : %s", getReadableFileSize(newFile.length())));
					}
				});
//				newFile = BitmapUtil.compressImageJni();
			}
		}).start();
	}

	private String getPathRoad()
	{
		String path = ImageUtils.getPhotoFileDir() + "/" + System.currentTimeMillis() + ".jpg";
		Log.e("path>>>>>", path);
		return path;
	}

	public void takePhoto(View view)
	{
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, PICK_IMAGE_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK)
		{
			if (data == null)
			{
				showError("Failed to open picture!");
				return;
			}
			try
			{
				oldFile = FileUtil.getTempFile(this, data.getData());
				mImageOld.setImageBitmap(BitmapFactory.decodeFile(oldFile.getAbsolutePath()));
				mTextOld.setText(String.format("Size : %s", getReadableFileSize(oldFile.length())));
				clearImage();
			}
			catch (IOException e)
			{
				showError("Failed to read picture data!");
				e.printStackTrace();
			}
		}
	}

	public void showError(String errorMessage)
	{
		Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
	}

	private int getRandomColor()
	{
		Random rand = new Random();
		return Color.argb(100, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
	}

	private void clearImage()
	{
		mImageOld.setBackgroundColor(getRandomColor());
		mImageNew.setImageDrawable(null);
		mImageNew.setBackgroundColor(getRandomColor());
		mTextNew.setText("Size : -");
	}

	private File saveBitmapToCamera(Context context, Bitmap bm, String name)

	{
		if (name == null)
		{
			// 文件名为空报错失败
			return null;
		}
		final File file = new File(name);
		;
		if (file.exists())
		{
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
		return file;
	}
}
