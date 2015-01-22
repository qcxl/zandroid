package com.zcj.android.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.CallLog.Calls;

import com.zcj.android.util.bean.CallRecordBean;
import com.zcj.android.util.bean.ContactBean;

public class UtilContacts {
	
	/**
	 * 获取通话记录列表
	 * 		<uses-permission android:name="android.permission.READ_CALL_LOG"/>  
	 * @param context
	 * @return
	 */
	public static List<CallRecordBean> getCallRecord(Context context) {
		List<CallRecordBean> list = new ArrayList<CallRecordBean>();
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI,
				new String[] { CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME,
						CallLog.Calls.TYPE, CallLog.Calls.DATE,
						CallLog.Calls.DURATION }, null, null,
				CallLog.Calls.DEFAULT_SORT_ORDER);

		while (cursor.moveToNext()) {
			String strNumber = cursor.getString(cursor.getColumnIndex(Calls.NUMBER)); // 呼叫号码
			String strName = cursor.getString(cursor.getColumnIndex(Calls.CACHED_NAME)); // 联系人姓名
			int type = cursor.getInt(cursor.getColumnIndex(Calls.TYPE));// 来电:1,拨出:2,未接:3
			String callType = "";
			switch (type) {
			case 1:
				callType = "来电";
				break;
			case 2:
				callType = "拨出";
				break;
			case 3:
				callType = "未接";
				break;
			}
			String durationTime = UtilDate.duration(cursor.getLong(cursor.getColumnIndex(Calls.DURATION)));
			String time = UtilDate.format(new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(Calls.DATE)))));
			
			list.add(new CallRecordBean(strName, strNumber, time, callType, durationTime));
		}
		cursor.close();
		return list;
	}
	
	/**
	 * 获取通讯录列表
	 * 		<uses-permission android:name="android.permission.READ_CONTACTS" />
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static List<ContactBean> getContacts(Context context) {
		List<ContactBean> contactsList = new ArrayList<ContactBean>();

		Uri uri = Uri.parse("content://com.android.contacts/contacts");
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(uri, new String[] { "_id" }, null, null, null);
		while (cursor.moveToNext()) {
			ContactBean contacts = new ContactBean();
			int contactid = cursor.getInt(0);
			contacts.setContactid(contactid);
			uri = Uri.parse("content://com.android.contacts/contacts/" + contactid + "/data");
			Cursor datacursor = resolver.query(uri, new String[] { "mimetype", "data1", "data2" }, null, null, null);
			while (datacursor.moveToNext()) {
				String data = datacursor.getString(datacursor.getColumnIndex("data1"));
				String type = datacursor.getString(datacursor.getColumnIndex("mimetype"));
				if ("vnd.android.cursor.item/name".equals(type)) {// 姓名
					contacts.setName(data);
				} else if ("vnd.android.cursor.item/email_v2".equals(type)) {// email
					contacts.setEmail(data);
				} else if ("vnd.android.cursor.item/phone_v2".equals(type)) {// phone
					contacts.setPhone(data);
				}
			}
			contactsList.add(contacts);
			datacursor.close();
		}
		cursor.close();

		return contactsList;
	}

	/**
	 * 添加联系人(同一事务)
	 * 		<uses-permission android:name="android.permission.READ_CONTACTS" />
	 * 		<uses-permission android:name="android.permission.WRITE_CONTACTS" />
	 * @param context
	 * @param contact
	 * @return
	 */
	public static boolean addContacts(Context context, ContactBean contact) {
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		ContentProviderOperation op1 = ContentProviderOperation.newInsert(uri).withValue("account_name", null).build();
		operations.add(op1);

		uri = Uri.parse("content://com.android.contacts/data");
		ContentProviderOperation op2 = ContentProviderOperation.newInsert(uri).withValueBackReference("raw_contact_id", 0).withValue("mimetype", "vnd.android.cursor.item/name").withValue("data2", contact.getName()).build();
		operations.add(op2);

		ContentProviderOperation op3 = ContentProviderOperation.newInsert(uri).withValueBackReference("raw_contact_id", 0).withValue("mimetype", "vnd.android.cursor.item/phone_v2").withValue("data1", contact.getPhone()).withValue("data2", "2").build();
		operations.add(op3);

		ContentProviderOperation op4 = ContentProviderOperation.newInsert(uri).withValueBackReference("raw_contact_id", 0).withValue("mimetype", "vnd.android.cursor.item/email_v2").withValue("data1", contact.getEmail()).withValue("data2", "2").build();
		operations.add(op4);

		ContentResolver resolver = context.getContentResolver();
		try {
			resolver.applyBatch("com.android.contacts", operations);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 根据号码获取联系人的姓名
	 * 		<uses-permission android:name="android.permission.READ_CONTACTS" />
	 * @throws Exception
	 */
	public static String getContactNameByPhone(Context context, String phone) {
		String name = null;
		Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + phone);
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(uri, new String[] { "display_name" }, null, null, null);
		if (cursor.moveToFirst()) {
			name = cursor.getString(0);
		}
		cursor.close();
		return name;
	}

}
