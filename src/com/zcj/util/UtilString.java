package com.zcj.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilString {

	public static final IdWorker WORKER = new IdWorker(8);
	
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

	/** 获取一个15位的唯一标识 */
	public static Long getLongUUID() {
		return WORKER.nextId();
	}

	/** 通过MD5加密 */
	public static String getMd5(String str) {
		return UtilEncryption.toMD5(str);
	}

	/** 初始化一个32位的UUID */
	public static String getUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

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

	/** 提取字符串中的数字 */
	public static String getNumeric(String value) {
		if (isBlank(value)) {
			return "";
		}
		String regEx = "[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(value);
		return m.replaceAll("").trim();
	}

	/** 计算折扣 */
	public static double discount(Float theOld, Float theNew) {
		return Math.ceil((theNew / theOld) * 100) / 10;
	}

	/** 获取随机数 */
	public static int getRandom(final int min, final int max) {
		int tmp = Math.abs(new Random().nextInt());
		return tmp % (max - min + 1) + min;
	}

	/** 验证身份证号 */
	public static boolean CheckIDCard(String idcard) {
		return ValidatorIdcard.isValidatedAllIdcard(idcard);
	}

	/** 验证是否为IP地址 */
	public static boolean isIp(String ip) {
		return ValidatorIP.isIp(ip);
	}

	/** 验证ip是否在ipMap(key~value)区间中 */
	public static boolean isIpInner(String ip, Map<String, String> ipMap) {
		return ValidatorIP.isIPInner(ip, ipMap);
	}

	/** 验证是否为邮箱地址 */
	public static boolean isEmail(String email) {
		if(email == null || email.trim().length()==0) 
			return false;
		Pattern pattern = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
		return pattern.matcher(email).matches();
	}
	
	/** 验证是否为手机号码 */
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

	/** 判断字符串格式是否正确，且字节数必须小于某值，且不能为空，且只能是数字、字母、下划线、中文 */
	public static boolean lengthVerify(String value, int length) {
		if (isBlank(value)) {
			return false;
		}

		char[] vs = value.toCharArray();
		int lenght = 0;

		Pattern pattern = Pattern.compile("^[\\u4e00-\\u9fa5]{1}$");
		Pattern pattern2 = Pattern.compile("^[\\dA-Za-z_]{1}$");

		for (char v : vs) {
			Matcher matcher = pattern.matcher(String.valueOf(v));
			Matcher matcher2 = pattern2.matcher(String.valueOf(v));
			if (matcher.matches()) {
				lenght = lenght + 2;
			} else if (matcher2.matches()) {
				lenght = lenght + 1;
			} else {
				return false;
			}
		}

		return (lenght <= length);
	}

	/**
	 * 移除原字符串中的某元素 UtilString.rejectElem("a,b,c","b")="a,c"
	 */
	public static String rejectElem(String src, String elem) {
		return rejectElem(src, elem, ",");
	}

	/**
	 * 移除原字符串中的某元素 UtilString.rejectElem("a,b,c","b")="a,c"
	 */
	public static String rejectElem(String src, String elem, String splite) {
		if (isBlank(src) || isBlank(elem)) {
			return src;
		}
		String temp = (splite + src + splite).replace(splite + elem + splite, splite);
		temp = temp.substring(1, temp.length() > 1 ? temp.length() - 1 : 1);
		return temp;
	}

	/**
	 * 把字符串中的某元素置顶 UtilString.topElem("a,b,c","b")="b,a,c"
	 */
	public static String topElem(String src, String elem) {
		if (isBlank(src) || isBlank(elem) || !src.contains(elem)) {
			return src;
		}
		String temp = rejectElem(src, elem);
		if (isBlank(temp)) {
			return elem;
		} else {
			temp = elem + "," + temp;
			return temp;
		}
	}

	/** Collection格式转换成用英文逗号分隔的String */
	public static String collectionToString(Collection<String> collection) {
		return UtilString.collectionToString(collection, ",");
	}

	/** Collection格式转换成用指定分隔符splite分隔的String */
	public static String collectionToString(Collection<String> collection, String splite) {
		StringBuilder sb = new StringBuilder();
		boolean isFirst = true;
		for (String a : collection) {
			if (isFirst) {
				sb.append(a);
			} else {
				sb.append(splite);
				sb.append(a);
			}
			isFirst = false;
		}
		return sb.toString();
	}

	/** 逗号分隔的字符串转换成List格式 */
	public static List<String> stringToList(String value) {
		if (isBlank(value)) {
			return new ArrayList<String>();
		} else {
			return Arrays.asList(value.split(","));
		}
	}

	/** 逗号分隔的字符串转换成List格式 */
	public static List<Long> stringToLongList(String value) {
		List<String> stringList = stringToList(value);
		List<Long> result = new ArrayList<Long>();
		for (String v : stringList) {
			result.add(Long.parseLong(v));
		}
		return result;
	}

	/** Map转Json */
	public static String mapToJson(Map<String, String> theMap) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		boolean isFirst = true;
		for (Map.Entry<String, String> entry : theMap.entrySet()) {
			if (isFirst) {
				sb.append("\"" + entry.getKey() + "\":\"" + entry.getValue() + "\"");
			} else {
				sb.append(",");
				sb.append("\"" + entry.getKey() + "\":\"" + entry.getValue() + "\"");
			}
			isFirst = false;
		}
		sb.append("}");
		return sb.toString();
	}

	/** 倒序 */
	public static Double[] sortDesc(Double[] array) {
		Arrays.sort(array);
		Double t;
		for (int i = 0; i < array.length / 2; i++) {
			t = array[i];
			array[i] = array[array.length - 1 - i];
			array[array.length - 1 - i] = t;
		}
		return array;
	}

	/** 替换Map的key和value的位置 */
	public static Map<String, Long> mapKeyValueReplace(Map<Long, String> map) {
		if (map == null) {
			return null;
		}
		Map<String, Long> result = new HashMap<String, Long>();
		for (Map.Entry<Long, String> entry : map.entrySet()) {
			result.put(entry.getValue(), entry.getKey());
		}
		return result;
	}

	/** 过滤HTML标签 */
	public static String delHTMLTag(String htmlStr) {
		String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
		String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
		String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

		Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
		Matcher m_script = p_script.matcher(htmlStr);
		htmlStr = m_script.replaceAll(""); // 过滤script标签

		Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
		Matcher m_style = p_style.matcher(htmlStr);
		htmlStr = m_style.replaceAll(""); // 过滤style标签

		Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
		Matcher m_html = p_html.matcher(htmlStr);
		htmlStr = m_html.replaceAll(""); // 过滤html标签

		return htmlStr.trim(); // 返回文本字符串
	}

	/** 截取字符串 */
	public static String subString(String str, int length) {
		if (isBlank(str)) {
			return "";
		}
		if (str.length() <= length) {
			return str;
		}
		return str.substring(0, length);
	}
	
	/** 首字母小写 */
	public static String firstLower(String str) {
		if (isBlank(str)) {
			return str;
		}
		char[] chars = new char[1];
		chars[0] = str.charAt(0);
		String temp = new String(chars);
		if (chars[0] >= 'A' && chars[0] <= 'Z') {
			return str.replaceFirst(temp, temp.toLowerCase());
		} else {
			return str;
		}
	}

}
