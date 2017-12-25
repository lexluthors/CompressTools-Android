package news.jaywei.com.compresslib;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileUtil
{
	static final String FILES_PATH = "CompressTools";
	private static final int EOF = -1;
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	public static final String PHOTO_PATH = Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + File.separator + "Cherry"
			+ File.separator;

	/**
	 * 重命名文件
	 *
	 * @param file
	 *            文件
	 * @param newName
	 *            新名字
	 * @return 新文件
	 */
	public static File renameFile(File file, String newName)
	{
		File newFile = new File(file.getParent(), newName);
		if (!newFile.equals(file))
		{
			if (newFile.exists())
			{
				if (newFile.delete())
				{
					Log.d("FileUtil", "Delete old " + newName + " file");
				}
			}
			if (file.renameTo(newFile))
			{
				Log.d("FileUtil", "Rename file to " + newName);
			}
		}
		return newFile;
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
		String fileName = getFileName(context, uri);
		String[] splitName = splitFileName(fileName);
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

	/**
	 * 截取文件名称
	 *
	 * @param fileName
	 *            文件名称
	 */
	static String[] splitFileName(String fileName)
	{
		String name = fileName;
		String extension = "";
		int i = fileName.lastIndexOf(".");
		if (i != -1)
		{
			name = fileName.substring(0, i);
			extension = fileName.substring(i);
		}

		return new String[] { name, extension };
	}

	/**
	 * 获取文件名称
	 *
	 * @param context
	 *            上下文
	 * @param uri
	 *            uri
	 * @return 文件名称
	 */
	static String getFileName(Context context, Uri uri)
	{
		String result = null;
		if (uri.getScheme().equals("content"))
		{
			Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
			try
			{
				if (cursor != null && cursor.moveToFirst())
				{
					result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				if (cursor != null)
				{
					cursor.close();
				}
			}
		}
		if (result == null)
		{
			result = uri.getPath();
			int cut = result.lastIndexOf(File.separator);
			if (cut != -1)
			{
				result = result.substring(cut + 1);
			}
		}
		return result;
	}

	/**
	 * 获取真实的路径
	 *
	 * @param context
	 *            上下文
	 * @param uri
	 *            uri
	 * @return 文件路径
	 */
	static String getRealPathFromURI(Context context, Uri uri)
	{
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		if (cursor == null)
		{
			return uri.getPath();
		}
		else
		{
			cursor.moveToFirst();
			int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			String realPath = cursor.getString(index);
			cursor.close();
			return realPath;
		}
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

	public static String getReadableFileSize(long size)
	{
		if (size <= 0)
		{
			return "0";
		}
		final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	/**
	 * 创建图片存储路径
	 */
	public static File getPhotoFileDir()
	{
		File fileDir = null;
		if (checkSDCardAvaiable())
		{
			fileDir = new File(FileUtil.PHOTO_PATH);
			if (fileDir.exists())
			{
				return fileDir;
			}
			else
			{
				fileDir.mkdirs();
			}
		}
		return fileDir;
	}

	// 判断文件是否存在
	public static boolean fileIsExists(String strFile)
	{
		try
		{
			File f = new File(strFile);
			if (!f.exists())
			{
				return false;
			}
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}

	/**
	 * 检测sd卡是否可用
	 *
	 * @return
	 */
	private static boolean checkSDCardAvaiable()
	{
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	private static Handler sHandler = new Handler(Looper.getMainLooper());

	private static ExecutorService sExecutorService = Executors.newSingleThreadExecutor();

	public static void runOnUiThread(Runnable r)
	{

		if (Looper.myLooper() == Looper.getMainLooper())
		{
			r.run();
		}
		else
		{
			sHandler.post(r);
		}

	}

	public static void runOnSubThread(Runnable r)
	{
		sExecutorService.submit(r);
	}
}
