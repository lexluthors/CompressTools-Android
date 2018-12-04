package net.bither.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * description:
 * date: 2017/12/25 10:05
 */
public class CompressTools
{
	private static volatile CompressTools INSTANCE;

	private Context context;
	/**
	 * 最大宽度，默认为720
	 */
	private int maxWidth = 720;
	/**
	 * 最大高度,默认为960
	 */
	private int maxHeight = 960;
	/**
	 * 默认压缩后的方式为JPEG
	 */
	private Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;

	/**
	 * 默认的图片处理方式是ARGB_8888
	 */
	private Bitmap.Config bitmapConfig = Bitmap.Config.ARGB_8888;
	/**
	 * 默认压缩质量为60,60足够清晰了。可以对比一下。可以自定义
	 */
	private int quality = 60;
	/**
	 * 默认压缩质量是否为最优，默认为true
	 */
	private boolean optimize = true;
	/**
	 * 是否使用原图分辨率，默认为false
	 */
	private boolean keepResolution = false;
	/**
	 * 存储路径
	 */
	private String destinationDirectoryPath;
	/**
	 * 文件名前缀
	 */
	private String fileNamePrefix;
	/**
	 * 文件名
	 */
	private String fileName;

	public static CompressTools getInstance(Context context)
	{
		if (INSTANCE == null)
		{
			synchronized (CompressTools.class)
			{
				if (INSTANCE == null)
				{
					INSTANCE = new CompressTools(context);
				}
			}
		}
		return INSTANCE;
	}

	public static Builder newBuilder(Context context)
	{
		return new Builder(context);
	}

	private CompressTools(Context context)
	{
		this.context = context;
		destinationDirectoryPath = context.getCacheDir().getPath() + File.pathSeparator + FileUtil.FILES_PATH;
	}

	/**
	 * description:
	 * author: liujie
	 * date: 2017/8/22 18:19
	 */
	public void compressToFile(final File file, final OnCompressListener mOnCompressListener)
	{
		compressImageJni(file, maxWidth, maxHeight, compressFormat, bitmapConfig, quality, destinationDirectoryPath, fileName, optimize, keepResolution,
				mOnCompressListener);
	}

	public void compressToFile(final String filePath, final OnCompressListener mOnCompressListener)
	{
		compressImageJni(new File(filePath), maxWidth, maxHeight, compressFormat, bitmapConfig, quality, destinationDirectoryPath, fileName, optimize, keepResolution,
				mOnCompressListener);
	}

	/**
	 * description:
	 * author: liujie
	 * date: 2017/8/22 18:19
	 */
//	public void compressToBitmap(final File file, final OnCompressListener onCompressBitmapListener)
//	{
//		compressTOBitmapJni(file, maxWidth, maxHeight, compressFormat, bitmapConfig, quality, destinationDirectoryPath, fileName, optimize,
//				onCompressBitmapListener);
//	}

	/**
	 * 采用建造者模式，设置Builder
	 */
	public static class Builder
	{
		private CompressTools compressTools;

		public Builder(Context context)
		{
			compressTools = new CompressTools(context);
		}

		/**
		 * 设置图片最大宽度
		 *
		 * @param maxWidth
		 *            最大宽度
		 */
		public Builder setMaxWidth(int maxWidth)
		{
			compressTools.maxWidth = maxWidth;
			return this;
		}

		/**
		 * 设置图片最大高度
		 *
		 * @param maxHeight
		 *            最大高度
		 */
		public Builder setMaxHeight(int maxHeight)
		{
			compressTools.maxHeight = maxHeight;
			return this;
		}

		/**
		 * 设置压缩的后缀格式
		 */
		public Builder setBitmapFormat(Bitmap.CompressFormat compressFormat)
		{
			compressTools.compressFormat = compressFormat;
			return this;
		}

		/**
		 * 设置是否开启压缩最优化
		 */
		public Builder setOptimize(boolean optimize)
		{
			compressTools.optimize = optimize;
			return this;
		}

		/**
		 * 设置Bitmap的参数
		 */
		public Builder setBitmapConfig(Bitmap.Config bitmapConfig)
		{
			compressTools.bitmapConfig = bitmapConfig;
			return this;
		}

		/**
		 * 设置分辨率是否保持原图分辨率
		 */
		public Builder setKeepResolution(boolean keepResolution)
		{
			compressTools.keepResolution = keepResolution;
			return this;
		}

		/**
		 * 设置压缩质量，建议50,50就足够了，采用底层压缩，最优解决方案
		 *
		 * @param quality
		 *            压缩质量，[0,100]
		 */
		public Builder setQuality(int quality)
		{
			compressTools.quality = quality;
			return this;
		}

		/**
		 * 设置目的存储路径
		 *
		 * @param destinationDirectoryPath
		 *            目的路径
		 */
		public Builder setDestinationDirectoryPath(String destinationDirectoryPath)
		{
			compressTools.destinationDirectoryPath = destinationDirectoryPath;
			return this;
		}

		/**
		 * 设置文件前缀
		 *
		 * @param prefix
		 *            前缀
		 */
		public Builder setFileNamePrefix(String prefix)
		{
			compressTools.fileNamePrefix = prefix;
			return this;
		}

		/**
		 * 设置文件名称
		 *
		 * @param fileName
		 *            文件名
		 */
		public Builder setFileName(String fileName)
		{
			compressTools.fileName = fileName;
			return this;
		}

