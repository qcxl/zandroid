package com.zcj.android.util;

import java.io.File;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.provider.Settings;
import android.widget.Toast;

public class UtilIntent {

	/** 剪裁后返回 */
	public static final int REQUEST_CODE_GETIMAGE_BYSDCARD = 0;
	/** 相机拍照后返回 */
	public static final int REQUEST_CODE_GETIMAGE_BYCAMERA = 1;
	/** 相册选图后返回 */
	public static final int REQUEST_CODE_GETIMAGE_BYCROP = 2;

	/** 相册选图 */
	public static void startImagePick(Activity activity) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		activity.startActivityForResult(Intent.createChooser(intent, "选择图片"), REQUEST_CODE_GETIMAGE_BYCROP);
	}

	/** 相机拍照 */
	public static void startActionCamera(Activity activity, String savePath) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(savePath)));
		activity.startActivityForResult(intent, REQUEST_CODE_GETIMAGE_BYCAMERA);
	}

	/** 裁剪 */
	public static void startActionCrop(Activity activity, String sourePath, String savePath) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(Uri.fromFile(new File(sourePath)), "image/*");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(savePath)));
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);// 裁剪框比例
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 200);// 输出图片大小
		intent.putExtra("outputY", 200);
		intent.putExtra("scale", true);// 去黑边
		intent.putExtra("scaleUpIfNeeded", true);// 去黑边
		activity.startActivityForResult(intent, REQUEST_CODE_GETIMAGE_BYSDCARD);
	}

	/**
	 * 打开网页
	 * 
	 * @param context
	 * @param url
	 *            如：http://www.baidu.com
	 */
	public static void startWeb(Context context, String url) {
		try {
			Uri uri = Uri.parse(url);
			Intent it = new Intent(Intent.ACTION_VIEW, uri);
			context.startActivity(it);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "无法浏览此网页", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 拨打电话
	 * 
	 * @param context
	 * @param phone
	 *            如：13788888888
	 */
	public static void startPhone(Context context, String phone) {
		Uri uri = Uri.parse("tel:" + phone);
		Intent it = new Intent(Intent.ACTION_DIAL, uri);
		context.startActivity(it);
	}

	/**
	 * 打开录音机
	 * 
	 * @param context
	 */
	public static void startAudioRecord(Context context) {
		Intent mi = new Intent(Media.RECORD_SOUND_ACTION);
		context.startActivity(mi);
	}

	/**
	 * 安装应用
	 * 
	 * @param context
	 * @param packageName
	 */
	public static void installApk(Context context, String packageName) {
		Uri installUri = Uri.fromParts("package", packageName, null);
		Intent it = new Intent(Intent.ACTION_PACKAGE_ADDED, installUri);
		context.startActivity(it);
	}

	/**
	 * 卸载应用
	 * 
	 * @param context
	 * @param packageName
	 */
	public static void uninstallApk(Context context, String packageName) {
		Uri uri = Uri.fromParts("package", packageName, null);
		Intent it = new Intent(Intent.ACTION_DELETE, uri);
		context.startActivity(it);
	}

	/** 打开网络设置界面 */
	public static void openSetting(Activity activity) {
		Intent intent = new Intent("/");
		ComponentName cm = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
		intent.setComponent(cm);
		intent.setAction("android.intent.action.VIEW");
		activity.startActivityForResult(intent, 0);
	}

	/** 打开GPS设置页面 */
	public static void openGpsSetting(Context context) {
		Intent intent = new Intent();
		intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			context.startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			intent.setAction(Settings.ACTION_SETTINGS);
			try {
				context.startActivity(intent);
			} catch (Exception e) {
			}
		}
	}

}
