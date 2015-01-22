package com.zcj.android.view.pathchoose;

import android.app.Activity;

import com.zcj.android.view.pathchoose.PathChooseDialog.ChooseCompleteListener;

public class UtilPathChoose {
	
	/**
	 * 显示路径选择对话框
	 * 
	 * @param context
	 * @param listener
	 *            选择路径后的回调方法监听
	 */
	public static void showFilePathDialog(Activity context, ChooseCompleteListener listener) {
		new PathChooseDialog(context, listener).show();
	}
}
