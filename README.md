#CompressTools-Android
几乎无损压缩图片，保持清晰度最优。可以对比原生方法bitmap.compress(CompressFormat.JPEG, quality, fileOutputStream);

使用方法：

    compile 'com.jaywei:compresstool:1.0.0'


1和2使用默认设置，默认设置最大宽高为720*960

1，压缩本地图片

    CompressTools.getDefault(this).compressToFileJni(oldFile, new OnCompressListener()
    		{
    			@Override
    			public void onStart()
    			{

    			}

    			@Override
    			public void onSuccess(File file)
    			{

    			}
    		});

2，压缩bitmap：

    CompressTools.getDefault(this).compressToBitmapJni(oldFile, new OnCompressBitmapListener()
    		{
    			@Override
    			public void onStart()
    			{

    			}

    			@Override
    			public void onSuccess(Bitmap bitmap)
    			{

    			}
    		});

3，自定义压缩File：

    new CompressTools.Builder(this).setMaxWidth(1080) // 默认最大宽度为720
    				.setMaxHeight(1920) // 默认最大高度为960
    				.setQuality(50) // 默认压缩质量为60,60足够清晰
    				//.setKeepResolution(true)//设置保持原图分辨率，则设置的最大宽高就无效了。不需要设置最大宽高了。设置也不会报错了，该参数默认false
    				.setCompressFormat(Bitmap.CompressFormat.JPEG) // 设置默认压缩为jpg格式
    				.setFileName("test1").setDestinationDirectoryPath(FileUtil.getPhotoFileDir().getAbsolutePath()).build()
    				.compressToFileJni(oldFile, new OnCompressListener()
    				{
    					@Override
    					public void onStart()
    					{

    					}

    					@Override
    					public void onSuccess(File file)
    					{
    					}
    				});

4，自定义压缩bitmap：
    注意：setKeepResolution方法在压缩bitmap中无效，以最大宽高为准。

    new CompressTools.Builder(this).setMaxWidth(1080) // 默认最大宽度为720
    				.setMaxHeight(1920) // 默认最大高度为960
    				.setQuality(50) // 默认压缩质量为60,60足够清晰
    				.setCompressFormat(Bitmap.CompressFormat.JPEG) // 设置默认压缩为jpg格式
    				.setFileName("test2").setDestinationDirectoryPath(FileUtil.getPhotoFileDir().getAbsolutePath()).build()
    				.compressToBitmapJni(oldFile, new OnCompressBitmapListener()
    				{
    					@Override
    					public void onStart()
    					{

    					}

    					@Override
    					public void onSuccess(Bitmap bitmap)
    					{
    					}
    				});

本项目部分参考了：https://github.com/nanchen2251/CompressHelper
