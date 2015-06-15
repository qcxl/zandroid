package com.zcj.android.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.telephony.SmsManager;

import com.zcj.android.util.bean.MessageBean;
import com.zcj.util.UtilDate;
import com.zcj.util.UtilString;

/**
 * 手机短信 相关操作
 * 
 * @author zouchongjin@sina.com
 * @data 2015年4月2日
 */
public class UtilMessage {

	/**
	 * 获取所有短信内容
	 * <p>
	 * 需要的权限：
	 * <p>
	 * {@link android.Manifest.permission#READ_SMS android.permission.READ_SMS}
	 */
	public static List<MessageBean> getAllMessages(Context context) {
		List<MessageBean> list = new ArrayList<MessageBean>();
		try {
			Cursor cur = context.getContentResolver().query(Uri.parse("content://sms/"),
					new String[] { "_id", "address", "person", "body", "date", "type" }, null, null, "date desc");

			while (cur.moveToNext()) {
				String name = cur.getString(cur.getColumnIndex("person"));
				String phoneNumber = cur.getString(cur.getColumnIndex("address"));
				String smsbody = cur.getString(cur.getColumnIndex("body"));
				String date = UtilDate.format(new Date(Long.parseLong(cur.getString(cur.getColumnIndex("date")))));
				String type;
				int typeId = cur.getInt(cur.getColumnIndex("type"));
				if (typeId == 1) {
					type = "接收";
				} else if (typeId == 2) {
					type = "发送";
				} else if (typeId == 0) {
					type = "未读";
				} else {
					type = "草稿";
				}

				if (smsbody == null) {
					smsbody = "";
				}
				list.add(new MessageBean(name, phoneNumber, smsbody, date, type));
			}
		} catch (SQLiteException ex) {
		}
		return list;
	}

	/**
	 * 发送短信
	 * <p>
	 * 需要的权限：
	 * <p>
	 * {@link android.Manifest.permission#WRITE_SMS
	 * android.permission.WRITE_SMS}
	 * 
	 * @param number
	 *            手机号码：+8618271803015
	 * @param content
	 *            发送内容：如果内容过长，会自动分段发送
	 * @return
	 */
	public static boolean sendMessage(String number, String content) {
		if (UtilString.isBlank(number))
			return false;
		SmsManager manager = SmsManager.getDefault();
		ArrayList<String> texts = manager.divideMessage(content);
		for (String text : texts) {
			manager.sendTextMessage(number, null, text, null, null);
		}
		return true;
	}
}
