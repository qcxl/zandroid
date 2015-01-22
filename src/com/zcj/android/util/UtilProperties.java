package com.zcj.android.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class UtilProperties {

	public static boolean contains(String filePath, String key) {
		Properties props = get(filePath);
		return props.containsKey(key);
	}

	public static String get(String filePath, String key) {
		Properties props = get(filePath);
		return (props != null) ? props.getProperty(key) : null;
	}

	private static Properties get(String filePath) {
		FileInputStream fis = null;
		Properties props = new Properties();
		try {
			fis = new FileInputStream(filePath);
			props.load(fis);
		} catch (Exception e) {
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return props;
	}

	public static void set(String filePath, String key, String value) {
		Properties props = get(filePath);
		props.setProperty(key, value);
		set(filePath, props);
	}

	public static void add(String filePath, Properties ps) {
		Properties props = get(filePath);
		props.putAll(ps);
		set(filePath, props);
	}

	private static void set(String filePath, Properties p) {
		FileOutputStream fos = null;
		try {
			File conf = new File(filePath);
			fos = new FileOutputStream(conf);
			p.store(fos, null);
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	public static void remove(String filePath, String... key) {
		Properties props = get(filePath);
		for (String k : key)
			props.remove(k);
		set(filePath, props);
	}

}
