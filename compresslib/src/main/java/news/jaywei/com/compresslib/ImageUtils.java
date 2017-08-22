package news.jaywei.com.compresslib;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.widget.ImageView;

import net.bither.util.NativeUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * description:
 * author: liujie
 * date: 2017/8/21 15:11
 */
public class ImageUtils {
    public static final String PHOTO_PATH = Environment.getExternalStorageDirectory() + File.separator + "DCIM" + File.separator + File.separator + "Cherry"
            + File.separator;

    /**
     * 将图片保存到sd卡
     */
    public static void saveBitmap2SDCard(Bitmap bitmap, String photoFileName, Context context) {
        File photoFileDir = getPhotoFileDir();
        File photoFile = null;
        if (photoFileDir != null) {
            photoFile = new File(photoFileDir, photoFileName);
            if (photoFile.exists()) {
                photoFile.delete();
                photoFile = new File(photoFileDir, photoFileName);
            }
        }

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(photoFile);
            if (bitmap != null && fileOutputStream != null) {
                bitmap.compress(CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.flush();
            }
        } catch (IOException e) {
            photoFile.delete();
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {

                    fileOutputStream.close();
                }
                if (photoFile != null && photoFile.isFile()) {
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(photoFile));
                    context.sendBroadcast(intent);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 将图片保存到sd卡
     */
    public static void saveBitmap2SDCard(byte[] bitmap, String photoFileName, Context context) {
        File photoFileDir = getPhotoFileDir();
        File photoFile = null;
        if (photoFileDir != null) {
            photoFile = new File(photoFileDir, photoFileName);
            if (photoFile.exists()) {
                photoFile.delete();
                photoFile = new File(photoFileDir, photoFileName);
            }
        }

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(photoFile);
            if (bitmap != null && fileOutputStream != null) {
                fileOutputStream.write(bitmap);
                fileOutputStream.flush();
            }
        } catch (IOException e) {
            photoFile.delete();
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {

                    fileOutputStream.close();
                }
                if (photoFile != null && photoFile.isFile()) {
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(photoFile));
                    context.sendBroadcast(intent);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static Bitmap getCompressBitmap(String path, Context context, int minSideLength, int maxNumOfPixels) {
        Bitmap mBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inTempStorage = new byte[100 * 1024];

        File file = new File(path);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            if (fis != null) {
                BitmapFactory.decodeFileDescriptor(fis.getFD(), null, options);

                options.inSampleSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
                // Log.e("options.inSampleSize", options.inSampleSize + "");
                options.inJustDecodeBounds = false;

                mBitmap = BitmapFactory.decodeFile(path, options);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return mBitmap;
    }

    /**
     * 创建图片存储路径
     *
     * @return
     */
    public static File getPhotoFileDir() {
        File fileDir = null;
        if (checkSDCardAvaiable()) {
            fileDir = new File(PHOTO_PATH);
            if (fileDir.exists()) {
                return fileDir;
            } else {
                fileDir.mkdirs();
            }
        }
        return fileDir;
    }

    /**
     * 检测sd卡是否可用
     *
     * @return
     */
    private static boolean checkSDCardAvaiable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取文件名称
     *
     * @param file ：文件的完整路径
     * @return
     */
    public static String getFileName(String file) {
        return file.substring(file.lastIndexOf(File.separator) + 1);
    }

    /**
     * 以宽为标准等比缩放高
     *
     * @param displayWidth
     * @return
     */
    public static int getDisplayHeight(float displayWidth, float resWidth, float resHeight) {

        return (int) (resHeight * (displayWidth / resWidth));
    }

    /**
     * 获取滤镜图片
     *
     * @param mBitmap
     * @param fliterArray
     * @return
     */
    public static Bitmap convertBitmap2FliterBitmap(Bitmap mBitmap, float[] fliterArray) {
        Bitmap bitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Paint paint = new Paint();
        paint.setColorFilter(null);
        Canvas canvas = new Canvas(bitmap);
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(fliterArray);
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bitmap;
    }

    /**
     * 计算放缩的比例
     *
     * @param resBitmap
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public static float getScale(Bitmap resBitmap, int targetWidth, int targetHeight) {

        float scale = 1.0f;

        if (resBitmap.getWidth() == targetWidth && resBitmap.getHeight() == targetHeight) {
            return scale;
        }

        if (resBitmap.getWidth() * targetHeight >= targetWidth * resBitmap.getHeight()) {
            scale = (float) targetHeight / (float) resBitmap.getHeight();
        } else {
            scale = (float) targetWidth / (float) resBitmap.getWidth();
        }

        return scale;
    }

    /**
     * 获取裁剪的正方形图片
     *
     * @param context
     * @param path
     * @param resWidth
     * @param resHeight
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    /*
	 * public static Bitmap getCropBitmap(final Context context, String path,
	 * int resWidth, int resHeight, int minSideLength, int maxNumOfPixels) { int
	 * targhtWH = Math.min(resWidth, resHeight); Bitmap bitmap =
	 * getCompressBitmap(path, context, minSideLength, maxNumOfPixels); bitmap =
	 * Bitmap.createBitmap(bitmap, (bitmap.getWidth() - targhtWH) / 2,
	 * (bitmap.getHeight() - targhtWH) / 2, targhtWH, targhtWH); return bitmap;
	 * }
	 */

    /**
     * (图片裁剪为指定比例):放大缩小到指定宽或者高
     *
     * @param bitmap
     */
    public static Bitmap getCropBitmap(Bitmap bitmap, int targetWidth, int targetHeight, ImageView imageView) {

        Bitmap mBitmap = null;

        boolean isBaseWidth = false;
        boolean isBaseHeight = false;

        float width = bitmap.getWidth();// 原图片的宽度
        float height = bitmap.getHeight();// 原图片的高度

		/*
		 * Log.e("getCropBitmap>>width>>>", width + "");
		 * Log.e("getCropBitmap>>height>>>", height + "");
		 */

        float scale = 1.0f;

        float widthScale = (float) targetWidth / (float) width;
        float heightScale = (float) targetHeight / (float) height;

        if (widthScale >= heightScale) {
            isBaseWidth = true;
            scale = widthScale;
        } else {
            isBaseHeight = true;
            scale = heightScale;
        }

        if (isBaseWidth) {
            // 以宽为标准计算
            mBitmap = Bitmap.createScaledBitmap(bitmap, (int) targetWidth, (int) (height * scale), true);
        } else if (isBaseHeight) {
            mBitmap = Bitmap.createScaledBitmap(bitmap, (int) (width * scale), (int) targetHeight, true);
        }

        // 截取图片中间部分显示
        mBitmap = Bitmap.createBitmap(mBitmap, (int) ((mBitmap.getWidth() - targetWidth) / 2), (int) ((mBitmap.getHeight() - targetHeight) / 2), targetHeight,
                targetHeight);

        android.view.ViewGroup.LayoutParams params = null;
        if (imageView.getLayoutParams() == null) {
            params = new android.view.ViewGroup.LayoutParams(targetWidth, targetHeight);
        } else {
            params = imageView.getLayoutParams();
            params.width = targetWidth;
            params.height = targetHeight;
        }
        imageView.setLayoutParams(params);

		/*
		 * Log.e("mBitmap>>>width", mBitmap.getWidth() + "");
		 * Log.e("mBitmap>>>height", mBitmap.getHeight() + "");
		 */

        return mBitmap;
    }

    /**
     * 计算centerCrop的放缩比例
     *
     * @param resWidth
     * @param resHeight
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public static float getScale(float resWidth, float resHeight, float targetWidth, float targetHeight) {
        float scale = 1.0f;

        float widthScale = targetWidth / resWidth;
        float heightScale = targetHeight / resHeight;

        if (widthScale >= heightScale) {
            scale = widthScale;
        } else {
            scale = heightScale;
        }

        return scale;
    }

    /**
     * 等比缩放图片
     */
    public static Bitmap getZoomBitmap(ImageView imageView, Bitmap bitmap, float targetWidth) {
        Bitmap mBitmap = null;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scale = ((float) height) / ((float) width);
        mBitmap = Bitmap.createScaledBitmap(bitmap, (int) targetWidth, (int) (targetWidth * scale), true);
        android.view.ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = (int) targetWidth;
        params.height = (int) (targetWidth * scale);
        imageView.setLayoutParams(params);
        return mBitmap;
    }

    /**
     * @param path
     * @param context
     * @param minSideLength
     * @param maxNumOfPixels :显示图片的最大像素
     * @param degrees        ：图片的角度
     * @return
     */
    public static Bitmap getCompressBitmap(String path, Context context, int minSideLength, int maxNumOfPixels, int degrees) {
        Bitmap mBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inPurgeable = true;
        options.inTempStorage = new byte[100 * 1024];

        File file = new File(path);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            if (fis != null) {
                BitmapFactory.decodeFileDescriptor(fis.getFD(), null, options);

                options.inSampleSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
                // Log.e("options.inSampleSize", options.inSampleSize + "");
                options.inJustDecodeBounds = false;

                mBitmap = BitmapFactory.decodeFile(path, options);

                if (degrees > 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(degrees);
                    mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return mBitmap;
    }

    private Boolean saveBitmapToCamera(Context context, Bitmap bm, String name) {
        if (name == null) {
            // 文件名为空报错失败
            return false;
        }
        File file = null;
        file = new File(name);
        if (file.exists()) {
            file.delete();
        }
        int quality = 10;
        // 压缩保存
        NativeUtil.compressBitmap(bm, quality, file.getAbsolutePath(), true);
        // 释放内存，回收bitmap
        bm.recycle();
        bm = null;
        return true;
    }

    /**
     * 获取图片旋转角度
     *
     * @param path
     * @return
     */
    public static int readPictureRotateDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 获取旋转后的图片
     *
     * @param degrees
     * @param bitmap
     * @return
     */
    public static Bitmap getRotatingBitmap(int degrees, Bitmap bitmap) {
        Bitmap mBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
    }

    /**
     * 图片压缩比例
     *
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 将bitmap转为string
     *
     * @return bitmap对应的string
     */
    public static String convertBitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 80, out);
        return Base64.encodeToString(out.toByteArray(), Base64.DEFAULT);
    }

    /**
     * 将string转为byte数组
     *
     * @return string对应的byte数组
     */
    public static byte[] convertBitmapStringToByteArray(String bitmapByteString) {
        return Base64.decode(bitmapByteString, Base64.DEFAULT);
    }

    /**
     * 存储位图
     *
     * @param bitmap
     */
    public static String saveBitmap(Bitmap bitmap, String fileName) {
        File photoFileDir = getPhotoFileDir();
        File file = new File(photoFileDir, fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            // if (!bitmap.isRecycled()) {
            // bitmap.recycle(); // 回收图片所占的内存
            // System.gc(); // 提醒系统及时回收
            // }
            return file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "-1";
        } catch (IOException e) {
            e.printStackTrace();
            return "-1";
        }
    }

    /**
     * 以最省内存的方式读取本地资源的图片
     */
    public static Bitmap readBitMap(Context context, String path) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        File file = new File(path);
        FileInputStream is;
        try {
            is = new FileInputStream(file);
            return BitmapFactory.decodeStream(is, null, opt);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 以最省内存的方式读取本地资源的图片
     */
    public static Bitmap readBitMap(String path) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        File file = new File(path);
        FileInputStream is;
        try {
            is = new FileInputStream(file);
            return BitmapFactory.decodeStream(is, null, opt);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 以最省内存的方式读取本地资源的图片
     */
    public static Bitmap readBitMapScale(String path, Context context, int minSideLength, int maxNumOfPixels) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        File file = new File(path);
        FileInputStream is;
        try {
            opt.inSampleSize = computeInitialSampleSize(opt, minSideLength, maxNumOfPixels);
            is = new FileInputStream(file);
            return BitmapFactory.decodeStream(is, null, opt);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    // 网络获取图片保存到本地
//    public static void saveImageURI(final Context context, final String imageurl, final String name) throws Exception {
//        new Thread() {
//            @Override
//            public void run() {
//
//                try {
//                    File file = new File(name + "/cherry");
//                    // 如果图片存在本地缓存目录，则不去服务器下载
//                    if (!file.exists()) {
//                        file.mkdirs();
//                    }
//                    // 从网络上获取图片
//                    String pathName = new MD5().md5(imageurl);
//                    file = new File(name + "/cherry/" + pathName + ".jpg");
//                    if (file.exists()) {
//                        new Handler(Looper.getMainLooper()).post(new Runnable() {
//                            @Override
//                            public void run() {
//                                ToastUtils.showShortToast(context, "图片已保存");
//                            }
//                        });
//                        return;
//                    }
//
//                    URL url = new URL(imageurl);
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.setConnectTimeout(20000);
//                    conn.setRequestMethod("GET");
//                    conn.setDoInput(true);
//                    if (conn.getResponseCode() == 200) {
//
//                        InputStream is = conn.getInputStream();
//                        FileOutputStream fos = new FileOutputStream(file);
//                        byte[] buffer = new byte[1024];
//                        int len = 0;
//                        while ((len = is.read(buffer)) != -1) {
//                            fos.write(buffer, 0, len);
//                        }
//                        is.close();
//                        fos.close();
//                        // 返回一个URI对象
//                        // 直接弹出保存成功了。
//                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                        final Uri uri = Uri.fromFile(file);
//                        intent.setData(uri);
//                        context.sendBroadcast(intent);
//                        new Handler(Looper.getMainLooper()).post(new Runnable() {
//                            @Override
//                            public void run() {
//                                ToastUtils.showLongToast(context, "图片保存在cherry文件夹下");
//                            }
//                        });
//                    }
//                } catch (Exception e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//                // }
//            }
//        }.start();
//    }

}
