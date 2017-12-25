package com.jaywei.compresstools_android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.bither.util.CompressTools;
import net.bither.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

import static net.bither.util.FileUtil.getReadableFileSize;
import static net.bither.util.FileUtil.renameFile;

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
		// CompressTools.getInstance(this).compressToFileJni(oldFile, new CompressTools.OnCompressListener()
		// {
		// @Override
		// public void onStart()
		// {
		//
		// }
		//
		// @Override
		// public void onFail(String error)
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

		// for (int i = 0; i < 5; i++)
		// {
		//   CompressTools.newBuilder(this)
		// // .setMaxWidth(1280) // 默认最大宽度为720
		// // .setMaxHeight(850) // 默认最大高度为960
		// .setQuality(50) // 默认压缩质量为60,60足够清晰
		// .setCompressFormat(Bitmap.CompressFormat.JPEG) // 设置默认压缩为jpg格式
		// .setKeepResolution(true)// 设置保持原图分辨率，则设置的最大宽高就无效了。不需要设置最大宽高了。设置也不会报错了，该参数默认false
		// .setFileName("testasdfas").setDestinationDirectoryPath(FileUtil.getPhotoFileDir().getAbsolutePath()).build()
		// .compressToFileJni(oldFile, new CompressTools.OnCompressListener()
		// {
		// @Override
		// public void onStart()
		// {
		//
		// }
		//
		// @Override
		// public void onFail(String error)
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
		// }

		CompressTools.newBuilder(this)
				// .setMaxWidth(1280) // 默认最大宽度为720
				// .setMaxHeight(850) // 默认最大高度为960
				.setQuality(50) // 默认压缩质量为60,60足够清晰
				.setBitmapFormat(Bitmap.CompressFormat.JPEG) // 设置默认压缩为jpg格式
				.setKeepResolution(false)// 设置保持原图分辨率，则设置的最大宽高就无效了。不需要设置最大宽高了。设置也不会报错了，该参数默认false
				.setFileName("test123").setDestinationDirectoryPath(FileUtil.getPhotoFileDir().getAbsolutePath()).build()
				.compressToFileJni(oldFile, new CompressTools.OnCompressListener()
				{
					@Override
					public void onStart()
					{

					}

					@Override
					public void onFail(String error)
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

		// // 压缩bitmap
		// CompressTools.getInstance(this).compressToBitmapJni(oldFile, new
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
		//
		// @Override
		// public void onFail(String error)
		// {
		//
		// }
		// });

		// CompressTools.newBuilder(this).setMaxWidth(500) // 默认最大宽度为720
		// .setMaxHeight(600) // 默认最大高度为960
		// .setQuality(50) // 默认压缩质量为60,60足够清晰
		// .setBitmapFormat(Bitmap.CompressFormat.JPEG) // 设置默认压缩为jpg格式
		// .build().compressToBitmapJni(oldFile, new CompressTools.OnCompressBitmapListener()
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
		// // 这里读取的是bitmap的大小，所以会显示比老图片读取的file大小要大
		// mainTextNew.setText(String.format("Size : %s", getReadableFileSize(bitmap.getByteCount())));
		// }
		//
		// @Override
		// public void onFail(String error)
		// {
		//
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
				showError("打开失败");
				return;
			}
			try
			{
				oldFile = getTempFile(this, data.getData());
				mainImageOld.setImageBitmap(BitmapFactory.decodeFile(oldFile.getAbsolutePath()));
				mainTextOld.setText(String.format("Size : %s", getReadableFileSize(oldFile.length())));
			}
			catch (IOException e)
			{
				showError("读图失败");
				e.printStackTrace();
			}
		}
	}

	public void showError(String errorMessage)
	{
		Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
	}

	static int copy(InputStream input, OutputStream output) throws IOException
	{
		long count = copyLarge(input, output);
		if (count > Integer.MAX_VALUE)
		{
			return -1;
		}
		return (int) count;
	}

	private static final int EOF = -1;
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	static long copyLarge(InputStream input, OutputStream output) throws IOException
	{
		return copyLarge(input, output, new byte[DEFAULT_BUFFER_SIZE]);
	}

	static long copyLarge(InputStream input, OutputStream output, byte[] buffer) throws IOException
	{
		long count = 0;
		int n;
		while (EOF != (n = input.read(buffer)))
		{
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	/**
	 * 获取临时文件
	 *
	 * @param context
	 *            上下文
	 * @param uri
	 *            url
	 * @return 临时文件
	 * @throws IOException
	 */
	public static File getTempFile(Context context, Uri uri) throws IOException
	{
		InputStream inputStream = context.getContentResolver().openInputStream(uri);
		String fileName = FileUtil.getFileName(context, uri);
		String[] splitName = FileUtil.splitFileName(fileName);
		File tempFile = File.createTempFile(splitName[0], splitName[1]);
		tempFile = renameFile(tempFile, fileName);
		tempFile.deleteOnExit();
		FileOutputStream out = null;
		try
		{
			out = new FileOutputStream(tempFile);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		if (inputStream != null)
		{
			copy(inputStream, out);
			inputStream.close();
		}

		if (out != null)
		{
			out.close();
		}
		return tempFile;
	}
}
