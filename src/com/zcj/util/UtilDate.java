package com.zcj.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class UtilDate {
	
	/**  
	 * UtilDate.SDF_DATETIME.get().format(new Date())
	 * UtilDate.SDF_DATETIME.get().parse("2013-12-12 00:00:00")
	 *  */
	public static final ThreadLocal<SimpleDateFormat> SDF_DATETIME = new ThreadLocal<SimpleDateFormat>(){
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};
	
	public static final ThreadLocal<SimpleDateFormat> SDF_DATE = new ThreadLocal<SimpleDateFormat>(){
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
	 * 秒数转成00:00:00格式的字符串
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

	/** 计算从某时间开始，满足几点几分几秒星期几的最近的时间 */
	public Calendar getEarliestDate(Calendar currentDate, int dayOfWeek,
			int hourOfDay, int minuteOfHour, int secondOfMinite) {

		int currentWeekOfYear = currentDate.get(Calendar.WEEK_OF_YEAR);
		int currentDayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK);
		int currentHour = currentDate.get(Calendar.HOUR_OF_DAY);
		int currentMinute = currentDate.get(Calendar.MINUTE);
		int currentSecond = currentDate.get(Calendar.SECOND);

		boolean weekLater = false;
		// 如果输入条件中的dayOfWeek小于当前日期的dayOfWeek,则WEEK_OF_YEAR需要推迟一周
		if (dayOfWeek < currentDayOfWeek) {
			weekLater = true;
		} else if (dayOfWeek == currentDayOfWeek) {
			// 当输入条件与当前日期的dayOfWeek相等时，如果输入条件中的hourOfDay小于当前日期的currentHour，则WEEK_OF_YEAR需要推迟一周
			if (hourOfDay < currentHour) {
				weekLater = true;
			} else if (hourOfDay == currentHour) {
				// 当输入条件与当前日期的dayOfWeek, hourOfDay相等时，如果输入条件中的minuteOfHour小于当前日期的currentMinute，则WEEK_OF_YEAR需要推迟一周
				if (minuteOfHour < currentMinute) {
					weekLater = true;
				} else if (minuteOfHour == currentSecond) {
					// 当输入条件与当前日期的dayOfWeek, hourOfDay，minuteOfHour相等时，如果输入条件中的secondOfMinite小于当前日期的currentSecond，则WEEK_OF_YEAR需要推迟一周
					if (secondOfMinite < currentSecond) {
						weekLater = true;
					}
				}
			}
		}
		if (weekLater) {
			// 设置当前日期中的WEEK_OF_YEAR为当前周推迟一周
			currentDate.set(Calendar.WEEK_OF_YEAR, currentWeekOfYear + 1);
		}
		// 设置当前日期中的DAY_OF_WEEK,HOUR_OF_DAY,MINUTE,SECOND为输入条件中的值。
		currentDate.set(Calendar.DAY_OF_WEEK, dayOfWeek);
		currentDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
		currentDate.set(Calendar.MINUTE, minuteOfHour);
		currentDate.set(Calendar.SECOND, secondOfMinite);
		return currentDate;
	}
	
	/** 对比两个时间相隔几天（过了24点就算隔一天,如果其中一个时间为null，则返回null） */
	public static Integer contrastDate(Date time, Date lastTime) {
		if (time == null || lastTime == null)
			return null;
		Calendar aCalendar = Calendar.getInstance();
		aCalendar.setTime(lastTime);
		int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
		aCalendar.setTime(time);
		int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
		return new Integer(day2 - day1);
	}
	
	/** 取得某一天的几天后的日期 */
	public static Date later(Date base, int later) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(base);
		cal.add(Calendar.DAY_OF_MONTH, later);
		return cal.getTime();
	}
	
	/** 取得某年某月的最后一天 */
	public static Date getLastDayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(year, month - 1, 1, 0, 0, 0);
		cal.add(Calendar.MONTH, 1);// 月份加一，得到下个月的一号
		cal.add(Calendar.DATE, -1);// 下一个月减一为本月最后一天
		return cal.getTime();// 获得月末是几号
	}
	
	/** 初始化一个Date类型的值 */
	public static Date initDate(int year, int month, int date, int hourOfDay, int minute, int second) {
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(year, month-1, date, hourOfDay, minute, second);
		return c.getTime();
	}
	
	/** 获取只有日期的值，如果参数错误，则返回今天 */
	public static Date initDateNoTimeByString(String date) {
		Date result = new Date();
		try {
			result = UtilDate.SDF_DATE.get().parse(date);
		} catch (Exception e) {
			Calendar c = Calendar.getInstance();
			result = UtilDate.initDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DATE), 0, 0, 0);
		}
		return result;
	}

	/** 取得本月的最后一天 */
	public static Date getLastDayOfThisMonth() {
		Calendar cal = Calendar.getInstance();
		return getLastDayOfMonth(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1);
	}
	
	/** 取得两个时间相差的秒数 */
	public static String dateDiff_Sec(Date begin, Date end) {
		return String.valueOf(((float)(end.getTime() - begin.getTime()) / 1000));
	}
	
	/** String 转 DateTime */
	public static Date toDateTime(String date) {
		try {
			return SDF_DATETIME.get().parse(date);
		} catch (ParseException e) {
			return null;
		}
	}
	
	/** 计算还剩余多少时间(x天xx时) */
	public static String friendly_after(Date date) {
		Date now = new Date();
		if (date != null && date.after(now)) {
			long sy = (date.getTime() - now.getTime())/(1000*60*60);
			int xs = (int) sy;
			int day = xs / 24;
			xs %= 24;
			return String.format("%d天%02d时", day, xs);
		} else {
			return null;
		}
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
	
	/** 以友好的方式显示时间 */
	public static String friendly_time(Date time) {
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
	
	/** 以友好的方式显示时间 */
	public static String friendly_time(String sdate) {
		Date time = toDateTime(sdate);
		return friendly_time(time);
	}
	
}
