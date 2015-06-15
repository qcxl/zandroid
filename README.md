
# com.lidroid.xutils - 开源框架XUtils #
	
	https://github.com/wyouflf/xUtils
	Version:2.6.14
	Document:http://xutilsapi.oschina.mopaas.com/
	
	>>ViewUtils
		ViewUtils.inject(this);
		@ContentView(R.layout.layout_housenew)
		@ViewInject(R.id.main_head_title)
		@OnClick(R.id.main_head_back)
	>>BitmapUtils
		BitmapUtils bitmapUtils = new BitmapUtils(context);
		bitmapUtils.display(testImageView, "http://bbs.lidroid.com/static/image/common/logo.png");

# BaseActivity - 注册双击全屏 #
	
	onCreate方法中添加：startAllowFullScreen(mHeader);// 传入需要隐藏的组件

# download - 下载&更新 #

	DownloadUtils dh = new DownloadUtils(this, "http://gdown.baidu.com/data/wisegame/cbe0fb2ba1d1dc55/baidushurufa.apk", Environment.getExternalStorageDirectory() + "/download");
	dh.checkUpdate(2, false);
	
# webview&imagezoom - 文章内容页（图片点击打开可保存、网页链接可点击打开） #

	<activity android:name="com.zcj.android.view.imagezoom.ImageZoomDialog"></activity>
	
	// 在指定的webView中显示指定的HTML内容，同时处理HTML的样式和支持图片的点击保存（主要用于显示文章内容）。
	UtilWebView.showArticleWebView(this, mWebView, newsDetail.getContext(), Environment.getExternalStorageDirectory()+ File.separator+ "TEST"+ File.separator);
	
# webviewshell - WebView壳 #

	详见palc项目或AndroidHelp。
	
# imageviewpager - 图片左右滑动 #

	android-support-v4.jar
	
	<include layout="@layout/zandroid_imageviewpager_framelayout"/>
	
	new ImageViewPagerUtil(HouseNewDetailActivity.this, dataList);
	
# screenshot - 截屏功能 #
	
	UtilScreenShot.saveScreenShot(this, savePath);// 直接保存截图
	或
	UtilScreenShot.addScreenShot(this, new OnScreenShotListener() {// 返回截图bitmap
		@Override
		public void onComplete(Bitmap bm) {}
	});
	
# audiorecord - 录音功能 #

	<!-- 录音/键盘的切换按钮 -->
	<ImageView
        android:id="@+id/tweet_pub_footbar_audio"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@drawable/zandroid_audiorecord_bar_audio" />
    <!-- 录音模式下的录音按钮 -->
    <Button
        android:id="@+id/tweet_pub_footbar_recarder"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:visibility="gone"
        android:text="按住  说话" />
    <!-- 录音模式下的主界面 -->    
    <include
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        layout="@layout/zandroid_audiorecord_status" />
    <!-- Activity -->
    private AudioRecordDialog ard;
    ard = new AudioRecordDialog(this, (ImageView) findViewById(R.id.tweet_pub_footbar_audio),
		(Button) findViewById(R.id.tweet_pub_footbar_recarder), new BeginAllCallback() {
			@Override
			public void doSomething() {
				showOrHideRecarder();
			}
		}, new AfterAllCallback() {
			@Override
			public void doSomething(String armFilepath) {
				Toast.makeText(Main.this, armFilepath, Toast.LENGTH_SHORT).show();
			}
		}
	);
	private void showOrHideRecarder() {
		ard.showOrHideRecareder(new View[]{});
	}
	
# pathchoose - 路径选择弹出窗 #

	UtilPathChoose.showFilePathDialog(Setting.this,new ChooseCompleteListener() {
		public void onComplete(String finalPath) {
			Toast.makeText(Main.this, finalPath+File.separator, Toast.LENGTH_SHORT).show();
		}
	});
	
# loadingdialog - 加载对话框组件#

	private LoadingDialog loading;
	
	loading = new LoadingDialog(this);
	loading.setLoadText("正在上传头像···");
	loading.show();
	
	loading.hide();
	
