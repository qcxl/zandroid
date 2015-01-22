package com.zcj.android.view.imageviewpager;

public class ImageViewPagerBean {

	private String imgurl;
	private String title;
	
	public ImageViewPagerBean() {
		super();
	}
	public ImageViewPagerBean(String imgurl, String title) {
		super();
		this.imgurl = imgurl;
		this.title = title;
	}
	public String getImgurl() {
		return imgurl;
	}
	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
}
