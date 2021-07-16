package com.brainyapps.funtivity.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ResourceUtil {
	public static String RES_DIRECTORY = Environment.getExternalStorageDirectory() + "/brainyapps/Funtivity/";
	public static String getImageFilePath(String fileName) {
		String tempDirPath = RES_DIRECTORY;
		String tempFileName = fileName;

		File tempDir = new File(tempDirPath);
		if (!tempDir.exists())
			tempDir.mkdirs();
		File tempFile = new File(tempDirPath + tempFileName);
		if (!tempFile.exists())
			try {
				tempFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		return tempDirPath + tempFileName;
	}

	public static String getAvatarFilePath() {
		return getImageFilePath("avatar.jpg");
	}

	public static String getPhotoFilePath() {
		return getImageFilePath("photo.jpg");
	}

	public static String getPhotoAFilePath() {
		return getImageFilePath("photoA.jpg");
	}

	public static String getPhotoBFilePath() {
		return getImageFilePath("photoB.jpg");
	}

	public static String getPhotoCFilePath() {
		return getImageFilePath("photoC.jpg");
	}

	public static Bitmap decodeUri(Context context, Uri selectedImage, int reqSize) throws FileNotFoundException {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, options);
		int width_tmp = options.outWidth, height_tmp = options.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp / 2 < reqSize
					|| height_tmp / 2 < reqSize) {
				break;
			}
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}
		options = new BitmapFactory.Options();
		options.inSampleSize = scale;
		return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, options);
	}

	public static void saveBitmapToSdcard(Bitmap bitmap, String dirPath) {
		File tempFile = new File(dirPath);
		if (tempFile.exists())
			tempFile.delete();

		try {
			FileOutputStream fOut = new FileOutputStream(tempFile);

			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
