package com.zcj.android.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

/**
 * SOAP
 * 
 * @author zouchongjin@sina.com
 * @data 2014年9月11日
 */
public class UtilTelAddress {
	
	private static final String XMLPATH = "com/zcj/android/util/xml/UtilTelAddress.xml";
	
	/**
	 * 获取手机号归属地
	 * 
	 * @param mobile
	 *            手机号(可以只传前7位)
	 * @return
	 * @throws Exception
	 */
	public static String getAddress(String mobile) {
		try {
			String soap = readSoap().replaceAll("\\$mobile", mobile);
			byte[] entity = soap.getBytes();

			String path = "http://webservice.webxml.com.cn/WebServices/MobileCodeWS.asmx";
			HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
			conn.setRequestProperty("Content-Length", String.valueOf(entity.length));
			conn.getOutputStream().write(entity);
			if (conn.getResponseCode() == 200) {
				return parseSOAP(conn.getInputStream());
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	private static String parseSOAP(InputStream xml) throws Exception {
		XmlPullParser pullParser = Xml.newPullParser();
		pullParser.setInput(xml, "UTF-8");
		int event = pullParser.getEventType();
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_TAG:
				if ("getMobileCodeInfoResult".equals(pullParser.getName())) {
					return pullParser.nextText();
				}
				break;
			}
			event = pullParser.next();
		}
		return null;
	}

	private static String readSoap() throws Exception {
		InputStream inStream = UtilTelAddress.class.getClassLoader().getResourceAsStream(XMLPATH);
		byte[] data = read(inStream);
		return new String(data);
	}

	private static byte[] read(InputStream inStream) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, len);
		}
		inStream.close();
		return outputStream.toByteArray();
	}
}
