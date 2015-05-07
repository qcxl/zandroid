package com.zcj.android.web;

public abstract class HttpCallback {

	public abstract void success(String dataJsonString);
	
	public void error() {
    }
	
}
