package news.jaywei.com.compresslib;

import android.graphics.Bitmap;

/**
 * Created by Tony on 2017/8/22.
 */

public interface OnCompressBitmapListener
{
	void onStart();

	void onSuccess(Bitmap bitmap);
}
