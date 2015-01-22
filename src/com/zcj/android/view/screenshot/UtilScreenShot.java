package com.zcj.android.view.screenshot;

import java.io.IOException;

import android.graphics.Bitmap;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import com.zcj.android.app.BaseActivity;
import com.zcj.android.util.UtilAppFile;
import com.zcj.android.util.UtilImage;
import com.zcj.android.view.screenshot.ScreenShotView.OnScreenShotListener;

public class UtilScreenShot {

	/**
	 * 在当前Activity中添加截屏功能，截屏后调用回调函数
	 * 
	 * @param context
	 *            必须继承BaseActivity(全屏和销毁判断)
	 * @param mScreenShotListener
	 */
	public static void addScreenShot(BaseActivity context, OnScreenShotListener mScreenShotListener) {
		ScreenShotView screenShot = new ScreenShotView(context, mScreenShotListener);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		context.getWindow().addContentView(screenShot, lp);
	}

	/**
	 * 在当前Activity中添加截屏功能，截屏后直接保存图片到指定目录
	 * @param context
	 * @param savePath 截屏保存的路径，包含文件名及后缀
	 */
	public static void saveScreenShot(final BaseActivity context, final String savePath) {
		addScreenShot(context, new OnScreenShotListener() {
			@Override
			public void onComplete(Bitmap bm) {
				try {
					UtilImage.saveImage(bm, savePath);
					UtilAppFile.scanPhoto(context, savePath);
					Toast.makeText(context, "截屏成功==>" + savePath, Toast.LENGTH_SHORT).show();
				} catch (IOException e) {
					e.printStackTrace();
					Toast.makeText(context, "截屏失败", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

}
