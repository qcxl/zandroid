package com.zcj.android.view.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.zcj.android.R;
import com.zcj.android.util.UtilBase;

public class DownloadUtils {

	private Activity mContext;
	private String downloadUrl;// 下载地址
	private String savePath;// 保存路径
	
	private Integer newApkVersion;

	public DownloadUtils(Activity mContext, String downloadUrl, String savePath) {
		super();
		this.mContext = mContext;
		this.downloadUrl = downloadUrl;
		this.savePath = savePath;
	}

	private static final int DOWNLOAD = 1;
	private static final int DOWNLOAD_FINISH = 2;
	private ProgressBar mProgress;
	private Dialog mDownloadDialog;
	private boolean cancelUpdate = false;
	private int progress;
	/**
	 * 检测更新
	 * @param newApkVersion 服务器上的最新版本号
	 * @param showNoUpdateMes 是否显示已经是最新版本的提示框
	 */
	public void checkUpdate(Integer newApkVersion, final boolean showNoUpdateMsg) {
		this.newApkVersion = newApkVersion;
		if (isUpdate()) {
			showNoticeDialog();
		} else {
			if (showNoUpdateMsg) {
				Toast.makeText(mContext, "您当前已经是最新版本", Toast.LENGTH_LONG).show();
			}
		}
	}
	private void showNoticeDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("软件更新");
		builder.setMessage("检测到有新版本，是否更新");
		builder.setPositiveButton("更新", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				downloading();
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		Dialog noticeDialog = builder.create();
		noticeDialog.show();
	}
	private boolean isUpdate() {
		int versionCode = getVersionCode(mContext);
		if (newApkVersion > versionCode) {
			return true;
		}
		return false;
	}
	private int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			versionCode = context.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}
	public void downloading() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("正在下载...");
		final LayoutInflater inflate = LayoutInflater.from(mContext);
		View v = inflate.inflate(R.layout.zandroid_download_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.zandroid_download_progress);
		builder.setView(v);
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				cancelUpdate = true;
			}
		});
		mDownloadDialog = builder.create();
		mDownloadDialog.show();
		downloadFile();
	}
	private void downloadFile() {
		new downloadFileThread().start();
	}
	private class downloadFileThread extends Thread {
		@Override
		public void run() {
			try {
				URL url = new URL(downloadUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.connect();
				
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();
				File file = new File(savePath);
				if (!file.exists()) {
					file.mkdir();
				}
				File apkFile = new File(savePath, UtilBase.filenameUtils_getName(downloadUrl));
				FileOutputStream fos = new FileOutputStream(apkFile);
				int count = 0;
				byte buf[] = new byte[1024];
				do {
					int numread = is.read(buf);
					count += numread;
					progress = (int) (((float)count/length)*100);
					mHandler.sendEmptyMessage(DOWNLOAD);
					if (numread <= 0) {
						mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
						break;
					}
					fos.write(buf, 0, numread);
				} while (!cancelUpdate);
				fos.close();
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
				if (mDownloadDialog.isShowing()) {
					mDownloadDialog.dismiss();
				}
			}
		}
	}
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWNLOAD:
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				if (mDownloadDialog.isShowing()) {
					mDownloadDialog.dismiss();
				}
				afertDownload();
				break;
			default:
				break;
			}
		};
	};
	private void afertDownload() {
		String suff = UtilBase.filenameUtils_getExtension(downloadUrl).trim().toLowerCase();
		String type;
		File f = new File(savePath, UtilBase.filenameUtils_getName(downloadUrl));
		if (!f.exists()) {
			return;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if ("apk".equals(suff)) {
			type = "application/vnd.android.package-archive";
		} else {
			type = "application/msword";
		}
		intent.setDataAndType(Uri.fromFile(f), type);
		mContext.startActivity(intent);
	}
}
