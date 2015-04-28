package com.zcj.android.view.audiorecord;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zcj.android.R;
import com.zcj.android.util.AudioRecordUtils;
import com.zcj.util.UtilString;

/**
 * 仿微信发语音功能
 * 
 * @author ZCJ
 * @data 2014年2月24日
 */
public class AudioRecordDialog {
	
	// 录音的存放文件夹名字(/data/data/PACKAGE/cache/voiceTweet)
	private final static String folder = "voiceTweet";
	
	// 录制
	private final static byte RECORDER_STATE_RECARDING = 0x0;
	// 发布中
	private final static byte TWEET_PUBING = 0X02;
	// 取消发布
	private final static byte RECORDER_STATE_CANALE = 0x03;
	
	// 语音最短时间(秒)
	private final static int RECORDER_TIME_MINTIME = 1;
	// 语音最长时间(秒)
	private final static int RECORDER_TIME_MAXTIME = 60;
	
	// 是否正在录音中
	private boolean isRecording = false;
	// 是否超时
	private boolean IS_OVERTIME = false;
	
	private Activity activity;
	// 语音按钮
	private ImageView mAudio;
	private Button mRecorder;
	
	private LinearLayout mRecardStatus;// 录音状态下的布局
	private LinearLayout mRecardStatusShow;// 录音状态显示
	private Handler mHandler;

	// 录制语音时涉及操作的控件
	private FrameLayout mvFrame;
	private RelativeLayout mvAnimArea;// 录制声音大小布局
	private ImageView mvVolume;// 录音音量
	private RelativeLayout mvCancelArea;// 取消发布布局
	private LinearLayout mvTooShort;// 录音太短
	private TextView mvTimeMess;// 录音剩余时间提示
	private AudioRecordUtils recordUtils;// 录音工具类
	
	private String savePath;// 录音的路径
	private String fileName;// 文件名
	
	private DisplayMetrics dm;
	
	private BeginAllCallback bac;
	private AfterAllCallback aac;
	
