package com.jaywei.compresstools_android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import news.jaywei.com.compresslib.CompressTools;
import news.jaywei.com.compresslib.FileUtil;

import static news.jaywei.com.compresslib.FileUtil.getReadableFileSize;

public class MainActivity extends AppCompatActivity
{
	@BindView(R.id.main_image_old)
	ImageView mainImageOld;
	@BindView(R.id.main_text_old)
	TextView mainTextOld;
	@BindView(R.id.main_image_new)
	ImageView mainImageNew;
	@BindView(R.id.main_text_new)
	TextView mainTextNew;

	private static final int PICK_IMAGE_REQUEST = 1;

	private File oldFile;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);
		ButterKnife.bind(this);
	}

	public void compress(View view)
	{
		// 压缩本地图片，返回新的file
		// CompressTools.getDefault(this).compressToFileJni(oldFile, new CompressTools.OnCompressListener()
		// {
		// @Override
		// public void onStart()
		// {
		//
		// }
		//
		// @Override
		// public void onSuccess(File file)
		// {
		// Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
		// mainImageNew.setImageBitmap(bitmap);
		// mainTextNew.setText(String.format("Size : %s", getReadableFileSize(file.length())));
		// }
		// });
		for (int i = 0; i < 5; i++)
		{
			new CompressTools.Builder(this).setMaxWidth(1280) // 默认最大宽度为720
					.setMaxHeight(850) // 默认最大高度为960
					.setQuality(50) // 默认压缩质量为60,60足够清晰
					.setCompressFormat(Bitmap.CompressFormat.JPEG) // 设置默认压缩为jpg格式
					// .setKeepResolution(true)//设置保持原图分辨率，则设置的最大宽高就无效了。不需要设置最大宽高了。设置也不会报错了，该参数默认false
					.setFileName("test"+i).setDestinationDirectoryPath(FileUtil.getPhotoFileDir().getAbsolutePath()).build()
					.compressToFileJni(oldFile, new CompressTools.OnCompressListener()
					{
						@Override
						public void onStart()
						{

						}

						@Override
						public void onSuccess(File file)
						{
							Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
							mainImageNew.setImageBitmap(bitmap);
							mainTextNew.setText(String.format("Size : %s", getReadableFileSize(file.length())));
						}
					});
		}

		// // 压缩bitmap
		// CompressTools.getDefault(this).compressToBitmapJni(oldFile, new
		// CompressTools.OnCompressBitmapListener()
		// {
		// @Override
		// public void onStart()
		// {
		//
		// }
		//
		// @Override
		// public void onSuccess(Bitmap bitmap)
		// {
		//
		// }
		// });

		// new CompressTools.Builder(this).setMaxWidth(1080) // 默认最大宽度为720
		// .setMaxHeight(1920) // 默认最大高度为960
		// .setQuality(50) // 默认压缩质量为60,60足够清晰
		// .setCompressFormat(Bitmap.CompressFormat.JPEG) // 设置默认压缩为jpg格式
		// .setFileName("test2").setDestinationDirectoryPath(FileUtil.getPhotoFileDir().getAbsolutePath()).build()
		// .compressToBitmapJni(oldFile, new CompressTools.OnCompressBitmapListener()
		// {
		// @Override
		// public void onStart()
		// {
		//
		// }
		//
		// @Override
		// public void onSuccess(Bitmap bitmap)
		// {
		// mainImageNew.setImageBitmap(bitmap);
		// mainTextNew.setText(String.format("Size : %s", getReadableFileSize(bitmap.getByteCount())));
		// }
		// });
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
				mainImageOld.setImageBitmap(BitmapFactory.decodeFile(oldFile.getAbsolutePath()));
				mainTextOld.setText(String.format("Size : %s", getReadableFileSize(oldFile.length())));
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
		mainImageOld.setBackgroundColor(getRandomColor());
		mainImageNew.setImageDrawable(null);
		mainImageNew.setBackgroundColor(getRandomColor());
		mainTextNew.setText("Size : -");
	}

}
