package com.zcj.android.view.imageviewpager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.zcj.android.R;

public class ImageViewPagerUtil {

	private Activity context;

	private ViewPager viewPager;
	private String[] imgurls;
	private String[] titles;
	private List<View> dots;
	private TextView tv_title;
	private BitmapUtils bitmapUtils;
	private LinearLayout pointlayout;

	public ImageViewPagerUtil(Activity context, List<ImageViewPagerBean> dataList) {
		this.context = context;

		bitmapUtils = new BitmapUtils(context);

		imgurls = new String[dataList.size()];
		titles = new String[dataList.size()];
		dots = new ArrayList<View>();
		pointlayout = (LinearLayout) context.findViewById(R.id.imageviewpage_pointlayout);
		for (int i = 0; i < dataList.size(); i++) {
			imgurls[i] = dataList.get(i).getImgurl();
			titles[i] = dataList.get(i).getTitle();
			View dot4 = new View(context);
			if (i == 0) {
				dot4.setBackgroundResource(R.drawable.zandroid_imageviewpager_dot_focused);
			} else {
				dot4.setBackgroundResource(R.drawable.zandroid_imageviewpager_dot_normal);
			}
			dot4.setLayoutParams(new ViewGroup.LayoutParams(10, 10));
			dots.add(dot4);
			pointlayout.addView(dot4);
		}

		tv_title = (TextView) context.findViewById(R.id.imageviewpage_title);
		tv_title.setText(titles[0]);

		viewPager = (ViewPager) context.findViewById(R.id.imageviewpage_viewpager);
		viewPager.setAdapter(new MyAdapter());
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			private int oldPosition = 0;

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int position) {
				tv_title.setText(titles[position]);
				dots.get(oldPosition).setBackgroundResource(R.drawable.zandroid_imageviewpager_dot_normal);
				dots.get(position).setBackgroundResource(R.drawable.zandroid_imageviewpager_dot_focused);
				oldPosition = position;
			}
		});
	}

	private class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return titles.length;
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			ImageView imageView = new ImageView(context);
			bitmapUtils.display(imageView, imgurls[arg1]);
			((ViewPager) arg0).addView(imageView);
			return imageView;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}
	}
}
