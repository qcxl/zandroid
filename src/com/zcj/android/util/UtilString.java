package com.zcj.android.util;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("SimpleDateFormat")
public class UtilString {

	/** 根据当前日期生成20位的唯一编码 */
	public static String getSoleCode() {
		return UtilString.getSoleCode(new Date());
	}

	/** 根据指定日期生成20位的唯一编码 */
	public static String getSoleCode(Date date) {
		SimpleDateFormat from = new SimpleDateFormat("yyyyMMddHHmmss");
		String time = from.format(date);
		int tmp = UtilString.getRandom(100000, 999999);
		return time + tmp;
	}

	/** 获取随机数 */
	public static int getRandom(final int min, final int max) {
		int tmp = Math.abs(new Random().nextInt());
		return tmp % (max - min + 1) + min;
	}

	public static boolean isNotBlank(String str) {
		return !UtilString.isBlank(str);
	}

	public static boolean isBlank(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(str.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isEmail(String email) {
		if(email == null || email.trim().length()==0) 
			return false;
		Pattern pattern = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
		return pattern.matcher(email).matches();
	}

	public static boolean isPhone(String phone) {
		try {
			if (!phone.substring(0, 1).equals("1")) {
				return false;
			}
			if (phone == null || phone.length() != 11) {
				return false;
			}
			String check = "^[0123456789]+$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(phone);
			boolean isMatched = matcher.matches();
			if (isMatched) {
				return true;
			} else {
				return false;
			}
		} catch (RuntimeException e) {
			return false;
		}
	}

	/** 隐藏手机号码的中间四位 */
	public static String hidePhone(String sPhone) {
		if (sPhone.length() == 11) {
			return sPhone = sPhone.subSequence(0, 3) + "****" + sPhone.subSequence(7, 11);
		} else {
			return "****";
		}
	}

}
