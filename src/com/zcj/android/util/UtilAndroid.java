package com.zcj.android.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

public class UtilAndroid {

	/** 获取当前进程名 */
	public static String getProcessName(Context context) {
		int pid = android.os.Process.myPid();
		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return null;
	}

	/** 获取应用程序名称 */
	public static String getAppName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			int labelRes = packageInfo.applicationInfo.labelRes;
			return context.getResources().getString(labelRes);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** 获取应用程序版本名称信息 */
	public static String getVersionName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionName;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** 获取屏幕宽度 */
	public static int getWindowWidth(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int rWidth = dm.widthPixels;
		return rWidth;
	}

	/** 获取屏幕宽度 */
	@SuppressWarnings("deprecation")
	public static int getWindowWidth(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getWidth();
	}

	/** 获得屏幕宽度 */
	public static int getScreenWidth(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	/** 获得屏幕高度 */
	public static int getScreenHeight(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}

	/** 获得状态栏的高度 */
	public static int getStatusHeight(Context context) {
		int statusHeight = -1;
		try {
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusHeight;
	}

	/** dp转px */
	public static int dp2px(Activity context, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
	}

	/** sp转px */
	public static int sp2px(Context context, float spVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context.getResources().getDisplayMetrics());
	}

	/** px转dp */
	public static float px2dp(Context context, float pxVal) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (pxVal / scale);
	}

	/** px转sp */
	public static float px2sp(Context context, float pxVal) {
		return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
	}