		public CompressTools build()
		{
			return compressTools;
		}
	}

	public interface OnCompressBitmapListener
	{
		void onStart();

		void onSuccess(Bitmap bitmap);

		void onFail(String error);
	}

	public interface OnCompressListener<T>
	{

		void onStart();

		void onFail(String error);

		void onSuccess(T t);
	}

	// public static void compressImageJni(Context context, File file, int maxWidth, int maxHeight,
	// Bitmap.CompressFormat compressFormat,
	// Bitmap.Config bitmapConfig, int quality, String parentPath, String prefix, String fileName,
	// boolean optimize, boolean keepResolution,
	// final CompressTools.OnCompressListener mOnCompressListener) {
	// final String filename = generateFilePath(parentPath, compressFormat.name().toLowerCase(),
	// fileName);
	// FileUtil.runOnUiThread(new Runnable() {
	// @Override
	// public void run() {
	// mOnCompressListener.onStart();
	// }
	// });
	// Bitmap newBmp = readBitMap(file.getAbsolutePath());
	// NativeUtil.saveBitmapWithMaxWH(newBmp, quality, filename, optimize, maxWidth,
	// maxHeight,keepResolution);
	// FileUtil.runOnUiThread(new Runnable() {
	// @Override
	// public void run() {
	// if (FileUtil.fileIsExists(filename)) {
	// mOnCompressListener.onSuccess(new File(filename));
	// } else {
	// mOnCompressListener.onFail("创建文件失败");
	// }
	// }
	// });
	// }
	public static void compressImageJni(final File file, final int maxWidth, final int maxHeight, Bitmap.CompressFormat compressFormat,
			Bitmap.Config bitmapConfig, final int quality, String parentPath, String fileName, final boolean optimize, final boolean keepResolution,
			final CompressTools.OnCompressListener mOnCompressListener)
	{
		final Message message = Message.obtain();
		onListener = mOnCompressListener;
		mOnCompressListener.onStart();
		final String filename = generateFilePath(parentPath, compressFormat.name().toLowerCase(), fileName);
		FileUtil.runOnSubThread(new Runnable()
		{
			@Override
			public void run()
			{
				File newFile = null;
				Bitmap newBmp = readBitMap(file.getAbsolutePath());
				if (null == newBmp)
				{
					message.what = FAIL;
					message.obj = "读取图片失败";
					mHandler.sendMessage(message);
					return;
				}
				NativeUtil.saveBitmapWithMaxWH(newBmp, quality, filename, optimize, maxWidth, maxHeight, keepResolution);
				if (FileUtil.fileIsExists(filename))
				{
					newFile = new File(filename);
					message.what = SUCCESS;
					message.obj = newFile;
					mHandler.sendMessage(message);
				}
				else
				{
					message.what = FAIL;
					message.obj = "创建文件失败";
					mHandler.sendMessage(message);
					return;
				}
			}
		});
	}

	public static void compressTOBitmapJni(final File file, final int maxWidth, final int maxHeight, Bitmap.CompressFormat compressFormat, Bitmap.Config bitmapConfig,
										   final int quality, String parentPath, String fileName, final boolean optimize, final CompressTools.OnCompressListener mOnCompressBitmapListener)
	{
		final Message message = Message.obtain();
		onListener = mOnCompressBitmapListener;
		mOnCompressBitmapListener.onStart();
		final String filename = generateFilePath(parentPath, compressFormat.name().toLowerCase(), fileName);
		FileUtil.runOnSubThread(new Runnable() {
			@Override
			public void run() {
				File newFile = null;
				Bitmap newBmp = readBitMap(file.getAbsolutePath());
				if (null == newBmp)
				{
					message.what = FAIL;
					message.obj = "读取图片失败";
					mHandler.sendMessage(message);
					return;
				}
				NativeUtil.saveBitmapWithMaxWH(newBmp, quality, filename, optimize, maxWidth, maxHeight, true);
				if (FileUtil.fileIsExists(filename))
				{
					newFile = new File(filename);
					message.what = SUCCESS;
					message.obj = readBitMap(newFile.getAbsolutePath());
					mHandler.sendMessage(message);
				}
				else
				{
					message.what = FAIL;
					message.obj = "创建文件失败";
					mHandler.sendMessage(message);
					return;
				}
			}
		});
	}

	private static String generateFilePath(String parentPath, String extension, String fileName)
	{
		File file = new File(parentPath);
		if (!file.exists())
		{
			file.mkdirs();
		}
		return file.getAbsolutePath() + File.separator + fileName + "." + extension;
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

	public static final int FAIL = 0;
	public static final int DOWNLOAD_PROGRESS = 2;
	public static final int SUCCESS = 1;
	public static CompressTools.OnCompressListener onListener;
	private static Handler mHandler = new Handler(Looper.getMainLooper())
	{
		@Override
		public void handleMessage(final Message msg)
		{
			super.handleMessage(msg);
			switch (msg.what)
			{
				case FAIL:
					onListener.onFail((String) msg.obj);
					break;
				case SUCCESS:
					if(msg.obj instanceof File){
						onListener.onSuccess((File) msg.obj);
					}else if(msg.obj instanceof Bitmap){
						onListener.onSuccess((Bitmap) msg.obj);
					}
					break;
			}
		}
	};
}
