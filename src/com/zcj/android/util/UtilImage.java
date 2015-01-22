package com.zcj.android.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

public class UtilImage {

	public static Bitmap getBitmapByResourceId(Context context, int id) {
		return BitmapFactory.decodeResource(context.getResources(), id);
	}

	public static Bitmap getBitmapBydrawable(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}

	@SuppressWarnings("deprecation")
	public static Drawable getDrawableByBitmap(Bitmap bitmap) {
		return new BitmapDrawable(bitmap);
	}

	public static byte[] getBytesByBitmap(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	public static Bitmap getBitmapByBytes(byte[] bytes) {
		if (bytes.length != 0) {
			return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		} else {
			return null;
		}
	}

	@SuppressWarnings("deprecation")
	public static Drawable getDrawableByFilePath(String filePath) {
		return new BitmapDrawable(getBitmapByFilePath(filePath, null));
	}

	public static Bitmap getBitmapByFilePath(String filePath, BitmapFactory.Options opts) {
		FileInputStream fis = null;
		Bitmap bitmap = null;
		try {
			File file = new File(filePath);
			fis = new FileInputStream(file);
			if (null == opts) {
				bitmap = BitmapFactory.decodeStream(fis);
			} else {
				bitmap = BitmapFactory.decodeStream(fis, null, opts);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return bitmap;
	}

	/**
	 * 写图片文件到指定的文件路径下
	 * 
	 * @param bitmap
	 * @param filePath
	 *            包含文件名的完整路径
	 * @return
	 * @throws IOException
	 */
	public static void saveImage(Bitmap bitmap, String filePath) throws IOException {
		if (bitmap != null) {
			File file = new File(filePath.substring(0, filePath.lastIndexOf(File.separator)));
			if (!file.exists()) {
				file.mkdirs();
			}
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
			bitmap.compress(CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
		}
	}

	/** 根据URI获取图片的地址 */
	public static String getFilePathByUri(Activity activity, Uri uri) {
		String thePath = getAbsolutePathFromNoStandardUri(uri);
		if (UtilString.isBlank(thePath)) {
			return getAbsoluteImagePath(activity, uri);
		} else {
			return thePath;
		}
	}

	@SuppressWarnings("deprecation")
	private static String getAbsoluteImagePath(Activity context, Uri uri) {
		String imagePath = "";
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.managedQuery(uri, proj, null, null, null);
		if (cursor != null) {
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			if (cursor.getCount() > 0 && cursor.moveToFirst()) {
				imagePath = new String(cursor.getString(column_index));
			}
			try {
				if (Integer.parseInt(Build.VERSION.SDK) < 14) {
					cursor.close();
				}
			} catch (Exception e) {
			}
		}
		return imagePath;
	}

	private static String getAbsolutePathFromNoStandardUri(Uri mUri) {
		String filePath = null;

		String mUriString = mUri.toString();
		mUriString = Uri.decode(mUriString);

		String pre1 = "file:///sdcard" + File.separator;
		String pre2 = "file:///mnt/sdcard" + File.separator;

		if (mUriString.startsWith(pre1)) {
			filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + mUriString.substring(pre1.length());
		} else if (mUriString.startsWith(pre2)) {
			filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + mUriString.substring(pre2.length());
		}
		return filePath;
	}

	/**
	 * 放大缩小图片
	 * 
	 * @param bitmap
	 *            原始图片
	 * @param w
	 *            宽度
	 * @param h
	 *            高度
	 * @return 改变宽度和高度后的图片
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		Bitmap newbmp = null;
		if (bitmap != null) {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			Matrix matrix = new Matrix();
			float scaleWidht = ((float) w / width);
			float scaleHeight = ((float) h / height);
			matrix.postScale(scaleWidht, scaleHeight);
			newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		}
		return newbmp;
	}

	/**
	 * 放大缩小图片
	 * 
	 * @param filePath
	 *            原始图片
	 * @param w
	 *            宽度
	 * @param h
	 *            高度
	 * @return 改变宽度和高度后的图片
	 */
	public static Bitmap zoomImage(String filePath, int w, int h) {
		Bitmap bitmap = getBitmapByFilePath(filePath, null);
		return UtilImage.zoomBitmap(bitmap, w, h);
	}

	/**
	 * 压缩图片
	 * 
	 * @param filePath
	 *            图片的文件路径
	 * @param maxWidthOrHeight
	 *            最大的宽度或高度
	 * @return
	 */
	public static Bitmap zoomBitmap(String filePath, int maxWidthOrHeight) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 1;
		// 原始图片bitmap
		Bitmap cur_bitmap = getBitmapByFilePath(filePath, opts);

		if (cur_bitmap == null)
			return null;

		// 原始图片的高宽
		int[] cur_img_size = new int[] { cur_bitmap.getWidth(), cur_bitmap.getHeight() };
		// 计算原始图片缩放后的宽高
		int[] new_img_size = scaleImageSize(cur_img_size, maxWidthOrHeight);
		return zoomBitmap(cur_bitmap, new_img_size[0], new_img_size[1]);
	}

	/**
	 * 根据手机屏幕缩放图片，用于显示到手机上
	 * 
	 * @param context
	 * @param bitmap
	 * @return
	 */
	public static Bitmap zoomBitMapForWindow(Activity context, Bitmap bitmap) {
		int rWidth = UtilAndroid.getWindowWidth(context);
		int width = bitmap.getWidth();
		float zoomScale;
		if (width >= rWidth)
			zoomScale = ((float) rWidth) / width;
		else
			zoomScale = 1.0f;
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 缩放图片动作
		matrix.postScale(zoomScale, zoomScale);
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	/** 根据图片的路径获取图片的缩略图 */
	public static Bitmap getThumbnail(Activity context, String filePath, int width, int height) {
		Bitmap bitmap = null;
		if (UtilString.isNotBlank(filePath)) {
			bitmap = loadImgThumbnail(context, UtilBase.filenameUtils_getName(filePath));
			if (bitmap == null) {
				bitmap = getBitmapByFilePath(filePath, null);
				bitmap = zoomBitmap(bitmap, width, height);
			}
		}
		return bitmap;
	}

	/**
	 * 获取图片缩略图 只有Android2.1以上版本支持
	 * 
	 * @param imgFileName
	 *            图片文件的文件名（带后缀）
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private static Bitmap loadImgThumbnail(Activity context, String imgFileName) {
		Bitmap bitmap = null;

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ECLAIR_MR1) {// 只有Android2.1以上版本支持
			String[] proj = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME };

			Cursor cursor = context.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj, MediaStore.Images.Media.DISPLAY_NAME
					+ "='" + imgFileName + "'", null, null);

			if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
				ContentResolver crThumb = context.getContentResolver();
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;
				bitmap = getThumbnail(crThumb, cursor.getInt(0), MediaStore.Images.Thumbnails.MICRO_KIND, options);
			}
		}

		return bitmap;
	}

	@TargetApi(7)
	private static Bitmap getThumbnail(ContentResolver cr, long origId, int kind, Options options) {
		return MediaStore.Images.Thumbnails.getThumbnail(cr, origId, kind, options);
	}

	/**
	 * 计算缩放图片的宽高
	 * 
	 * @param img_size
	 *            包含宽度和高度的数组
	 * @param square_size
	 *            最大宽度或高度
	 * @return
	 */
	public static int[] scaleImageSize(int[] img_size, int square_size) {
		if (img_size[0] <= square_size && img_size[1] <= square_size)
			return img_size;
		double ratio = square_size / (double) Math.max(img_size[0], img_size[1]);
		return new int[] { (int) (img_size[0] * ratio), (int) (img_size[1] * ratio) };
	}

	/**
	 * 获得圆角图片的方法
	 * 
	 * @param bitmap
	 * @param roundPx
	 *            一般设成14
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 获得带倒影的图片方法
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2, width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);

		return bitmapWithReflection;
	}

	/** 获取当前屏幕截图，包含状态栏 */
	public static Bitmap snapShotWithStatusBar(Activity activity) {
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		int width = UtilAndroid.getScreenWidth(activity);
		int height = UtilAndroid.getScreenHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
		view.destroyDrawingCache();
		return bp;
	}

	/** 获取当前屏幕截图，不包含状态栏 */
	public static Bitmap snapShotWithoutStatusBar(Activity activity) {
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		int width = UtilAndroid.getScreenWidth(activity);
		int height = UtilAndroid.getScreenHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight);
		view.destroyDrawingCache();
		return bp;
	}
}