	public AudioRecordDialog(Activity activity, ImageView mAudio, Button mRecorder, BeginAllCallback beginAllCallback, AfterAllCallback afterAllCallback) {
		super();
		
		// 获得手机屏幕的像素大小
		this.dm = new DisplayMetrics();  
		activity.getWindowManager().getDefaultDisplay().getMetrics(this.dm);
		
		this.bac = beginAllCallback;
		this.aac = afterAllCallback;
		
		this.activity = activity;
		this.mAudio = mAudio;
		this.mAudio.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			bac.doSomething();
    		}
    	});
		this.mRecorder = mRecorder;
		this.mRecorder.setOnTouchListener(recorderTouchListener);
		
    	// 录音状态下的相关布局
    	mRecardStatus = (LinearLayout) activity.findViewById(R.id.zandroid_audiorecord_pub_record_status);
    	mRecardStatusShow = (LinearLayout) activity.findViewById(R.id.zandroid_audiorecord_pub_record_status_show);
    	mHandler = new Handler();
    	
    	// 录制语音时涉及操作的控件
    	mvFrame = (FrameLayout) activity.findViewById(R.id.zandroid_audiorecord_pub_voice_rcd_hint_rcding);
    	mvAnimArea = (RelativeLayout) activity.findViewById(R.id.zandroid_audiorecord_pub_voice_rcd_hint_anim_area);// 录制声音大小布局
    	mvVolume = (ImageView) activity.findViewById(R.id.zandroid_audiorecord_pub_voice_rcd_hint_anim);// 录音状态动画
    	mvCancelArea = (RelativeLayout) activity.findViewById(R.id.zandroid_audiorecord_pub_voice_rcd_hint_cancel_area);// 取消发布布局
    	mvTooShort = (LinearLayout) activity.findViewById(R.id.zandroid_audiorecord_pub_voice_rcd_hint_tooshort);// 录音太短
    	mvTimeMess = (TextView) activity.findViewById(R.id.zandroid_audiorecord_pub_record_status_time_mes);// 录音剩余时间提示
    	recordUtils = new AudioRecordUtils();
	}
	
	/**
	 * 获得语音动弹保存的路径
	 * @return
	 */
	private String getRecorderPath() {
		String savePath = activity.getCacheDir().getAbsolutePath() + File.separator + folder + File.separator;
		File savedir = new File(savePath);
		if (!savedir.exists()) {
			savedir.mkdirs();
		}
		return savePath;
	}
	
	// 删除录音文件
	public void deleteVoiceFile() {
		File newPath = new File(savePath + fileName);
		newPath.delete();
	}
	
	// 录音计时器
	private Runnable mTimerTask = new Runnable() {
		int i = 0;
		public void run() {
			if (!isRecording) return;
			i++;
			if (i == RECORDER_TIME_MAXTIME) {
				IS_OVERTIME = true;
				i = 0;
				mvTimeMess.setVisibility(View.INVISIBLE);
				return;
			}
			if (i >= RECORDER_TIME_MAXTIME - 10) {
				if (i == RECORDER_TIME_MAXTIME - 10) {
					Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
					vibrator.vibrate(300);
				}
				mvTimeMess.setVisibility(View.VISIBLE);
				mvTimeMess.setText("录音时间还剩下" + (RECORDER_TIME_MAXTIME - i) + "秒");
			}
			mHandler.postDelayed(mTimerTask, 1000);
		}
	};
	
	// 录音音量状态展示
	private Runnable mPollTask = new Runnable() {
		public void run() {
			int amp = recordUtils.getAmplitudeSeven();
			updateDisplay(amp);
			mHandler.postDelayed(mPollTask, 300);
		}
	};
	
	// 开始录制语音动弹
	private void startRecorder(String name) {
		if (UtilString.isBlank(savePath))
			return;
		recordUtils.start(savePath + File.separator + name);
		mHandler.postDelayed(mPollTask, 300);
		mHandler.postDelayed(mTimerTask, 1000);
	}
	// 停止录音操作
	private void stopRecorder() {
		mRecardStatusShow.setVisibility(View.GONE);
		mHandler.removeCallbacks(mPollTask);
		mHandler.removeCallbacks(mTimerTask);
		recordUtils.stop();
		mvVolume.setImageResource(R.drawable.zandroid_audiorecord_amp1);
	}
	
	// 根据录制音量的大小定时更新状态图片
	private void updateDisplay(int signalEMA) {
		switch (signalEMA) {
		case 1:
			mvVolume.setImageResource(R.drawable.zandroid_audiorecord_amp1);
			break;
		case 2:
			mvVolume.setImageResource(R.drawable.zandroid_audiorecord_amp2);
			break;
		case 3:
			mvVolume.setImageResource(R.drawable.zandroid_audiorecord_amp3);
			break;
		case 4:
			mvVolume.setImageResource(R.drawable.zandroid_audiorecord_amp4);
			break;
		case 5:
			mvVolume.setImageResource(R.drawable.zandroid_audiorecord_amp5);
			break;
		case 6:
			mvVolume.setImageResource(R.drawable.zandroid_audiorecord_amp6);
			break;
		case 7:
			mvVolume.setImageResource(R.drawable.zandroid_audiorecord_amp7);
			break;
		default:
			break;
		}
	}
	
	/** 录音完毕放开后 */
	public interface AfterAllCallback {
		public void doSomething(String armFilepath);
	}
	
	/** 点击录音菜单 */
	public interface BeginAllCallback {
		public void doSomething();
	}
	
	// 语音录制按钮触摸事件
	private View.OnTouchListener recorderTouchListener = new View.OnTouchListener() {
		long startVoiceT = 0;// 开始时间
		long endVoiceT = 0;// 结束世间
		int startY = 0;// 开始的Y
		byte state = RECORDER_STATE_RECARDING;
		public boolean onTouch(View v, MotionEvent event) {
			// 超时
			if (IS_OVERTIME) {
				mvTimeMess.setVisibility(View.INVISIBLE);
				stopRecorder();
				mRecorder.setBackgroundResource(R.color.zandroid_audiorecord_weight_bar_buttonup);
				mRecorder.setText("按住  说话");
				// 状态为取消
	        	if (state == RECORDER_STATE_CANALE || state == TWEET_PUBING) {
	        		deleteVoiceFile();
	        		if (state == RECORDER_STATE_CANALE)
	        			IS_OVERTIME = false;
	        		return false;
	        	}
				if (state != TWEET_PUBING) {
					state = TWEET_PUBING;
					IS_OVERTIME = false;
					aac.doSomething(savePath + fileName);
				}
				return false;
			}
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				isRecording = true;
				savePath = getRecorderPath();
				MediaPlayer md = MediaPlayer.create(activity, R.raw.notificationsound);
				md.start();
				// 提示音播放完开始录音
				md.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					public void onCompletion(MediaPlayer mp) {
						startRecorder(fileName);
					}
				});
				IS_OVERTIME = false;
				mRecorder.setBackgroundResource(R.color.zandroid_audiorecord_weight_bar_buttondown);
				mRecorder.setText("松开  结束");
				// 按下时记录录音文件名
				String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
				fileName = "_" + timeStamp + ".amr";// 语音动弹命名
				startY = (int) event.getY();
				startVoiceT = System.currentTimeMillis();
				// 隐藏软键盘
				mRecardStatusShow.setVisibility(View.VISIBLE);
				break;
			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY();
				if (Math.abs(startY - tempY) > dm.heightPixels / 3) {
					// 取消
					state = RECORDER_STATE_CANALE;
					mvAnimArea.setVisibility(View.GONE);
					mvCancelArea.setVisibility(View.VISIBLE);
				} else {
					// 录音
					state = RECORDER_STATE_RECARDING;
					mvAnimArea.setVisibility(View.VISIBLE);
					mvCancelArea.setVisibility(View.GONE);
				}
				break;
	        case MotionEvent.ACTION_UP:
	        	isRecording = false;
	        	mRecorder.setBackgroundResource(R.color.zandroid_audiorecord_weight_bar_buttonup);
	        	mRecorder.setText("按住  说话");
	        	endVoiceT = System.currentTimeMillis();
	        	long voiceT = endVoiceT - startVoiceT;
	        	// 停止录音
	        	stopRecorder();
	        	// 录音小于最小时间
	        	if (voiceT < RECORDER_TIME_MINTIME * 1000 || state == RECORDER_STATE_CANALE) {
	        		deleteVoiceFile();
	        		if (voiceT < RECORDER_TIME_MINTIME * 1000) {
	        			mvFrame.setVisibility(View.GONE);
		    			mRecardStatusShow.setVisibility(View.VISIBLE);
		        		mvTooShort.setVisibility(View.VISIBLE);
		        		mHandler.postDelayed(new Runnable() {
		    				public void run() {
		    	        		mRecardStatusShow.setVisibility(View.GONE);
		    	        		mvTooShort.setVisibility(View.GONE);
		    	        		mvFrame.setVisibility(View.VISIBLE);
		    				}
		    			}, 1000);
	        		}
	        		if (state == RECORDER_STATE_CANALE)
	        			mvTimeMess.setVisibility(View.INVISIBLE);
	        		return false;
	        	}
	        	IS_OVERTIME = false;
	        	aac.doSomething(savePath + fileName);
	        	break;
			default:
				break;
			}
			return false;
		}
	};
	
	/**
	 * 显示或隐藏录音功能
	 * @param goneView
	 * 			录音时隐藏的View：new View[]{mForm, mFace, mPick, mAtme, mSoftware, mPublish}
	 */
	public void showOrHideRecareder(View[] goneView) {
		mvTooShort.setVisibility(View.GONE);
		if (mAudio.getTag() == null) {// 显示录制操作选项
			for (View v : goneView) {
				v.setVisibility(View.GONE);
			}
			mAudio.setTag(1);
	    	mAudio.setImageResource(R.drawable.zandroid_audiorecord_bar_keyboard);
	    	mRecorder.setVisibility(View.VISIBLE);
	    	mRecardStatus.setVisibility(View.VISIBLE);
		} else {// 隐藏录制操作选项
			for (View v : goneView) {
				v.setVisibility(View.VISIBLE);
			}
			mAudio.setTag(null);
			mAudio.setImageResource(R.drawable.zandroid_audiorecord_bar_audio);
			mRecorder.setVisibility(View.GONE);
			mRecardStatus.setVisibility(View.GONE);
			this.invisibleMvTimeMess();
		}
	}
	
	public void invisibleMvTimeMess() {
		mvTimeMess.setVisibility(View.INVISIBLE);
	}

	
}
