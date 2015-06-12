package com.zcj.android.util.bean;

/**
 * 手机浏览器信息
 * 
 * @author zouchongjin@sina.com
 * @data 2015年6月12日
 */
public class InternetBean {

	public static final String TYPE_HOSTORY = "历史记录";
	public static final String TYPE_BOOKMARKS = "书签";

	private String title;
	private String url;
	private String date;
	private String type;

	public InternetBean() {
		super();
	}

	public InternetBean(String title, String url, String date, String type) {
		super();
		this.title = title;
		this.url = url;
		this.date = date;
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
