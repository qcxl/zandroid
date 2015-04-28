package com.zcj.android.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;

import com.zcj.util.UtilFile;
import com.zcj.util.UtilString;

/**
 * 手机文件操作：SD卡、应用文件、媒体库
 * 
 * @author ZCJ
 * @data 2013-11-14
 */
public class UtilAppFile {

	/** SD卡是否存在 */
	public static boolean sdcardExist() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/** /data/data/xxx.xxx.xxx/files/ */
	public static String getFilesDir(Context context) {
		return context.getFilesDir().getAbsolutePath() + "/";
	}

	/** /mnt/sdcard/ */
	public static String getSdcardPath() {
		if (sdcardExist()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
		} else {
			return null;
		}
	}

	/** 获取系统存储路径 */
	public static String getRootDirectoryPath() {
		return Environment.getRootDirectory().getAbsolutePath();
	}

	/**
	 * 保存内容到文件里 文件目录：/data/data/xxx.xxx.xxx/files/fileName<br/>
	 * 其他应用读取文件的方法：File file = new
	 * File("/data/data/com.xxx.xxx/files/fileName");<br/>
	 * 注：File存储的文件都可以手动打开查看内容<br/>
	 * 
	 * @param context
	 * @param fileName
	 *            文件名
	 * @param content
	 *            内容
	 * @param mode
	 *            写入模式<br/>
	 *            Context.MODE_PRIVATE：私有模式，其他应用无法读取该文件，写的时候是覆盖模式<br/>
	 *            Context.MODE_APPEND：检查文件是否存在，存在就往文件内追加内容，不存在就创建新文件<br/>
	 *            Context.MODE_WORLD_READABLE：可以被其他应用读取<br/>
	 *            Context.MODE_WORLD_WRITEABLE：可以被其他应用写入<br/>
	 *            Context.MODE_WORLD_READABLE+Context.MODE_WORLD_WRITEABLE：
	 *            可被读和被写<br/>
	 * @return
	 */
	public static boolean saveToFiles(Context context, String fileName, String content, int mode) {
		if (context == null || UtilString.isBlank(fileName) || content == null) {
			return false;
		}
		try {
			FileOutputStream fos = context.openFileOutput(fileName, mode);
			fos.write(content.getBytes());
			fos.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 保存图片到文件里 文件目录：/data/data/xxx.xxx.xxx/files/fileName<br/>
	 * 其他应用读取文件的方法：File file = new
	 * File("/data/data/xxx.xxx.xxx/files/fileName");<br/>
	 * 注：File存储的文件都可以手动打开查看内容<br/>
	 * 
	 * @param context
	 * @param fileName
	 *            文件名
	 * @param bitmap
	 * @throws IOException
	 */
	public static void saveImage(Context context, String fileName, Bitmap bitmap) throws IOException {
		if (bitmap == null || UtilString.isBlank(fileName) || context == null)
			return;
		FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 100, stream);
		byte[] bytes = stream.toByteArray();
		fos.write(bytes);
		fos.close();
	}

	/**
	 * 读取文件里的内容 文件目录：/data/data/xxx.xxx.xxx/files/fileName
	 * 
	 * @param context
	 * @param fileName
	 *            文件名
	 * @return
	 */
	public static String getByFiles(Context context, String fileName) {
		try {
			FileInputStream inStream = context.openFileInput(fileName);
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[512];
			int length = -1;
			while ((length = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, length);
			}
			outStream.close();
			inStream.close();
			return outStream.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 读取图片的内容 文件目录：/data/data/xxx.xxx.xxx/files/fileName
	 * 
	 * @param context
	 * @param fileName
	 *            图片文件名
	 * @return
	 */
	public static Bitmap getBitmap(Context context, String fileName) {
		FileInputStream fis = null;
		Bitmap bitmap = null;
		try {
			fis = context.openFileInput(fileName);
			bitmap = BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return bitmap;
	}

	/**
	 * 读取项目静态文件里的内容 文件目录：res/raw/fileName
	 * 
	 * @param context
	 * @param rawResourceId
	 * @return
	 */
	public static String getByStaticFile(Context context, int rawResourceId) {
		String message = null;
		try {
			InputStream fis2 = context.getResources().openRawResource(rawResourceId);
			byte[] buffer2 = new byte[fis2.available()];
			fis2.read(buffer2);
			message = EncodingUtils.getString(buffer2, "UTF-8");
			fis2.close();
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return message;
	}

	/**
	 * 计算SD卡的剩余空间
	 * 
	 * @return 返回-1，说明没有安装SD卡
	 */
	@SuppressWarnings("deprecation")
	public static long getSdcardFreeDiskSpace() {
		long freeSpace = 0;
		if (sdcardExist()) {
			try {
				File path = Environment.getExternalStorageDirectory();
				StatFs stat = new StatFs(path.getPath());
				long blockSize = stat.getBlockSize();
				long availableBlocks = stat.getAvailableBlocks();
				freeSpace = availableBlocks * blockSize / 1024;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return -1;
		}
		return freeSpace;
	}

	/** 检查是否安装外置的SD卡 */
	public static boolean checkExternalSDExists() {
		Map<String, String> evn = System.getenv();
		return evn.containsKey("SECONDARY_STORAGE");
	}

	/** 获取手机外置SD卡的根目录 */
	public static String getExternalSDRoot() {
		Map<String, String> evn = System.getenv();
		return evn.get("SECONDARY_STORAGE");
	}

	/**
	 * 更新媒体库
	 */
	public static void scanPhoto(Context ctx, String filePath) {
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Uri contentUri = Uri.fromFile(new File(filePath));
		mediaScanIntent.setData(contentUri);
		ctx.sendBroadcast(mediaScanIntent);
	}
	
	/**
	 * 获取网络图片的数据
	 * @param imgUrl
	 *            网络图片路径
	 * @return
	 * @throws IOException
	 */
	@Deprecated
	public static byte[] getImage(String imgUrl) throws IOException {
		URL url = new URL(imgUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(6000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == 200) {
			InputStream inStream = conn.getInputStream();
			return UtilFile.toBytes(inStream);
		}
		return null;
	}
	
	/**
	 * 获取网络图片的数据
	 * @param imgUrl 图片URL
	 * @return
	 * @throws IOException
	 */
	@Deprecated
	public static Bitmap getImageBitmap(String imgUrl) throws IOException {
		byte[] data = getImage(imgUrl);
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		return bitmap;
	}

}
