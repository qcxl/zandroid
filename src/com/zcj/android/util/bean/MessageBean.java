package com.zcj.android.util.bean;

import java.util.Date;

import android.telephony.SmsMessage;

import com.zcj.util.UtilDate;

/**
 * 短信
 * 
 * @author ZCJ
 * @data 2014年1月24日
 */
public class MessageBean {

	private String linkman;// 联系人
	private String number;// 联系号码
	private String content;// 短信内容
	private String date;// 短信时间
	private String type;// 短信类型

	/** SmsMessage 转 MessageBean */
	public static MessageBean getMessageBeanBySmsMessage(SmsMessage message) {
		if (message != null) {
			String content = message.getMessageBody();
			Date date = new Date(message.getTimestampMillis());
			String receiveTime = UtilDate.format(date);
			String senderNumber = message.getOriginatingAddress();
			return new MessageBean(null, senderNumber, content, receiveTime, "接收");
		}
		return null;
	}

	public MessageBean() {
	}

	public MessageBean(String linkman, String number, String content, String date, String type) {
		this.linkman = linkman;
		this.number = number;
		this.content = content;
		this.date = date;
		this.type = type;
	}

	public String getLinkman() {
		return linkman;
	}

	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
