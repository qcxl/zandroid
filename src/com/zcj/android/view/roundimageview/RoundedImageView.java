package com.zcj.android.view.roundimageview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.lidroid.xutils.bitmap.core.AsyncDrawable;
import com.zcj.android.util.UtilImage;

/**
 * 有圆角的图片
 * 
 * @author zouchongjin@sina.com
 * @data 2014年12月10日
 */
@SuppressLint("DrawAllocation")
public class RoundedImageView extends ImageView {

	public RoundedImageView(Context context) {
		super(context);
	}

	public RoundedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		Drawable drawable = getDrawable();

		if (drawable == null) {
			return;
		}

		if (getWidth() == 0 || getHeight() == 0) {
			return;
		}
		Bitmap b = null;
		if (drawable instanceof BitmapDrawable) {
			b = ((BitmapDrawable) drawable).getBitmap();
		} else if (drawable instanceof AsyncDrawable) {
			b = Bitmap.createBitmap(getWidth(), getHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
					: Bitmap.Config.RGB_565);
			Canvas canvas1 = new Canvas(b);
			drawable.setBounds(0, 0, getWidth(), getHeight());
			drawable.draw(canvas1);
		}
		b = UtilImage.zoomBitmap(b, getWidth(), getHeight());
		Bitmap roundBitmap = UtilImage.getRoundedCornerBitmap(b, 5);
		canvas.drawBitmap(roundBitmap, 0, 0, null);
	}

}