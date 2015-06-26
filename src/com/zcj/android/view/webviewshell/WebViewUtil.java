package com.zcj.android.view.webviewshell;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.zcj.util.UtilFile;

/**
 * WebView壳
 * 
 * @author zouchongjin@sina.com
 * @data 2015年6月10日
 */
public class WebViewUtil {

	public static final String JS_NAME = "Android";

	private Activity activity;
	private WebView myWebView;
	private Map<String, String> staticFileMap;

	public WebViewUtil(Activity activity, WebView myWebView, Map<String, String> staticFileMap) {
		super();
		this.activity = activity;
		this.myWebView = myWebView;
		this.staticFileMap = staticFileMap;
	}

	public WebViewUtil(Activity activity, WebView myWebView) {
		super();
		this.activity = activity;
		this.myWebView = myWebView;
	}

	/** 初始化 */
	@SuppressLint("SetJavaScriptEnabled")
	public void init(Object myJavascriptInterface, String indexUrl) {
		if (myWebView != null) {
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
	}

	/** 处理手机返回按钮（返回按钮调用JS的方法） */
	public Boolean onKeyDown(int keyCode, KeyEvent event, final OnQuitListener listener, final String jsFunction) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (myWebView != null && myWebView.canGoBack()) {
				myWebView.post(new Runnable() {
					@Override
					public void run() {
						myWebView.loadUrl("javascript:" + jsFunction + "()");
					}
				});
				return true;
			} else {
				// 弹出框
				AlertDialog ad = new AlertDialog.Builder(activity).setMessage("确定退出吗")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
								listener.webViewQuit();
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

	/** 处理手机返回按钮（返回按钮调用webView的goBack方法） */
	public Boolean onKeyDown(int keyCode, KeyEvent event, final OnQuitListener listener) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (myWebView != null && myWebView.canGoBack()) {
				myWebView.goBack();
				return true;
			} else {
				// 弹出框
				AlertDialog ad = new AlertDialog.Builder(activity).setMessage("确定退出吗")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
								listener.webViewQuit();
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

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
			WebResourceResponse response = null;
			if (staticFileMap != null && staticFileMap.size() > 0) {
				for (Map.Entry<String, String> entry : staticFileMap.entrySet()) {
					if (url.contains(entry.getKey())) {
						try {
							InputStream localCopy = activity.getAssets().open(entry.getValue());
							Log.v("WebView", url + "加载本地资源" + entry.getValue());
							response = new WebResourceResponse(UtilFile.getMimeType(entry.getKey()), "UTF-8", localCopy);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			return response;
		}

	}

	private class MyWebChromeClient extends WebChromeClient {

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
