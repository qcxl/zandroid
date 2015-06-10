package com.zcj.android.view.webview;

import com.zcj.util.UtilString;

/**
 * 处理HTML内容，更好的支持WebView
 * 
 * @author zouchongjin@sina.com
 * @data 2015年6月10日
 */
public class UtilHtml {

	/** 全局web样式 */
	private final static String WEB_STYLE = "<style>* {font-size:16px;line-height:20px;} p {color:#333;} a {color:#3E62A6;} img {max-width:310px;} "
			+ "img.alignleft {float:left;max-width:120px;margin:0 10px 5px 0;border:1px solid #ccc;background:#fff;padding:2px;} "
			+ "pre {font-size:9pt;line-height:12pt;font-family:Courier New,Arial;border:1px solid #ddd;border-left:5px solid #6CE26C;background:#f6f6f6;padding:5px;} "
			+ "a.tag {font-size:15px;text-decoration:none;background-color:#bbd6f3;border-bottom:2px solid #3E6D8E;border-right:2px solid #7F9FB6;color:#284a7b;margin:2px 2px 2px 0;padding:2px 4px;white-space:nowrap;}</style>";

	/**
	 * 处理HTML内容，更好的支持WebView
	 * 
	 * @param body
	 *            HTML内容
	 * @param style
	 *            是否添加全局WEB样式（包括图片最大宽度、字体大小等等样式）
	 * @param bottom
	 *            是否在内容最下方添加一个80px高的空白区域
	 * @param imgClick
	 *            是否需要给图片添加额外点击事件（点击打开图片，提供保存和关闭功能）
	 * @return
	 */
	public static String webViewSupport(String body, boolean style, boolean bottom, boolean imgClick) {
		if (UtilString.isNotBlank(body)) {
			if (style) {
				body = WEB_STYLE + body;// 设置基本样式
				body = body.replaceAll("(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");// 过滤掉IMG标签的width属性
				body = body.replaceAll("(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");// 过滤掉IMG标签的height属性
			}
			if (imgClick) {
				body = body.replaceAll("(<img[^>]+src=\")(\\S+)\"", "$1$2\" onClick=\"javascript:" + UtilWebView.JS_CLASSNAME + "."
						+ UtilWebView.JS_FUNNAME_ONIMAGECLICK + "('$2')\"");
			}
			if (bottom) {
				body = body + "<div style='margin-bottom: 80px'/>";
			}
		}
		return "";
	}

	/** 已废弃 */
	@Deprecated
	public static String webViewImgHandle(String body, final boolean showImg) {
		if (showImg) {// 过滤掉 img标签的width,height属性
			body = body.replaceAll("(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
			body = body.replaceAll("(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");

			// 添加点击图片放大支持
			body = body.replaceAll("(<img[^>]+src=\")(\\S+)\"", "$1$2\" onClick=\"javascript:" + UtilWebView.JS_CLASSNAME + "."
					+ UtilWebView.JS_FUNNAME_ONIMAGECLICK + "('$2')\"");
		} else {// 过滤掉 img标签
			body = body.replaceAll("<\\s*img\\s+([^>]*)\\s*>", "");
		}
		return body;
	}

	/** 已废弃 */
	@Deprecated
	public static String webViewAddStyle(String body) {
		return WEB_STYLE + body;
	}

	/** 已废弃 */
	@Deprecated
	public static String webViewAddBottom(String body) {
		return body + "<div style='margin-bottom: 80px'/>";
	}

}
