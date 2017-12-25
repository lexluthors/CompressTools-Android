package net.bither.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;

/**
 * @author liujie
 */
public class NativeUtil {

    private static native String compressBitmap(Bitmap bit, int w, int h, int quality, byte[] fileNameBytes, boolean optimize);

    public static void saveBitmap(Bitmap bit, int quality, String fileName, boolean optimize) {
        compressBitmap(bit, bit.getWidth(), bit.getHeight(), quality, fileName.getBytes(), optimize);
    }

    public static void saveBitmapWithScale(Bitmap bit, int quality, String fileName, boolean optimize, float scaleSize) {
        Bitmap result = null;
        result = Bitmap.createBitmap((int) (bit.getWidth() * scaleSize), (int) (bit.getHeight() * scaleSize), Bitmap.Config.ARGB_8888);// 比例缩放
        Canvas canvas = new Canvas(result);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        Rect rect = new Rect(0, 0, bit.getWidth(), bit.getHeight());
        rect = new Rect(0, 0, (int) (bit.getWidth() * scaleSize), (int) (bit.getHeight() * scaleSize));// 比例缩放
        canvas.drawBitmap(bit, null, rect, null);
        compressBitmap(result, result.getWidth(), result.getHeight(), quality, fileName.getBytes(), optimize);
    }

    /**
     * description: 根据最大宽高计算出比例
     * author: liujie
     * date: 2017/12/25 14:36
     */
    public static void saveBitmapWithMaxWH(Bitmap bit, int quality, String fileName, boolean optimize, float maxWidth, float maxHeight) {
        float originalScale = bit.getWidth() / bit.getHeight();
        float maxScale = maxWidth / maxHeight;
        float finalScale = 1f;
        if (originalScale >= maxScale) {
            finalScale = maxWidth / bit.getWidth();
        } else {
            finalScale = maxHeight / bit.getHeight();
        }
        float scaleSize = maxHeight;
        saveBitmapWithScale(bit, quality, fileName, optimize, finalScale);
    }

    static {
        System.loadLibrary("jpegbither");
        System.loadLibrary("bitherjni");
    }

}
