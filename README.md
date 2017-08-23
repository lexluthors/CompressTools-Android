#CompressTools-Android
几乎无损压缩图片。

使用方法：
1，压缩本地图片，生成File：

使用默认设置。默认设置最大宽高为720*960

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



自定义File：

new CompressTools.Builder(this).setMaxWidth(1080) // 默认最大宽度为720
				.setMaxHeight(1920) // 默认最大高度为960
				.setQuality(50) // 默认压缩质量为60,60足够清晰
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

自定义bitmap：

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
						mainImageNew.setImageBitmap(bitmap);
						mainTextNew.setText(String.format("Size : %s", getReadableFileSize(bitmap.getByteCount())));
					}
				});