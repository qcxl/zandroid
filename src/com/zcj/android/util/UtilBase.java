package com.zcj.android.util;

import com.zcj.android.util.base.FilenameUtils;

public class UtilBase {

	/**
	 * 获取路径中的文件名，包含后缀
	 * @param filename
	 * @return
	 */
	public static String filenameUtils_getName(String filename) {
		return FilenameUtils.getName(filename);
	}
	
	/**
	 * 获取路径中的文件后缀，不包含"."
	 * @param filename
	 * @return
	 */
	public static String filenameUtils_getExtension(String filename) {
		return FilenameUtils.getExtension(filename);
	}
	
}
