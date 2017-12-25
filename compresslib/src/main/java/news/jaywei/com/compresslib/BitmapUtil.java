package news.jaywei.com.compresslib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import net.bither.util.NativeUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class BitmapUtil
{

	public static void compressImageJni(Context context, Uri imageUri, int maxWidth, int maxHeight, Bitmap.CompressFormat compressFormat,
			Bitmap.Config bitmapConfig, int quality, String parentPath, String prefix, String fileName, boolean optimize, boolean keepResolution,
			final CompressTools.OnCompressListener mOnCompressListener)
	{
		final String filename = generateFilePath(context, parentPath, imageUri, compressFormat.name().toLowerCase(), prefix, fileName);
		FileUtil.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				mOnCompressListener.onStart();
			}
		});
		Bitmap newBmp;
		if (keepResolution)
		{
			newBmp = readBitMap(FileUtil.getRealPathFromURI(context, imageUri));
		}
		else
		{
			NativeUtil.saveBitmapWithMaxWH(readBitMap(FileUtil.getRealPathFromURI(context, imageUri)), quality, filename, optimize, maxWidth, maxHeight);
		}
		FileUtil.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				if (FileUtil.fileIsExists(filename))
				{
					mOnCompressListener.onSuccess(new File(filename));
				}
				else
				{
					mOnCompressListener.onFail("创建文件失败");
				}
			}
		});
	}

	public static void compressTOBitmapJni(final Context context, final Uri imageUri, int maxWidth, int maxHeight, Bitmap.CompressFormat compressFormat,
			Bitmap.Config bitmapConfig, int quality, String parentPath, String prefix, String fileName, boolean optimize,
			final CompressTools.OnCompressBitmapListener mOnCompressBitmapListener)
	{
		final String filename = generateFilePath(context, parentPath, imageUri, compressFormat.name().toLowerCase(), prefix, fileName);
		FileUtil.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				mOnCompressBitmapListener.onStart();
			}
		});
		Bitmap newBmp = readBitMap(FileUtil.getRealPathFromURI(context, imageUri));
		if (newBmp != null)
		{
			NativeUtil.saveBitmapWithMaxWH(newBmp, quality, filename, optimize, maxWidth, maxHeight);
		}
		FileUtil.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				if (FileUtil.fileIsExists(filename))
				{
					mOnCompressBitmapListener.onSuccess(readBitMap(filename));
				}
				else
				{
					mOnCompressBitmapListener.onFail("创建文件失败");
				}
			}
		});
	}

	private static String generateFilePath(Context context, String parentPath, Uri uri, String extension, String prefix, String fileName)
	{
		File file = new File(parentPath);
		if (!file.exists())
		{
			file.mkdirs();
		}
		return file.getAbsolutePath() + File.separator + System.currentTimeMillis() + "." + extension;
	}

	/**
	 * 以最省内存的方式读取本地资源的图片
	 */
	public static Bitmap readBitMap(String path)
	{
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		File file = new File(path);
		FileInputStream is;
		try
		{
			is = new FileInputStream(file);
			return BitmapFactory.decodeStream(is, null, opt);
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
