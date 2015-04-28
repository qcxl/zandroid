package com.zcj.android.util;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.zcj.util.UtilString;

/**
 * 主要用于偏好参数设置
 * 		文件目录：/data/data/myPackageName/shared_prefs/myFileName.xml
 * 
 * @author ZCJ
 * @data 2013-11-14
 */
public class UtilSharedPreferences {

	/**
	 * 保存到XML配置文件
	 * @param context
	 * @param xmlFileName 文件名（不带后缀）
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean save(Context context, String xmlFileName, String key, String value) {
		if (UtilString.isBlank(key)) {
			return false;
		}
		Map<String, String> args = new HashMap<String, String>();
		args.put(key, value);
		return save(context, xmlFileName, args);
	}
	
	/**
	 * 保存到XML配置文件
	 * @param context
	 * @param xmlFileName 文件名（不带后缀）
	 * @param args
	 * @return
	 */
	public static boolean save(Context context, String xmlFileName, Map<String, String> args) {
		if (context == null || UtilString.isBlank(xmlFileName) || args == null || args.isEmpty()) {
			return false;
		}
		SharedPreferences sp = context.getSharedPreferences(xmlFileName, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		for (Map.Entry<String, String> entry : args.entrySet()) {
			editor.putString(entry.getKey(), entry.getValue());
		}
		editor.commit();
		return true;
	}
	
	/**
	 * 到XML配置文件里读取数据
	 * 		如果不存在，则返回空
	 * @param context
	 * @param xmlFileName 文件名（不带后缀）
	 * @param key
	 * @return
	 */
	public static String get(Context context, String xmlFileName, String key) {
		if (context == null || UtilString.isBlank(xmlFileName) || UtilString.isBlank(key)) {
			return null;
		}
		SharedPreferences sp = context.getSharedPreferences(xmlFileName, Context.MODE_PRIVATE);
		return sp.getString(key, null);
	}
	
}
