package com.zcj.android.view.webviewshell;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Process;
import android.view.KeyEvent;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

/**
 * WebView壳
 * 
 * @author zouchongjin@sina.com
 * @data 2015年6月10日
 */
public class WebViewUtil {

	public static final String JS_NAME = "Android";

	Activity activity;
	WebView myWebView;

	private ValueCallback<Uri> mUploadMessage;
	private final static int FILECHOOSER_RESULTCODE = 1;

	public WebViewUtil(Activity activity, WebView myWebView) {
		super();
		this.activity = activity;
		this.myWebView = myWebView;
	}

	/** 初始化 */
	@SuppressLint("SetJavaScriptEnabled")
	public void init(Object myJavascriptInterface, String indexUrl) {
		// 支持JS
		myWebView.getSettings().setJavaScriptEnabled(true);

		// 允许访问文件数据
		myWebView.getSettings().setAllowFileAccess(true);

		// 设置支持缩放
		// myWebView.getSettings().setBuiltInZoomControls(true);

		// 处理各种通知、请求事件(支持内部处理URL)
		myWebView.setWebViewClient(new MyWebViewClient());

		// 处理JS的对话框，网站图标，网站title，加载进度等
		myWebView.setWebChromeClient(new MyWebChromeClient());

		// 支持JS访问Android
		if (myJavascriptInterface != null) {
			myWebView.addJavascriptInterface(myJavascriptInterface, JS_NAME);
		}

		// 调用外链接
		myWebView.loadUrl(indexUrl);
	}

	/** 退出程序 */
	public void quit() {
		activity.finish();// 退出当前Activity，进程还存在
		Process.killProcess(Process.myPid());// 退出程序，进程也不存在，不推荐使用
	}

	/** 接收文件上传的返回 */
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == FILECHOOSER_RESULTCODE) {
			if (null == mUploadMessage)
				return;
			Uri result = intent == null || resultCode != Activity.RESULT_OK ? null : intent.getData();
			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;
		}
	}

	/** 处理手机返回按钮 */
	public Boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (myWebView.canGoBack()) {
				myWebView.goBack();
				return true;
			} else {
				// 弹出框
				AlertDialog ad = new AlertDialog.Builder(activity).setMessage("确定退出吗")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
								quit();
							}
						}).setNegativeButton("返回", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// 不做任何操作
							}
						}).create();
				ad.show();
				return false;
			}
		}
		return null;
	}

	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	@SuppressWarnings("unused")
	private class MyWebChromeClient extends WebChromeClient {

		// For 3.0-
		public void openFileChooser(ValueCallback<Uri> uploadMsg) {
			mUploadMessage = uploadMsg;
			Intent i = new Intent(Intent.ACTION_GET_CONTENT);
			i.addCategory(Intent.CATEGORY_OPENABLE);
			i.setType("image/*");
			activity.startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
		}

		// For Android 3.0+
		public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
			openFileChooser(uploadMsg);
		}

		// For Android 4.1+
		public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
			openFileChooser(uploadMsg);
		}

		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
			builder.setTitle("提示").setMessage(message).setPositiveButton("确定", null);
			builder.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					return true;
				}
			});
			builder.setCancelable(false);
			AlertDialog dialog = builder.create();
			dialog.show();
			result.confirm();
			return true;
		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
			builder.setTitle("提示").setMessage(message).setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					result.confirm();
				}
			}).setNeutralButton("取消", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					result.cancel();
				}
			});
			builder.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					result.cancel();
				}
			});

			builder.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					return true;
				}
			});
			builder.setCancelable(false);
			AlertDialog dialog = builder.create();
			dialog.show();
			return true;
		}

		@Override
		public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
			builder.setTitle("提示").setMessage(message);
			final EditText et = new EditText(view.getContext());
			et.setSingleLine();
			et.setText(defaultValue);
			builder.setView(et);
			builder.setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					result.confirm(et.getText().toString());
				}
			}).setNeutralButton("取消", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					result.cancel();
				}
			});

			// 屏蔽keycode等于84之类的按键，避免按键后导致对话框消息而页面无法再弹出对话框的问题
			builder.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					return true;
				}
			});

			// 禁止响应按back键的事件
			builder.setCancelable(false);
			AlertDialog dialog = builder.create();
			dialog.show();
			return true;
		}
	}

}
