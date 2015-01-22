package com.zcj.android.view.imagezoom;

import android.content.Context;
import android.content.Intent;

public class UtilImageZoom {
	
	/**
	 * 打开图片显示详细页面
	 * 
	 * @param context
	 * @param imgUrl
	 *            图片的URL
	 * @param imgSavePath
	 *            点击保存按钮时保存图片的地址
	 */
	public static void showImageZoomDialog(Context context, String imgUrl, String imgSavePath) {
		Intent intent = new Intent(context, ImageZoomDialog.class);
		intent.putExtra("img_url", imgUrl);
		intent.putExtra("img_savePath", imgSavePath);
		context.startActivity(intent);
	}
	
}
