package com.zcj.android.util;

import java.text.NumberFormat;

public class UtilMath {
	
	/** 计算百分比 */
	public static String percent(int value, int full) {
		if (full == 0 || value < 0 || full <= 0) {return null;}
		return percent(Double.valueOf(value), Double.valueOf(full));
	}
	
	/** 计算百分比 */
	public static String percent(Long value, Long full) {
		if (full == 0 || value < 0 || full <= 0) {return null;}
		return percent(Double.valueOf(value), Double.valueOf(full));
	}
	
	/** 计算百分比 */
	public static String percent(Double value, Double full) {
		if (value == null || full == null) {
			return null;
		}
		double percent = 0;
		if (value > 0 && full > 0) {
			percent = value / full;
		}
		NumberFormat nt = NumberFormat.getPercentInstance();
		nt.setMinimumFractionDigits(2);
		return nt.format(percent);
	}

}