# circleimageview - 图片圆形显示 #
	
	<!-- Layout.xml -->
	<?xml version="1.0" encoding="utf-8"?>
	<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
	    xmlns:app="http://schemas.android.com/apk/res-auto"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent"
	    android:orientation="vertical"
	    android:background="@color/white">
	    <RelativeLayout
	        android:layout_width="match_parent"
	        android:layout_height="0dp"
	        android:layout_weight="1"
	        android:padding="16dp"
	        android:background="#FF222222">
	        <com.zcj.android.view.circleimageview.CircleImageView
	            android:id="@+id/dme_headimage"
	            android:layout_width="160dp"
	            android:layout_height="160dp"
	            android:layout_centerInParent="true"
	            android:src="@drawable/ic_launcher"
	            app:border_width="4dp"
	            app:border_color="#FFEEEEEE" />
	    </RelativeLayout>
	</LinearLayout>
		
	<!-- MyActivity.java -->
	private CircleImageView headImage;
	headImage.setImageBitmap(UtilImage.getBitmapByFilePath(cutAfterPath, null));// 修改图片
	
# roundimageview - 有圆角的图片 #

	用com.zcj.android.view.roundimageview.RoundedImageView 替代 ImageView 即可。
	
# viewbadger - 未读红点 #
	
	BadgeView badge = new BadgeView(this, myView);
	badge.setText("1");
	// badge.setTextColor(Color.BLUE);// 内容的颜色
	// badge.setTextSize(12); 内容文字的大小
	// badge.setBadgeBackgroundColor(Color.YELLOW);// 自定义背景颜色
	// badge.setBackgroundResource(R.drawable.badge_ifaux);// 自定义背景图片
	// badge.setBadgePosition(BadgeView.POSITION_CENTER);// 红点位置居中
	// badge.setBadgeMargin(15, 10);
	// badge.toggle();// 点击后需要调动的方法
	// badge.toggle(true);// 点击后需要调动的方法
	
	// if (badge8.isShown()) {
	//		badge.increment(1);
	// } else {
	//		badge.show();
	// }
	badge.show();
	badge.hide();
	
	<!-- 列表中MyAdapter -->
	static class ViewHolder {
        TextView text;
        BadgeView badge;
    }
    holder = new ViewHolder();
    holder.text = (TextView) convertView.findViewById(android.R.id.text1);
    holder.badge = new BadgeView(mContext, holder.text);
	
	
Color.parseColor("#A4C639")	
	
# numberprogressbar - 水平进度条 #
	
	numberProgressBar.incrementProgressBy(1);
	numberProgressBar.setProgress(0);
	
	<com.zcj.android.view.numberprogressbar.NumberProgressBar
        android:id="@+id/numberbar1"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:padding="20dp"
		custom:max="100"
		custom:progress_unreached_color="#CCCCCC"
		custom:progress_reached_color="#3498DB"
		custom:progress_text_size="10sp"
		custom:progress_text_color="#3498DB"
		custom:progress_reached_bar_height="10dp"
		custom:progress_unreached_bar_height="9dp"
        custom:progress="20"/>
	
# pulltorefreshbyhandmark - 下拉刷新上拉加载 #
	
	https://github.com/chrisbanes/Android-PullToRefresh

	<com.zcj.android.view.pulltorefreshbyhandmark.PullToRefreshListView
        android:id="@+id/pull_refresh_list"
        android:layout_width="fill_parent"
        android:layout_height="0dp"
       	android:layout_weight="1"
        android:cacheColorHint="@color/black"
        android:divider="@color/white"
        android:dividerHeight="0dp"
        android:fadingEdge="none"
        android:fastScrollEnabled="false"
        android:footerDividersEnabled="false"
        android:headerDividersEnabled="false"
        android:smoothScrollbar="true"/>

# com.google.zxing - 二维码生成和识别 #
 
	https://github.com/zxing/zxing
	
	com.google.zxing					core核心包(在原版的基础上修改了编码,详见com.google.zxing.qrcode.encoder.Encoder.java)
	com.google.zxing.client.android		android客户端包(网络修改简化版,包含资源文件和Activity)
	
	Demo(详见AndroidHelp)
		1、BarCodeTestActivity.java
		2、layout_main.xml
		3、Manifest.xml
			<uses-permission android:name="android.permission.VIBRATE" />
		    <uses-permission android:name="android.permission.CAMERA" />
		    <uses-feature android:name="android.hardware.camera" />
		    <uses-feature android:name="android.hardware.camera.autofocus" />
		    
		    <activity android:name="com.android.zouchongjin.demo.zxing.BarCodeTestActivity"></activity>
	        <activity
	            android:configChanges="orientation|keyboardHidden"
	            android:name="com.google.zxing.client.android.activity.CaptureActivity"
	            android:screenOrientation="portrait"
	            android:theme="@android:style/Theme.NoTitleBar.Fullscreen"
	            android:windowSoftInputMode="stateAlwaysHidden" >
	        </activity>
	
	