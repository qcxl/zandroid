package com.zcj.android.util.bean;

/**
 * 联系人
 * 
 * @author ZCJ
 * @data 2014年1月24日
 */
public class ContactBean {
	
	private Integer contactid;// ID
	private String name;// 姓名
	private String phone;// 电话号码
	private String email;

	public ContactBean() {
	}

	public ContactBean(String name, String phone, String email) {
		this.name = name;
		this.phone = phone;
		this.email = email;
	}

	public Integer getContactid() {
		return contactid;
	}

	public void setContactid(Integer contactid) {
		this.contactid = contactid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
