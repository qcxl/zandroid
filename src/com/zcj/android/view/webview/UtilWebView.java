package com.zcj.android.view.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zcj.android.util.UtilIntent;
import com.zcj.android.view.imagezoom.UtilImageZoom;

public class UtilWebView {

	public static final String JS_CLASSNAME = "mWebViewImageListener";
	public static final String JS_FUNNAME_ONIMAGECLICK = "onImageClick";

	/** 在指定的webView中显示指定的HTML内容，同时处理HTML的样式和支持图片的点击保存（主要用于显示文章内容）。 */
	public static void showArticleWebView(final Context cxt, WebView mWebView, String body, final String imgSavePath) {
		// 初始化webView
		UtilWebView.initWebView(cxt, mWebView, imgSavePath);
		// 处理HTML
		body = UtilHtml.webViewSupport(body, true, true, true);
		// 在指定webView中显示指定HTML
		UtilWebView.showWebView(body, mWebView);
	}

	// 初始化webView配置（主要用于打开文章详细页）
	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	private static void initWebView(final Context cxt, WebView mWebView, final String imgSavePath) {

		mWebView.getSettings().setSupportZoom(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setDefaultFontSize(15);

		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				UtilIntent.startWeb(view.getContext(), url);
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

	// 在指定的webView中显示指定的HTML内容
	private static void showWebView(String body, WebView mWebView) {
		mWebView.loadDataWithBaseURL(null, body, "text/html", "utf-8", null);
	}

}
