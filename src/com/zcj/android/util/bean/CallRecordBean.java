package com.zcj.android.util.bean;

/**
 * 通话记录
 * 
 * @author ZCJ
 * @data 2014年1月24日
 */
public class CallRecordBean {
	
	private String linkman;// 联系人
	private String number;// 联系号码
	private String callDate;// 呼叫时间
	private String type;// 联系类型
	private String durction;// 通话时长

	public CallRecordBean() {
	}

	public CallRecordBean(String linkman, String number, String callDate,
			String type, String durction) {
		super();
		this.linkman = linkman;
		this.number = number;
		this.callDate = callDate;
		this.type = type;
		this.durction = durction;
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

	public String getCallDate() {
		return callDate;
	}

	public void setCallDate(String callDate) {
		this.callDate = callDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDurction() {
		return durction;
	}

	public void setDurction(String durction) {
		this.durction = durction;
	}

}
