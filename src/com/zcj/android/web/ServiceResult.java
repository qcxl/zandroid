package com.zcj.android.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ServiceResult {

	private int s;
	private Object d;

	private static final int S_SUCCESS = 1;// 成功标识

	public static final Gson GSON_D = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
	public static final Gson GSON_DT = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

	/** 此返回结果是成功的 */
	public boolean success() {
		if (S_SUCCESS == this.getS()) {
			return true;
		} else {
			return false;
		}
	}

	public int getS() {
		return s;
	}

	public void setS(int s) {
		this.s = s;
	}

	public Object getD() {
		return d;
	}

	public void setD(Object d) {
		this.d = d;
	}
}