	/** 判断网络是否可用 */
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager == null) {
			return false;
		}
		NetworkInfo networkinfo = manager.getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isConnected()) {
			return false;
		}
		return true;
	}

	/** 判断GPS是否打开 */
	public static boolean isGpsEnabled(Context context) {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	/** 判断WIFI网络是否可用 */
	public static boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/** 判断MOBILE网络是否可用 */
	public static boolean isMobileConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/** 获取手机号码(移动神州行,联通的卡是可以取到的.动感地带的取不到) */
	public static String getPhone(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getLine1Number();
	}

	/**
	 * 取得设备的唯一标识 DEVICE_ID(IMEI) -> ANDROID_ID -> Sim Serial Number -> MAC ->
	 * UUID(SharedPreferences)
	 * 
	 * @param context
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public static String getUdid(Context context, String xmlFileName, String xmlKey) {

		String imei = getIMEI(context);
		if (imei != null) {
			return imei;
		}

		String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		if (androidId != null && androidId.trim().length() > 0 && !"9774d56d682e549c".equals(androidId.toLowerCase())) {
			return androidId;
		}

		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String simSerialNumber = telephonyManager.getSimSerialNumber();
		if (simSerialNumber != null && simSerialNumber.trim().length() > 0) {
			return simSerialNumber;
		}

		String macAddress = getMacAddress(context);
		if (macAddress != null) {
			return macAddress;
		}

		String theValue = UtilSharedPreferences.get(context, xmlFileName, xmlKey);
		if (theValue != null && theValue.trim().length() > 0) {
			return theValue;
		}

		String defaultValue = UUID.randomUUID().toString().replaceAll("-", "");
		boolean saveOk = UtilSharedPreferences.save(context, xmlFileName, xmlKey, defaultValue);
		if (saveOk) {
			return defaultValue;
		}
		return null;
	}

	/**
	 * 获取IMEI(国际移动装备辨识码)，返回NULL表示获取失败，如果是双卡，则两个值通过“&”字符拼接
	 * <p>
	 * 需要的权限： {@link android.Manifest.permission#READ_PHONE_STATE
	 * android.permission.READ_PHONE_STATE}
	 */
	public static String getIMEI(Context context) {
		DoubleCardInfo doubleInfo = doubleSimInfo(context);
		if (doubleInfo == null) {
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = telephonyManager.getDeviceId();
			if (imei != null && imei.length() >= 10 && !"358673013795895".equals(imei) && !"004999010640000".equals(imei)
					&& !"00000000000000".equals(imei) && !"000000000000000".equals(imei)) {
				return imei;
			}
			return null;
		} else {
			return doubleInfo.getImei_1() + "&" + doubleInfo.getImei_2();
		}
	}

	/**
	 * 获取IMSI(国际移动用户识别码)，返回NULL表示获取失败，如果是双卡，则两个值通过“&”字符拼接
	 * <p>
	 * 需要的权限： {@link android.Manifest.permission#READ_PHONE_STATE
	 * android.permission.READ_PHONE_STATE}
	 */
	public static String getIMSI(Context context) {
		DoubleCardInfo doubleInfo = doubleSimInfo(context);
		if (doubleInfo == null) {
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String imsi = telephonyManager.getSubscriberId();
			if (imsi != null && imsi.length() > 0) {
				return imsi;
			}
			return null;
		} else {
			return doubleInfo.getImsi_1() + "&" + doubleInfo.getImsi_2();
		}
	}

	/** 获取mac地址，返回NULL表示获取失败 */
	public static String getMacAddress(Context context) {
		try {
			WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			String mac = wifimanager.getConnectionInfo().getMacAddress();
			if (mac != null && mac.trim().length() > 0) {
				return mac;
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	/** 获取蓝牙MAC地址，返回NULL表示获取失败（蓝牙打开状态才能获取） */
	public static String getBluetoothMacAddress() {
		try {
			BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			return bluetoothAdapter.getAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/** 如果是双卡手机，则返回双卡手机信息(包括两个IMEI和两个IMSI )；如果不是，则返回NULL */
	private static DoubleCardInfo doubleSimInfo(Context c) {
		DoubleCardInfo gaotongDoubleInfo = initQualcommDoubleSim(c);
		boolean isGaoTongCpu = gaotongDoubleInfo.isGaotongDoubleSim();
		if (isGaoTongCpu) {
			// 高通芯片双卡
			return gaotongDoubleInfo;
		}
		DoubleCardInfo mtkDoubleInfo = initMtkDoubleSim(c);
		boolean isMtkCpu = mtkDoubleInfo.isMtkDoubleSim();
		if (isMtkCpu) {
			// MTK芯片双卡
			return mtkDoubleInfo;
		}
		// 普通单卡手机
		return null;
	}
	
	@SuppressWarnings("unused")
	private static class DoubleCardInfo {
		private Integer simId_1;
		private Integer simId_2;
		private String imsi_1;
		private String imsi_2;
		private String imei_1;
		private String imei_2;
		private String defaultImsi;
		private Integer phoneType_1;
		private Integer phoneType_2;
		private boolean mtkDoubleSim;
		private boolean gaotongDoubleSim;
		public boolean isGaotongDoubleSim() {
			return gaotongDoubleSim;
		}
		public void setGaotongDoubleSim(boolean gaotongDoubleSim) {
			this.gaotongDoubleSim = gaotongDoubleSim;
		}
		public boolean isMtkDoubleSim() {
			return mtkDoubleSim;
		}
		public void setMtkDoubleSim(boolean mtkDoubleSim) {
			this.mtkDoubleSim = mtkDoubleSim;
		}
		public Integer getPhoneType_1() {
			return phoneType_1;
		}
		public void setPhoneType_1(Integer phoneType_1) {
			this.phoneType_1 = phoneType_1;
		}
		public Integer getPhoneType_2() {
			return phoneType_2;
		}
		public void setPhoneType_2(Integer phoneType_2) {
			this.phoneType_2 = phoneType_2;
		}
		public String getDefaultImsi() {
			return defaultImsi;
		}
		public void setDefaultImsi(String defaultImsi) {
			this.defaultImsi = defaultImsi;
		}
		public String getImsi_1() {
			return imsi_1;
		}
		public void setImsi_1(String imsi_1) {
			this.imsi_1 = imsi_1;
		}
		public String getImsi_2() {
			return imsi_2;
		}
		public void setImsi_2(String imsi_2) {
			this.imsi_2 = imsi_2;
		}
		public String getImei_1() {
			return imei_1;
		}
		public void setImei_1(String imei_1) {
			this.imei_1 = imei_1;
		}
		public String getImei_2() {
			return imei_2;
		}
		public void setImei_2(String imei_2) {
			this.imei_2 = imei_2;
		}
		public Integer getSimId_1() {
			return simId_1;
		}
		public void setSimId_1(Integer simId_1) {
			this.simId_1 = simId_1;
		}
		public Integer getSimId_2() {
			return simId_2;
		}
		public void setSimId_2(Integer simId_2) {
			this.simId_2 = simId_2;
		}
	}
	
	private static DoubleCardInfo initMtkDoubleSim(Context mContext) {
		DoubleCardInfo mtkDoubleInfo = new DoubleCardInfo();
		try {
			TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
			Class<?> c = Class.forName("com.android.internal.telephony.Phone");
			Field fields1 = c.getField("GEMINI_SIM_1");
			fields1.setAccessible(true);
			mtkDoubleInfo.setSimId_1((Integer) fields1.get(null));
			Field fields2 = c.getField("GEMINI_SIM_2");
			fields2.setAccessible(true);
			mtkDoubleInfo.setSimId_2((Integer) fields2.get(null));
			Method m = TelephonyManager.class.getDeclaredMethod("getSubscriberIdGemini", int.class);
			mtkDoubleInfo.setImsi_1((String) m.invoke(tm, mtkDoubleInfo.getSimId_1()));
			mtkDoubleInfo.setImsi_2((String) m.invoke(tm, mtkDoubleInfo.getSimId_2()));

			Method m1 = TelephonyManager.class.getDeclaredMethod("getDeviceIdGemini", int.class);
			mtkDoubleInfo.setImei_1((String) m1.invoke(tm, mtkDoubleInfo.getSimId_1()));
			mtkDoubleInfo.setImei_2((String) m1.invoke(tm, mtkDoubleInfo.getSimId_2()));

			Method mx = TelephonyManager.class.getDeclaredMethod("getPhoneTypeGemini", int.class);
			mtkDoubleInfo.setPhoneType_1((Integer) mx.invoke(tm, mtkDoubleInfo.getSimId_1()));
			mtkDoubleInfo.setPhoneType_2((Integer) mx.invoke(tm, mtkDoubleInfo.getSimId_2()));

			if (TextUtils.isEmpty(mtkDoubleInfo.getImsi_1()) && (!TextUtils.isEmpty(mtkDoubleInfo.getImsi_2()))) {
				mtkDoubleInfo.setDefaultImsi(mtkDoubleInfo.getImsi_2());
			}
			if (TextUtils.isEmpty(mtkDoubleInfo.getImsi_2()) && (!TextUtils.isEmpty(mtkDoubleInfo.getImsi_1()))) {
				mtkDoubleInfo.setDefaultImsi(mtkDoubleInfo.getImsi_1());
			}
		} catch (Exception e) {
			mtkDoubleInfo.setMtkDoubleSim(false);
			return mtkDoubleInfo;
		}
		mtkDoubleInfo.setMtkDoubleSim(true);
		return mtkDoubleInfo;
	}
	
	private static DoubleCardInfo initQualcommDoubleSim(Context mContext) {
		DoubleCardInfo gaotongDoubleInfo = new DoubleCardInfo();
		gaotongDoubleInfo.setSimId_1(0);
		gaotongDoubleInfo.setSimId_2(1);
		try {
			Class<?> cx = Class.forName("android.telephony.MSimTelephonyManager");
			Object obj = mContext.getSystemService("phone_msim");

			Method md = cx.getMethod("getDeviceId", int.class);
			Method ms = cx.getMethod("getSubscriberId", int.class);

			gaotongDoubleInfo.setImei_1((String) md.invoke(obj, gaotongDoubleInfo.getSimId_1()));
			gaotongDoubleInfo.setImei_2((String) md.invoke(obj, gaotongDoubleInfo.getSimId_2()));
			gaotongDoubleInfo.setImsi_1((String) ms.invoke(obj, gaotongDoubleInfo.getSimId_1()));
			gaotongDoubleInfo.setImsi_2((String) ms.invoke(obj, gaotongDoubleInfo.getSimId_2()));
		} catch (Exception e) {
			gaotongDoubleInfo.setGaotongDoubleSim(false);
			return gaotongDoubleInfo;
		}
		gaotongDoubleInfo.setGaotongDoubleSim(true);
		return gaotongDoubleInfo;
	}

	/** 获取SDK号 */
	public static int getAndroidSdk() {
		return android.os.Build.VERSION.SDK_INT;
	}

	/** 获取Android系统版本号 */
	public static String getAndroidVersion() {
		return android.os.Build.VERSION.RELEASE;
	}

	/** 获取手机型号 */
	public static String getPhoneVersion() {
		return android.os.Build.MODEL;
	}

}
