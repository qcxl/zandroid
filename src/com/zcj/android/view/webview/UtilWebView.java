package com.zcj.android.view.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.zcj.android.view.imagezoom.UtilImageZoom;

public class UtilWebView {
	
	public static final String JS_CLASSNAME = "mWebViewImageListener";
	public static final String JS_FUNNAME_ONIMAGECLICK = "onImageClick";

	/**
	 * 初始化WebView配置
	 * @param cxt
	 * @param mWebView
	 * @param imgSavePath
	 */
	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	public static void initWebView(final Context cxt, WebView mWebView,
			final String imgSavePath) {
		
		mWebView.getSettings().setSupportZoom(true);
    	mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setDefaultFontSize(15);
		
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				openBrowser(view.getContext(), url);
				return true;
			}
		});
		
		// 添加网页的点击图片展示支持
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.addJavascriptInterface(new OnWebViewImageListener() {
			@Override
			public void onImageClick(String bigImageUrl) {
				if (bigImageUrl != null)
					UtilImageZoom.showImageZoomDialog(cxt, bigImageUrl, imgSavePath);
			}
		}, JS_CLASSNAME);
	}
	
	/**
	 * 显示WebView的内容
	 * @param body
	 * @param mWebView
	 */
	public static void showWebView(String body, WebView mWebView) {
		mWebView.loadDataWithBaseURL(null, body, "text/html", "utf-8", null);
	}
	
	/**
	 * 打开浏览器
	 * 
	 * @param context
	 * @param url
	 */
	private static void openBrowser(Context context, String url) {
		try {
			Uri uri = Uri.parse(url);
			Intent it = new Intent(Intent.ACTION_VIEW, uri);
			context.startActivity(it);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "无法浏览此网页", Toast.LENGTH_LONG).show();
		}
	}

}
