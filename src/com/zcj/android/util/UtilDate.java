package com.zcj.android.util;

import java.text.SimpleDateFormat;

@Deprecated
public class UtilDate {

	public static String durationChinese(long duration) {
		return com.zcj.util.UtilDate.durationChinese(duration);
	}

	public static final ThreadLocal<SimpleDateFormat> SDF_DATETIME = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	public static final ThreadLocal<SimpleDateFormat> SDF_DATE = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};

	public static final ThreadLocal<SimpleDateFormat> SDF_TIME = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("HH:mm:ss");
		}
	};
}
