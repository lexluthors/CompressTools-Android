# CompressTools-Android
几乎无损压缩图片。

使用方法：
压缩本地图片，生成File：


        //使用默认设置。
        CompressTools.getDefault(this).compressToFileJni(oldFile, new OnCompressListener() {
			@Override
			public void onStart() {

			}

			@Override
			public void onSuccess(File file) {

			}
		});
		//可自定义
		new CompressTools.Builder(this).setMaxWidth(1080) // 默认最大宽度为720
				.setMaxHeight(1920) // 默认最大高度为960
				.setQuality(50) // 默认压缩质量为60,60足够清晰
				.setCompressFormat(Bitmap.CompressFormat.JPEG) // 设置默认压缩为jpg格式
				.setFileName("test1")//自定义文件名称
				.setDestinationDirectoryPath(FileUtil.getPhotoFileDir().getAbsolutePath()).build()//自定义路径
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

				压缩bitmap：
				//默认设置：
				CompressTools.getDefault(this).compressToBitmapJni(oldFile, new OnCompressBitmapListener() {
                			@Override
                			public void onStart() {

                			}

                			@Override
                			public void onSuccess(Bitmap bitmap) {

                			}
                		});
                //自定义：
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



