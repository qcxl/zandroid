package com.zcj.android.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

import com.zcj.android.util.bean.InternetBean;
import com.zcj.util.UtilDate;
import com.zcj.util.UtilString;

/**
 * 手机浏览器历史记录、书签等操作
 * 
 * @author zouchongjin@sina.com
 * @data 2015年6月12日
 */
public class UtilInternet {

	/**
	 * 获取所有的浏览器历史记录和书签
	 * <p>
	 * 需要的权限：
	 * <p>
	 * {@link android.Manifest.permission#READ_HISTORY_BOOKMARKS
	 * com.android.browser.permission.READ_HISTORY_BOOKMARKS}
	 */
	public static List<InternetBean> getAllHostory(Context context) {
		List<InternetBean> list = new ArrayList<InternetBean>();
		try {
			Cursor cur = context.getContentResolver().query(Uri.parse("content://browser/bookmarks"),
					new String[] { "title", "url", "date" }, null, null, "date desc");

			while (cur.moveToNext()) {
				String title = cur.getString(cur.getColumnIndex("title"));
				String url = cur.getString(cur.getColumnIndex("url"));
				String date = cur.getString(cur.getColumnIndex("date"));
				String type = "";
				if (UtilString.isNotBlank(date)) {
					date = UtilDate.format(new Date(Long.parseLong(date)));
					type = InternetBean.TYPE_HOSTORY;
				} else {
					type = InternetBean.TYPE_BOOKMARKS;
				}
				list.add(new InternetBean(title, url, date, type));
			}

			cur.close();
		} catch (SQLiteException ex) {
		}
		return list;
	}

}
