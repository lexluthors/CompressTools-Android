package news.jaywei.com.compresslib;

import java.io.File;

/**
 * Created by Tony on 2017/8/22.
 */

public interface OnCompressListener {

    void onStart();
    void onSuccess(File file);
}
