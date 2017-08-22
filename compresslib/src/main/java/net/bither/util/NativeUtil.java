package net.bither.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Log;

/**
 * @author liujie
 */
public class NativeUtil
{
	private static int DEFAULT_QUALITY = 95;

	public static void compressBitmap(Bitmap bit, String fileName, boolean optimize)
	{
		compressBitmap(bit, DEFAULT_QUALITY, fileName, optimize);
	}

	// public static void compressBitmap(Bitmap bit, int quality, String fileName, boolean optimize)
	// {
	// // Bitmap result = null;
	// // result = Bitmap.createBitmap(bit.getWidth() / 2, bit.getHeight() / 2,
	// // Config.ARGB_4444);// 缩小2倍
	// // Canvas canvas = new Canvas(result);
	// // canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG |
	// // Paint.FILTER_BITMAP_FLAG));
	// // Rect rect = new Rect(0, 0, bit.getWidth(), bit.getHeight());// original
	// // rect = new Rect(0, 0, bit.getWidth() / 2, bit.getHeight() / 2);// 缩小2倍
	// // canvas.drawBitmap(bit, null, rect, null);
	// saveBitmap(bit, quality, fileName, optimize);
	// bit.recycle();
	// bit = null;
	// System.gc();
	// }
	//
	// private static void saveBitmap(Bitmap bit, int quality, String fileName, boolean optimize)
	// {
	// compressBitmap(bit, bit.getWidth()/ 2, bit.getHeight()/ 2, quality, fileName.getBytes(),
	// optimize);
	// bit.recycle();
	// bit = null;
	// }
	//
	private static native String compressBitmap(Bitmap bit, int w, int h, int quality, byte[] fileNameBytes, boolean optimize);

	public static void compressBitmap(Bitmap bit, int quality, String fileName, boolean optimize)
	{
		Log.d("native", "compress of native");

		// if (bit.getConfig() != Config.ARGB_8888) {
		Bitmap result = null;

		result = Bitmap.createBitmap(bit.getWidth() / 2, bit.getHeight() / 2, Bitmap.Config.ARGB_8888);// 缩小3倍
		Canvas canvas = new Canvas(result);
		// Rect rect = new Rect(0, 0, bit.getWidth(), bit.getHeight());// original
		Rect rect = new Rect(0, 0, bit.getWidth() / 2, bit.getHeight() / 2);// 缩小3倍
		canvas.drawBitmap(bit, null, rect, null);
		saveBitmap(result, quality, fileName, optimize);
		result.recycle();
		bit = null;
		result = null;
		// } else {
		// saveBitmap(bit, quality, fileName, optimize);
		// }

	}

	private static void saveBitmap(Bitmap bit, int quality, String fileName, boolean optimize)
	{

		compressBitmap(bit, bit.getWidth(), bit.getHeight(), quality, fileName.getBytes(), optimize);

	}
	//
	// private static native String compressBitmap(Bitmap bit, int w, int h,
	// int quality, byte[] fileNameBytes, boolean optimize);

	static
	{
		System.loadLibrary("jpegbither");
		System.loadLibrary("bitherjni");

	}

	/**
	 * @Description: 通过JNI图片压缩把Bitmap保存到指定目录
	 */
	public static void compressBitmap2(Bitmap image, File file)
	{
		// 最大图片大小 100KB
		int maxSize = 200;
		// 获取尺寸压缩倍数
		int ratio = NativeUtil.getRatioSize(image.getWidth(), image.getHeight());
		// 压缩Bitmap到对应尺寸
		Bitmap result = Bitmap.createBitmap(image.getWidth() / ratio, image.getHeight() / ratio, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		Rect rect = new Rect(0, 0, image.getWidth() / ratio, image.getHeight() / ratio);
		canvas.drawBitmap(image, null, rect, null);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		result.compress(Bitmap.CompressFormat.JPEG, options, baos);
		// 循环判断如果压缩后图片是否大于100kb,大于继续压缩
		while (baos.toByteArray().length / 1024 > maxSize)
		{
			// 重置baos即清空baos
			baos.reset();
			// 每次都减少10
			options -= 10;
			// 这里压缩options%，把压缩后的数据存放到baos中
			result.compress(Bitmap.CompressFormat.JPEG, options, baos);
		}
		// JNI调用保存图片到SD卡 这个关键
		saveCompressBitmap(result, options, file);
		// 释放Bitmap
		if (result != null && !result.isRecycled())
		{
			result.recycle();
			result = null;
		}
	}

	/**
	 * 计算缩放比
	 */
	public static int getRatioSize(int bitWidth, int bitHeight)
	{
		// 图片最大分辨率
		int imageHeight = 1920;
		int imageWidth = 1080;
		// 缩放比
		int ratio = 1;
		// 缩放比,由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		if (bitWidth > bitHeight && bitWidth > imageWidth)
		{
			// 如果图片宽度比高度大,以宽度为基准
			ratio = bitWidth / imageWidth;
		}
		else if (bitWidth < bitHeight && bitHeight > imageHeight)
		{
			// 如果图片高度比宽度大，以高度为基准
			ratio = bitHeight / imageHeight;
		}
		// 最小比率为1
		if (ratio <= 0)
			ratio = 1;
		return ratio;
	}

	public static void saveCompressBitmap(Bitmap bitmap, int quality, File originalFile)
	{
		try
		{
			FileOutputStream fileOutputStream = new FileOutputStream(originalFile);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
			bitmap.recycle();
			bitmap = null;
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private File saveBitmapToCamera(Context context, Bitmap bm, String name)
	{
		if (name == null)
		{
			// 文件名为空报错失败
			return null;
		}
		final File file = new File(name);
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
		return file;
	}
}
