package com.zcj.android.util;

import java.util.Date;

@Deprecated
public class UtilString {

	public static boolean isNotBlank(String str) {
		return com.zcj.util.UtilString.isNotBlank(str);
	}

	public static boolean isBlank(String str) {
		return com.zcj.util.UtilString.isBlank(str);
	}
	
	public static String getSoleCode() {
		return com.zcj.util.UtilString.getSoleCode(new Date());
	}
	
}
