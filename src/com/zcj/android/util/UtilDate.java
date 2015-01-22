package com.zcj.android.util;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class UtilDate {

	/**
	 * UtilDate.SDF_DATETIME.get().format(new Date())
	 * UtilDate.SDF_DATETIME.get().parse("2013-12-12 00:00:00")
	 * */
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

	/** Date 转 String(yyyy-MM-dd HH:mm:ss) */
	public static String format(Date date) {
		return SDF_DATETIME.get().format(date);
	}

	/** String 转 Date */
	public static Date toDate(String date) {
		try {
			return SDF_DATETIME.get().parse(date);
		} catch (ParseException e) {
			return null;
		}
	}
	
	/**
	 * 判断给定字符串时间是否为今日
	 * @param sdate
	 * @return boolean
	 */
	public static boolean isToday(String sdate){
		boolean b = false;
		Date time = toDate(sdate);
		Date today = new Date();
		if(time != null){
			String nowDate = SDF_DATE.get().format(today);
			String timeDate = SDF_DATE.get().format(time);
			if(nowDate.equals(timeDate)){
				b = true;
			}
		}
		return b;
	}

	/**
	 * 以友好的方式显示时间
	 * 
	 * @param sdate
	 * @return
	 */
	public static String friendly_time(String sdate) {
		Date time = toDate(sdate);
		if (time == null) {
			return "Unknown";
		}
		String ftime = "";
		Calendar cal = Calendar.getInstance();

		// 判断是否是同一天
		String curDate = SDF_DATE.get().format(cal.getTime());
		String paramDate = SDF_DATE.get().format(time);
		if (curDate.equals(paramDate)) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
			if (hour == 0)
				ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1) + "分钟前";
			else
				ftime = hour + "小时前";
			return ftime;
		}

		long lt = time.getTime() / 86400000;
		long ct = cal.getTimeInMillis() / 86400000;
		int days = (int) (ct - lt);
		if (days == 0) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
			if (hour == 0)
				ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1) + "分钟前";
			else
				ftime = hour + "小时前";
		} else if (days == 1) {
			ftime = "昨天";
		} else if (days == 2) {
			ftime = "前天";
		} else if (days > 2 && days <= 10) {
			ftime = days + "天前";
		} else if (days > 10) {
			ftime = SDF_DATE.get().format(time);
		}
		return ftime;
	}

	/**
	 * 秒数转成00:00:00格式的字符串
	 * 
	 * @param duration
	 * @return
	 */
	public static String duration(long duration) {
		int timetiem = (int) duration;
		int minute = timetiem / 60;
		int hour = minute / 60;
		int second = timetiem % 60;
		minute %= 60;
		return String.format("%02d:%02d:%02d", hour, minute, second);
	}
	
	/**
	 * 秒数转成3天20时25分06秒格式的字符串
	 * @param duration
	 * @return
	 */
	public static String durationChinese(long duration) {
		int timetiem = (int) duration;
		int minute = timetiem / 60;
		int hour = minute / 60;
		int day = hour / 24;
		int second = timetiem % 60;
		minute %= 60;
		hour %= 24;
		return String.format("%d天%02d时%02d分%02d秒", day, hour, minute, second);
	}

}
