package com.zcj.android.util;

import java.io.IOException;

import android.media.MediaRecorder;

/**
 * 录音工具包
 * 
 * @author FireAnt
 * @version 1.0
 * @created 2013-01-17
 */
public class AudioRecordUtils {

	static final private double EMA_FILTER = 0.6;

	private MediaRecorder mRecorder = null;
	private double mEMA = 0.0;

	public void start(String savePath) {

		if (mRecorder == null) {
			mRecorder = new MediaRecorder();
			// 指定音频来源（麦克风）
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			// 指定音频输出格式
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
			// 指定音频编码方式
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

			// 指定录制音频输出信息的文件
			mRecorder.setOutputFile(savePath);
			try {
				mRecorder.prepare();
				mRecorder.start();
				mEMA = 0.0;
			} catch (IllegalStateException e) {
				System.out.print(e.getMessage());
			} catch (IOException e) {
				System.out.print(e.getMessage());
			}
		}
	}

	public void stop() {
		if (mRecorder != null) {
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
		}
	}

	public void pause() {
		if (mRecorder != null) {
			mRecorder.stop();
		}
	}

	public void start() {
		if (mRecorder != null) {
			mRecorder.start();
		}
	}
	
	/** 取得音量大小（1-7） */
	public int getAmplitudeSeven() {
		int result = 0;
		switch ((int) getAmplitude()) {
		case 0:
			result = 1;
			break;
		case 1:
			result = 1;
			break;
		case 2:
			result = 2;
			break;
		case 3:
			result = 2;
			break;
		case 4:
			result = 3;
			break;
		case 5:
			result = 3;
			break;
		case 6:
			result = 4;
			break;
		case 7:
			result = 4;
			break;
		case 8:
			result = 5;
			break;
		case 9:
			result = 5;
			break;
		case 10:
			result = 6;
			break;
		case 11:
			result = 6;
			break;
		default:
			result = 7;
			break;
		}
		return result;
	}

	public double getAmplitude() {
		if (mRecorder != null)
			// 获取在前一次调用此方法之后录音中出现的最大振幅
			return (mRecorder.getMaxAmplitude() / 2700.0);
		else
			return 0;
	}

	public double getAmplitudeEMA() {
		double amp = getAmplitude();
		mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
		return mEMA;
	}
}
