package com.zcj.android.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 文件(夹)操作：读取、新建、删除、重命名、存储、文件(夹)大小
 * 
 * @author zouchongjin@sina.com
 * @data 2015年4月2日
 */
public class UtilFile {
	
	/** 列出目录下所有子目录,过滤掉以.开始的文件夹 */
	public static List<String> listPath(String root) {
		List<String> allDir = new ArrayList<String>();
		SecurityManager checker = new SecurityManager();
		File path = new File(root);
		checker.checkRead(root);
		if (path.isDirectory()) {
			for (File f : path.listFiles()) {
				if (f.isDirectory() && !f.getName().startsWith(".")) {// 过滤掉以.开始的文件夹
					allDir.add(f.getAbsolutePath());
				}
			}
		}
		return allDir;
	}
	
	/**
	 * 创建目录
	 * <br/>返回0代表失败，1代表成功，2代表已存在
	 */
	public static int createPath(String newPath) {
		File path = new File(newPath);
		if (path.exists()) {
			return 2;
		}
		if (path.mkdir()) {
			return 1;
		} else {
			return 0;
		}
	}
	
	/**
	 * 删除空目录
	 * <br/>返回 0代表成功 ,1 代表没有删除权限, 2代表不是空目录,3 代表未知错误
	 */
	public static int deleteBlankPath(String path) {
		File f = new File(path);
		if (!f.canWrite()) {
			return 1;
		}
		if (f.list() != null && f.list().length > 0) {
			return 2;
		}
		if (f.delete()) {
			return 0;
		}
		return 3;
	}
	
	/**
	 * 重命名
	 * 
	 * @param oldPath
	 * @param newPath
	 * @return
	 */
	public static boolean reNamePath(String oldPath, String newPath) {
		File f = new File(oldPath);
		return f.renameTo(new File(newPath));
	}
	
	/**
	 * 获取文件大小
	 * 
	 * @param filePath
	 * @return
	 */
	public static long getFileSize(String filePath) {
		long size = 0;
		File file = new File(filePath);
		if (file != null && file.exists()) {
			size = file.length();
		}
		return size;
	}
	
	/**
	 * 获取文件大小
	 * 
	 * @param fileS
	 * @return B/KB/MB/GB
	 */
	public static String getFileSizeText(String filePath) {
		long fileS = getFileSize(filePath);
		return getFileSizeText(fileS);
	}
	
	/**
	 * 转换文件大小
	 * 
	 * @param fileS
	 * @return B/KB/MB/GB
	 */
	public static String getFileSizeText(long fileS) {
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}
	
	/**
	 * 获取文件夹大小
	 * 
	 * @param dir
	 * @return
	 */
	public static long getFolderSize(File dir) {
		if (dir == null) {
			return 0;
		}
		if (!dir.isDirectory()) {
			return 0;
		}
		long dirSize = 0;
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				dirSize += file.length();
			} else if (file.isDirectory()) {
				dirSize += file.length();
				dirSize += getFolderSize(file); // 递归调用继续统计
			}
		}
		return dirSize;
	}

	/**
	 * 删除文件夹或文件
	 * 
	 * @param sPath
	 *            绝对路径
	 * @return
	 */
	public static boolean deleteFolder(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		if (!file.exists()) {
			return flag;
		} else {
			if (file.isFile()) {
				return deleteFile(sPath);
			} else {
				return deleteDirectory(sPath);
			}
		}
	}

	private static boolean deleteDirectory(String sPath) {
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	public static byte[] toBytes(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int ch;
		while ((ch = in.read()) != -1) {
			out.write(ch);
		}
		byte buffer[] = out.toByteArray();
		out.close();
		return buffer;
	}
	
	/**
	 * 保存内容到指定的文件路径下
	 * @param buffer
	 * @param filePath 包含文件名的完整路径
	 * @return
	 */
	public static boolean saveByByte(byte[] buffer, String filePath) {
		boolean writeSucc = false;
		File fileDir = new File(filePath.substring(0, filePath.lastIndexOf(File.separator)));
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(new File(filePath));
			out.write(buffer);
			writeSucc = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return writeSucc;
	}
	
	/**
	 * 获取网络图片的数据
	 * @param imgUrl
	 *            网络图片路径
	 * @return
	 * @throws IOException
	 */
	@Deprecated
	public static byte[] getImage(String imgUrl) throws IOException {
		URL url = new URL(imgUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(6000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == 200) {
			InputStream inStream = conn.getInputStream();
			return toBytes(inStream);
		}
		return null;
	}
	
	/**
	 * 获取网络图片的数据
	 * @param imgUrl 图片URL
	 * @return
	 * @throws IOException
	 */
	@Deprecated
	public static Bitmap getImageBitmap(String imgUrl) throws IOException {
		byte[] data = UtilFile.getImage(imgUrl);
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		return bitmap;
	}
	
}
