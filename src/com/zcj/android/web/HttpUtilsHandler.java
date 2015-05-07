package com.zcj.android.web;

import java.io.File;
import java.util.Date;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zcj.util.UtilDate;
import com.zcj.util.json.json.JSONObject;

public class HttpUtilsHandler {

	private static HttpUtils http;

	private HttpUtilsHandler() {

	}

	private static synchronized HttpUtils getInstance() {
		if (http == null) {
			http = new HttpUtils();
		}
		return http;
	}

	/**
	 * 发起网络请求（请求失败时重新发起一次请求，失败不提示）
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            请求参数
	 */
	public static void send(final String url, final RequestParams params) {
		send(null, url, params, null, false);
	}

	/**
	 * 发起网络请求（请求失败时重新发起一次请求，失败不提示）
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @param callback
	 *            请求成功或失败时运行的方法（包括success和error）
	 */
	public static void send(final String url, final RequestParams params, final HttpCallback callback) {
		send(null, url, params, callback, false);
	}

	// public static void send(final String url, final RequestParams params,
	// final HttpCallback callback, final ProgressDialog loadingDialog) {
	// send(null, url, params, callback, false, loadingDialog);
	// }

	/**
	 * 发起网络请求（请求失败时重新发起一次请求）
	 * 
	 * @param context
	 *            上下文
	 * @param url
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @param callback
	 *            请求成功或失败时运行的方法（包括success和error）
	 * @param alertErrorString
	 *            请求失败时是否提示错误信息
	 */
	public static void send(final Context context, final String url, final RequestParams params, final HttpCallback callback,
			final boolean alertErrorString) {
		sendFunction(context, url, params, callback, alertErrorString, true, null);
	}

	// public static void send(final Context context, String url, RequestParams
	// params, final HttpCallback callback, final boolean alertErrorString,
	// final ProgressDialog loadingDialog) {
	// sendFunction(context, url, params, callback, alertErrorString, true,
	// loadingDialog);
	// }

	/**
	 * 发起网络请求
	 * 
	 * @param context
	 *            上下文
	 * @param url
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @param callback
	 *            请求成功或失败时运行的方法（包括success和error）
	 * @param alertErrorString
	 *            请求失败时是否提示错误信息
	 * @param reload
	 *            请求失败时是否重新发起一次请求
	 * @param loadingDialog
	 *            绑定外部加载框
	 */
	public static void sendFunction(final Context context, final String url, final RequestParams params, final HttpCallback callback,
			final boolean alertErrorString, final boolean reload, final ProgressDialog loadingDialog) {
		if (loadingDialog != null) {
			loadingDialog.setMessage("加载中...");
			loadingDialog.show();
		}
		HttpUtilsHandler.getInstance().send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = responseInfo.result;
				ServiceResult sr = new ServiceResult();
				try {
					sr = ServiceResult.GSON_DT.fromJson(result, ServiceResult.class);
				} catch (Exception e1) {
					sr.setS(0);
					sr.setD("操作失败：数据解析出错");
				}
				if (sr.success()) {
					if (callback != null) {
						String dataJsonString = null;
						try {
							dataJsonString = new JSONObject(result).get("d").toString();
						} catch (Exception e) {
						}
						callback.success(dataJsonString);
					}
				} else {
					if (callback != null) {
						callback.error();
					}
					if (alertErrorString) {
						String errorString = "";
						if (sr.getD() != null) {
							errorString = "：" + String.valueOf(sr.getD());
						}
						Toast.makeText(context, "操作失败" + errorString, Toast.LENGTH_SHORT).show();
					}
				}
				if (loadingDialog != null) {
					loadingDialog.dismiss();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				if (reload) {
					sendFunction(context, url, params, callback, alertErrorString, false, loadingDialog);
				} else {
					if (callback != null) {
						callback.error();
					}
					if (alertErrorString) {
						Toast.makeText(context, "网络异常，请求超时", Toast.LENGTH_SHORT).show();
					}
					if (loadingDialog != null) {
						loadingDialog.dismiss();
					}
				}
			}
		});
	}

	public static RequestParams initParams(String[] keys, Object[] values) {
		if (keys == null || values == null || keys.length != values.length) {
			return null;
		}
		RequestParams params = new RequestParams();
		for (int i = 0; i < keys.length; i++) {
			if (values[i] != null) {
				if (values[i] instanceof File) {
					params.addBodyParameter(keys[i], (File) values[i]);
				} else if (values[i] instanceof Date) {
					params.addBodyParameter(keys[i], UtilDate.format((Date) values[i]));
				} else {
					params.addBodyParameter(keys[i], String.valueOf(values[i]));
				}
			}
		}
		return params;
	}

}
