package com.zcj.android.util;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/** 数据清除管理器 */
public class UtilCleaner {
	
	public static String getAppCacheSizeText(Context context) {
		String cacheSize = "0KB";
		long size = getAppCacheSize(context);
		if (size > 0) {
			cacheSize = UtilFile.getFileSizeText(size);
		}
		return cacheSize;
	}
	
	private static long getAppCacheSize(Context context) {
		long fileSize = 0;
		fileSize += UtilFile.getFolderSize(context.getFilesDir());
		fileSize += UtilFile.getFolderSize(context.getCacheDir());
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {// 2.2版本才有将应用缓存转移到SD卡的功能
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				fileSize += UtilFile.getFolderSize(context.getExternalCacheDir());
			}
		}
		return fileSize;
	}

	public static void clearAppCache(final Context context) {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					Toast.makeText(context, "缓存清除成功", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context, "缓存清除失败", Toast.LENGTH_SHORT).show();
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					clean(context);
					msg.what = 1;
				} catch (Exception e) {
					e.printStackTrace();
					msg.what = -1;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	private static void clean(Context context) {
		clean(context, true, true, false, false, true, true);
	}

	/** 自定义删除缓存内容 */
	private static void clean(Context context, boolean webViewCache, boolean cache, boolean databases, boolean shared_prefs, boolean files, boolean externalCache) {
		long curTime = System.currentTimeMillis();
		if (webViewCache) {
			cleanWebViewCache(context);
		}
		if (cache) {
			cleanInternalCache(context, curTime);
		}
		if (databases) {
			cleanDatabases(context, curTime);
		}
		if (shared_prefs) {
			cleanSharedPreference(context, curTime);
		}
		if (files) {
			cleanFiles(context, curTime);
		}
		if (externalCache) {
			cleanExternalCache(context, curTime);
		}
	}

	/** 清除本应用的所有WebView缓存 */
	private static void cleanWebViewCache(Context context) {
		// TODO CacheManager 类在4.4以上版本不存在
//		File file = CacheManager.getCacheFileBaseDir();
//		if (file != null && file.exists() && file.isDirectory()) {
//			for (File item : file.listFiles()) {
//				item.delete();
//			}
//			file.delete();
//		}
		context.deleteDatabase("webview.db");
		context.deleteDatabase("webview.db-shm");
		context.deleteDatabase("webview.db-wal");
		context.deleteDatabase("webviewCache.db");
		context.deleteDatabase("webviewCache.db-shm");
		context.deleteDatabase("webviewCache.db-wal");
	}

	/** 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) */
	private static void cleanInternalCache(Context context, long curTime) {
		deleteFilesByDirectory(context.getCacheDir(), curTime);
	}

	/** 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) */
	private static void cleanDatabases(Context context, long curTime) {
		deleteFilesByDirectory(new File("/data/data/" + context.getPackageName() + "/databases"), curTime);
	}

	/** 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) */
	private static void cleanSharedPreference(Context context, long curTime) {
		deleteFilesByDirectory(new File("/data/data/" + context.getPackageName() + "/shared_prefs"), curTime);
	}

	/** 按名字清除本应用数据库 */
	public static void cleanDatabaseByName(Context context, String dbName) {
		context.deleteDatabase(dbName);
	}

	/** 清除/data/data/com.xxx.xxx/files下的内容 */
	private static void cleanFiles(Context context, long curTime) {
		deleteFilesByDirectory(context.getFilesDir(), curTime);
	}

	/** 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache) */
	private static void cleanExternalCache(Context context, long curTime) {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {// 2.2版本才有将应用缓存转移到SD卡的功能
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				deleteFilesByDirectory(context.getExternalCacheDir(), curTime);
			}
		}
	}

	/** 删除指定文件夹里的所有文件，保留空文件夹，返回删除的文件数量 */
	private static int deleteFilesByDirectory(File directory, long curTime) {
		int deletedFiles = 0;
		if (directory != null && directory.exists() && directory.isDirectory()) {
			try {
				for (File child : directory.listFiles()) {
					if (child.isDirectory()) {
						deletedFiles += deleteFilesByDirectory(child, curTime);
					} else if (child.isFile() && child.lastModified() < curTime) {
						if (child.delete()) {
							deletedFiles++;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return deletedFiles;
	}
}