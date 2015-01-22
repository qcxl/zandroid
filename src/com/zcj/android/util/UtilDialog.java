package com.zcj.android.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class UtilDialog {
	
	/** 弹出 Toast 提示框 */
	public static void alert(Context context, String msgStr) {
		Toast.makeText(context, msgStr, Toast.LENGTH_SHORT).show();
	}

	/** 弹出 Alert 提示框，（一个确定按钮，点击关闭提示框） */
	public static void builderAlertDialog(Context context, final String title, final String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if (title != null) {
			builder.setTitle(title);
		}
		builder.setMessage(message);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setPositiveButton("确定", null);
		builder.create();
		builder.show();
	}

	/** 弹出 Alert 提示框，（一个确定按钮，点击触发 callback 事件） */
	public static void builderAlertDialog(Context context, final String title, final String message, final DialogCallback callback) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if (title != null) {
			builder.setTitle(title);
		}
		builder.setMessage(message);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (callback != null) {
					callback.doSomething_ChickOK();
				}
				dialog.cancel();
			}
		});
		builder.show();
	}

	/** 弹出 Alert 提示框，（一个确定按钮，点击触发 callback 事件；一个取消按钮，点击关闭提示框） */
	public static void builderAlertDialog2(Context context, final String title, final String message, final DialogCallback callback) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if (title != null) {
			builder.setTitle(title);
		}
		builder.setMessage(message);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (callback != null) {
					callback.doSomething_ChickOK();
				}
				dialog.cancel();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.show();
	}

	public interface DialogCallback {
		public void doSomething_ChickOK();
	}
}
