package com.mp.basekit.util;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.mp.basekit.core.MPApplication;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/08/12
 *     desc  : utils about image
 * </pre>
 */
public final class ImageUtils {

    private ImageUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * String to bitmap.
     *
     * @param string
     * @return
     */
    public static Bitmap string2Bitmap(String string) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * Bitmap to bytes.
     *
     * @param bitmap The bitmap.
     * @param format The format of bitmap.
     * @return bytes
     */
    public static byte[] bitmap2Bytes(final Bitmap bitmap, final CompressFormat format) {
        if (bitmap == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, 100, baos);
        return baos.toByteArray();
    }

    /**
     * Bytes to bitmap.
     *
     * @param bytes The bytes.
     * @return bitmap
     */
    public static Bitmap bytes2Bitmap(final byte[] bytes) {
        return (bytes == null || bytes.length == 0)
                ? null
                : BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * Drawable to bitmap.
     *
     * @param drawable The drawable.
     * @return bitmap
     */
    public static Bitmap drawable2Bitmap(final Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        Bitmap bitmap;
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1,
                    drawable.getOpacity() != PixelFormat.OPAQUE
                            ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE
                            ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Bitmap to drawable.
     *
     * @param bitmap The bitmap.
     * @return drawable
     */
    public static Drawable bitmap2Drawable(final Bitmap bitmap) {
        return bitmap == null ? null : new BitmapDrawable(MPUtils.getApp().getResources(), bitmap);
    }

    /**
     * Drawable to bytes.
     *
     * @param drawable The drawable.
     * @param format   The format of bitmap.
     * @return bytes
     */
    public static byte[] drawable2Bytes(final Drawable drawable, final CompressFormat format) {
        return drawable == null ? null : bitmap2Bytes(drawable2Bitmap(drawable), format);
    }

    /**
     * Bytes to drawable.
     *
     * @param bytes The bytes.
     * @return drawable
     */
    public static Drawable bytes2Drawable(final byte[] bytes) {
        return bitmap2Drawable(bytes2Bitmap(bytes));
    }

    /**
     * View to bitmap.
     *
     * @param view The view.
     * @return bitmap
     */
    public static Bitmap view2Bitmap(final View view) {
        if (view == null) return null;
        Bitmap ret = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(ret);
//        Drawable bgDrawable = view.getBackground();
//        if (bgDrawable != null) {
//            bgDrawable.draw(canvas);
//        } else {
//            canvas.drawColor(Color.WHITE);
//        }
        view.draw(canvas);
        return ret;
    }

    /**
     * Return bitmap.
     *
     * @param file The file.
     * @return bitmap
     */
    public static Bitmap getBitmap(final File file) {
        if (file == null) return null;
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    /**
     * Return bitmap.
     *
     * @param file      The file.
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @return bitmap
     */
    public static Bitmap getBitmap(final File file, final int maxWidth, final int maxHeight) {
        if (file == null) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    /**
     * Return bitmap.
     *
     * @param filePath The path of file.
     * @return bitmap
     */
    public static Bitmap getBitmap(final String filePath) {
        if (isSpace(filePath)) return null;
        return getBitmap(getImageContentUri(filePath));
    }

    // 通过uri加载图片
    public static Bitmap getBitmap(Uri uri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    MPApplication.getAppContext().getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Return bitmap.
     *
     * @param filePath  The path of file.
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @return bitmap
     */
    public static Bitmap getBitmap(final String filePath, final int maxWidth, final int maxHeight) {
        if (isSpace(filePath)) return null;

        return getBitmap(getImageContentUri(filePath));
    }

    public static Uri getImageContentUri(String path) {
        Context context = MPApplication.getAppContext();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{path}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            // 如果图片不在手机的共享图片数据库，就先把它插入。
            if (new File(path).exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, path);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * Return bitmap.
     *
     * @param is The input stream.
     * @return bitmap
     */
    public static Bitmap getBitmap(final InputStream is) {
        if (is == null) return null;
        return BitmapFactory.decodeStream(is);
    }

    /**
     * Return bitmap.
     *
     * @param is        The input stream.
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @return bitmap
     */
    public static Bitmap getBitmap(final InputStream is, final int maxWidth, final int maxHeight) {
        if (is == null) return null;
        byte[] bytes = input2Byte(is);
        return getBitmap(bytes, 0, maxWidth, maxHeight);
    }

    /**
     * Return bitmap.
     *
     * @param data   The data.
     * @param offset The offset.
     * @return bitmap
     */
    public static Bitmap getBitmap(final byte[] data, final int offset) {
        if (data.length == 0) return null;
        return BitmapFactory.decodeByteArray(data, offset, data.length);
    }

    /**
     * Return bitmap.
     *
     * @param data      The data.
     * @param offset    The offset.
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @return bitmap
     */
    public static Bitmap getBitmap(final byte[] data,
                                   final int offset,
                                   final int maxWidth,
                                   final int maxHeight) {
        if (data.length == 0) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, offset, data.length, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, offset, data.length, options);
    }

    /**
     * Return bitmap.
     *
     * @param resId The resource id.
     * @return bitmap
     */
    public static Bitmap getBitmap(@DrawableRes final int resId) {
        Drawable drawable = ContextCompat.getDrawable(MPUtils.getApp(), resId);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Return bitmap.
     *
     * @param resId     The resource id.
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @return bitmap
     */
    public static Bitmap getBitmap(@DrawableRes final int resId,
                                   final int maxWidth,
                                   final int maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        final Resources resources = MPUtils.getApp().getResources();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resId, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(resources, resId, options);
    }

    /**
     * Return bitmap.
     *
     * @param fd The file descriptor.
     * @return bitmap
     */
    public static Bitmap getBitmap(final FileDescriptor fd) {
        if (fd == null) return null;
        return BitmapFactory.decodeFileDescriptor(fd);
    }

    /**
     * Return bitmap.
     *
     * @param fd        The file descriptor
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @return bitmap
     */
    public static Bitmap getBitmap(final FileDescriptor fd,
                                   final int maxWidth,
                                   final int maxHeight) {
        if (fd == null) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd, null, options);
    }

    /**
     * Return the bitmap with the specified color.
     *
     * @param src   The source of bitmap.
     * @param color The color.
     * @return the bitmap with the specified color
     */
    public static Bitmap drawColor(@NonNull final Bitmap src, @ColorInt final int color) {
        return drawColor(src, color, false);
    }

    /**
     * Return the bitmap with the specified color.
     *
     * @param src     The source of bitmap.
     * @param color   The color.
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return the bitmap with the specified color
     */
    public static Bitmap drawColor(@NonNull final Bitmap src,
                                   @ColorInt final int color,
                                   final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Bitmap ret = recycle ? src : src.copy(src.getConfig(), true);
        Canvas canvas = new Canvas(ret);
        canvas.drawColor(color, PorterDuff.Mode.DARKEN);
        return ret;
    }

    /**
     * Return the scaled bitmap.
     *
     * @param src       The source of bitmap.
     * @param newWidth  The new width.
     * @param newHeight The new height.
     * @return the scaled bitmap
     */
    public static Bitmap scale(final Bitmap src, final int newWidth, final int newHeight) {
        return scale(src, newWidth, newHeight, false);
    }

    /**
     * Return the scaled bitmap.
     *
     * @param src       The source of bitmap.
     * @param newWidth  The new width.
     * @param newHeight The new height.
     * @param recycle   True to recycle the source of bitmap, false otherwise.
     * @return the scaled bitmap
     */
    public static Bitmap scale(final Bitmap src,
                               final int newWidth,
                               final int newHeight,
                               final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Bitmap ret = Bitmap.createScaledBitmap(src, newWidth, newHeight, true);
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * Return the scaled bitmap
     *
     * @param src         The source of bitmap.
     * @param scaleWidth  The scale of width.
     * @param scaleHeight The scale of height.
     * @return the scaled bitmap
     */
    public static Bitmap scale(final Bitmap src, final float scaleWidth, final float scaleHeight) {
        return scale(src, scaleWidth, scaleHeight, false);
    }

    /**
     * Return the scaled bitmap
     *
     * @param src         The source of bitmap.
     * @param scaleWidth  The scale of width.
     * @param scaleHeight The scale of height.
     * @param recycle     True to recycle the source of bitmap, false otherwise.
     * @return the scaled bitmap
     */
    public static Bitmap scale(final Bitmap src,
                               final float scaleWidth,
                               final float scaleHeight,
                               final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Matrix matrix = new Matrix();
        matrix.setScale(scaleWidth, scaleHeight);
        Bitmap ret = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * Return the clipped bitmap.
     *
     * @param src    The source of bitmap.
     * @param x      The x coordinate of the first pixel.
     * @param y      The y coordinate of the first pixel.
     * @param width  The width.
     * @param height The height.
     * @return the clipped bitmap
     */
    public static Bitmap clip(final Bitmap src,
                              final int x,
                              final int y,
                              final int width,
                              final int height) {
        return clip(src, x, y, width, height, false);
    }

    /**
     * Return the clipped bitmap.
     *
     * @param src     The source of bitmap.
     * @param x       The x coordinate of the first pixel.
     * @param y       The y coordinate of the first pixel.
     * @param width   The width.
     * @param height  The height.
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return the clipped bitmap
     */
    public static Bitmap clip(final Bitmap src,
                              final int x,
                              final int y,
                              final int width,
                              final int height,
                              final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Bitmap ret = Bitmap.createBitmap(src, x, y, width, height);
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * Return the skewed bitmap.
     *
     * @param src The source of bitmap.
     * @param kx  The skew factor of x.
     * @param ky  The skew factor of y.
     * @return the skewed bitmap
     */
    public static Bitmap skew(final Bitmap src, final float kx, final float ky) {
        return skew(src, kx, ky, 0, 0, false);
    }

    /**
     * Return the skewed bitmap.
     *
     * @param src     The source of bitmap.
     * @param kx      The skew factor of x.
     * @param ky      The skew factor of y.
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return the skewed bitmap
     */
    public static Bitmap skew(final Bitmap src,
                              final float kx,
                              final float ky,
                              final boolean recycle) {
        return skew(src, kx, ky, 0, 0, recycle);
    }

    /**
     * Return the skewed bitmap.
     *
     * @param src The source of bitmap.
     * @param kx  The skew factor of x.
     * @param ky  The skew factor of y.
     * @param px  The x coordinate of the pivot point.
     * @param py  The y coordinate of the pivot point.
     * @return the skewed bitmap
     */
    public static Bitmap skew(final Bitmap src,
                              final float kx,
                              final float ky,
                              final float px,
                              final float py) {
        return skew(src, kx, ky, px, py, false);
    }

    /**
     * Return the skewed bitmap.
     *
     * @param src     The source of bitmap.
     * @param kx      The skew factor of x.
     * @param ky      The skew factor of y.
     * @param px      The x coordinate of the pivot point.
     * @param py      The y coordinate of the pivot point.
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return the skewed bitmap
     */
    public static Bitmap skew(final Bitmap src,
                              final float kx,
                              final float ky,
                              final float px,
                              final float py,
                              final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Matrix matrix = new Matrix();
        matrix.setSkew(kx, ky, px, py);
        Bitmap ret = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * Return the rotated bitmap.
     *
     * @param src     The source of bitmap.
     * @param degrees The number of degrees.
     * @param px      The x coordinate of the pivot point.
     * @param py      The y coordinate of the pivot point.
     * @return the rotated bitmap
     */
    public static Bitmap rotate(final Bitmap src,
                                final int degrees,
                                final float px,
                                final float py) {
        return rotate(src, degrees, px, py, false);
    }

    /**
     * Return the rotated bitmap.
     *
     * @param src     The source of bitmap.
     * @param degrees The number of degrees.
     * @param px      The x coordinate of the pivot point.
     * @param py      The y coordinate of the pivot point.
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return the rotated bitmap
     */
    public static Bitmap rotate(final Bitmap src,
                                final int degrees,
                                final float px,
                                final float py,
                                final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        if (degrees == 0) return src;
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, px, py);
        Bitmap ret = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * Return the rotated degree.
     *
     * @param filePath The path of file.
     * @return the rotated degree
     */
    public static int getRotateDegree(final String filePath) {
        try {
            ExifInterface exifInterface = new ExifInterface(filePath);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
            );
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Return the round bitmap.
     *
     * @param src The source of bitmap.
     * @return the round bitmap
     */
    public static Bitmap toRound(final Bitmap src) {
        return toRound(src, 0, 0, false);
    }

    /**
     * Return the round bitmap.
     *
     * @param src     The source of bitmap.
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return the round bitmap
     */
    public static Bitmap toRound(final Bitmap src, final boolean recycle) {
        return toRound(src, 0, 0, recycle);
    }

    /**
     * Return the round bitmap.
     *
     * @param src         The source of bitmap.
     * @param borderSize  The size of border.
     * @param borderColor The color of border.
     * @return the round bitmap
     */
    public static Bitmap toRound(final Bitmap src,
                                 @IntRange(from = 0) int borderSize,
                                 @ColorInt int borderColor) {
        return toRound(src, borderSize, borderColor, false);
    }

    /**
     * Return the round bitmap.
     *
     * @param src         The source of bitmap.
     * @param recycle     True to recycle the source of bitmap, false otherwise.
     * @param borderSize  The size of border.
     * @param borderColor The color of border.
     * @return the round bitmap
     */
    public static Bitmap toRound(final Bitmap src,
                                 @IntRange(from = 0) int borderSize,
                                 @ColorInt int borderColor,
                                 final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        int width = src.getWidth();
        int height = src.getHeight();
        int size = Math.min(width, height);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap ret = Bitmap.createBitmap(width, height, src.getConfig());
        float center = size / 2f;
        RectF rectF = new RectF(0, 0, width, height);
        rectF.inset((width - size) / 2f, (height - size) / 2f);
        Matrix matrix = new Matrix();
        matrix.setTranslate(rectF.left, rectF.top);
        matrix.preScale((float) size / width, (float) size / height);
        BitmapShader shader = new BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        shader.setLocalMatrix(matrix);
        paint.setShader(shader);
        Canvas canvas = new Canvas(ret);
        canvas.drawRoundRect(rectF, center, center, paint);
        if (borderSize > 0) {
            paint.setShader(null);
            paint.setColor(borderColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(borderSize);
            float radius = center - borderSize / 2f;
            canvas.drawCircle(width / 2f, height / 2f, radius, paint);
        }
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * Return the round corner bitmap.
     *
     * @param src    The source of bitmap.
     * @param radius The radius of corner.
     * @return the round corner bitmap
     */
    public static Bitmap toRoundCorner(final Bitmap src, final float radius) {
        return toRoundCorner(src, radius, 0, 0, false);
    }

    /**
     * Return the round corner bitmap.
     *
     * @param src     The source of bitmap.
     * @param radius  The radius of corner.
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return the round corner bitmap
     */
    public static Bitmap toRoundCorner(final Bitmap src,
                                       final float radius,
                                       final boolean recycle) {
        return toRoundCorner(src, radius, 0, 0, recycle);
    }

    /**
     * Return the round corner bitmap.
     *
     * @param src         The source of bitmap.
     * @param radius      The radius of corner.
     * @param borderSize  The size of border.
     * @param borderColor The color of border.
     * @return the round corner bitmap
     */
    public static Bitmap toRoundCorner(final Bitmap src,
                                       final float radius,
                                       @IntRange(from = 0) int borderSize,
                                       @ColorInt int borderColor) {
        return toRoundCorner(src, radius, borderSize, borderColor, false);
    }

    /**
     * Return the round corner bitmap.
     *
     * @param src         The source of bitmap.
     * @param radius      The radius of corner.
     * @param borderSize  The size of border.
     * @param borderColor The color of border.
     * @param recycle     True to recycle the source of bitmap, false otherwise.
     * @return the round corner bitmap
     */
    public static Bitmap toRoundCorner(final Bitmap src,
                                       final float radius,
                                       @IntRange(from = 0) int borderSize,
                                       @ColorInt int borderColor,
                                       final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        int width = src.getWidth();
        int height = src.getHeight();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap ret = Bitmap.createBitmap(width, height, src.getConfig());
        BitmapShader shader = new BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        Canvas canvas = new Canvas(ret);
        RectF rectF = new RectF(0, 0, width, height);
        float halfBorderSize = borderSize / 2f;
        rectF.inset(halfBorderSize, halfBorderSize);
        canvas.drawRoundRect(rectF, radius, radius, paint);
        if (borderSize > 0) {
            paint.setShader(null);
            paint.setColor(borderColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(borderSize);
            paint.setStrokeCap(Paint.Cap.ROUND);
            canvas.drawRoundRect(rectF, radius, radius, paint);
        }
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * Return the round corner bitmap with border.
     *
     * @param src          The source of bitmap.
     * @param borderSize   The size of border.
     * @param color        The color of border.
     * @param cornerRadius The radius of corner.
     * @return the round corner bitmap with border
     */
    public static Bitmap addCornerBorder(final Bitmap src,
                                         @IntRange(from = 1) final int borderSize,
                                         @ColorInt final int color,
                                         @FloatRange(from = 0) final float cornerRadius) {
        return addBorder(src, borderSize, color, false, cornerRadius, false);
    }

    /**
     * Return the round corner bitmap with border.
     *
     * @param src          The source of bitmap.
     * @param borderSize   The size of border.
     * @param color        The color of border.
     * @param cornerRadius The radius of corner.
     * @param recycle      True to recycle the source of bitmap, false otherwise.
     * @return the round corner bitmap with border
     */
    public static Bitmap addCornerBorder(final Bitmap src,
                                         @IntRange(from = 1) final int borderSize,
                                         @ColorInt final int color,
                                         @FloatRange(from = 0) final float cornerRadius,
                                         final boolean recycle) {
        return addBorder(src, borderSize, color, false, cornerRadius, recycle);
    }

    /**
     * Return the round bitmap with border.
     *
     * @param src        The source of bitmap.
     * @param borderSize The size of border.
     * @param color      The color of border.
     * @return the round bitmap with border
     */
    public static Bitmap addCircleBorder(final Bitmap src,
                                         @IntRange(from = 1) final int borderSize,
                                         @ColorInt final int color) {
        return addBorder(src, borderSize, color, true, 0, false);
    }

    /**
     * Return the round bitmap with border.
     *
     * @param src        The source of bitmap.
     * @param borderSize The size of border.
     * @param color      The color of border.
     * @param recycle    True to recycle the source of bitmap, false otherwise.
     * @return the round bitmap with border
     */
    public static Bitmap addCircleBorder(final Bitmap src,
                                         @IntRange(from = 1) final int borderSize,
                                         @ColorInt final int color,
                                         final boolean recycle) {
        return addBorder(src, borderSize, color, true, 0, recycle);
    }

    /**
     * Return the bitmap with border.
     *
     * @param src          The source of bitmap.
     * @param borderSize   The size of border.
     * @param color        The color of border.
     * @param isCircle     True to draw circle, false to draw corner.
     * @param cornerRadius The radius of corner.
     * @param recycle      True to recycle the source of bitmap, false otherwise.
     * @return the bitmap with border
     */
    private static Bitmap addBorder(final Bitmap src,
                                    @IntRange(from = 1) final int borderSize,
                                    @ColorInt final int color,
                                    final boolean isCircle,
                                    final float cornerRadius,
                                    final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Bitmap ret = recycle ? src : src.copy(src.getConfig(), true);
        int width = ret.getWidth();
        int height = ret.getHeight();
        Canvas canvas = new Canvas(ret);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderSize);
        if (isCircle) {
            float radius = Math.min(width, height) / 2f - borderSize / 2f;
            canvas.drawCircle(width / 2f, height / 2f, radius, paint);
        } else {
            int halfBorderSize = borderSize >> 1;
            RectF rectF = new RectF(halfBorderSize, halfBorderSize,
                    width - halfBorderSize, height - halfBorderSize);
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);
        }
        return ret;
    }

    /**
     * Return the bitmap with reflection.
     *
     * @param src              The source of bitmap.
     * @param reflectionHeight The height of reflection.
     * @return the bitmap with reflection
     */
    public static Bitmap addReflection(final Bitmap src, final int reflectionHeight) {
        return addReflection(src, reflectionHeight, false);
    }

    /**
     * Return the bitmap with reflection.
     *
     * @param src              The source of bitmap.
     * @param reflectionHeight The height of reflection.
     * @param recycle          True to recycle the source of bitmap, false otherwise.
     * @return the bitmap with reflection
     */
    public static Bitmap addReflection(final Bitmap src,
                                       final int reflectionHeight,
                                       final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        final int REFLECTION_GAP = 0;
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        Bitmap reflectionBitmap = Bitmap.createBitmap(src, 0, srcHeight - reflectionHeight,
                srcWidth, reflectionHeight, matrix, false);
        Bitmap ret = Bitmap.createBitmap(srcWidth, srcHeight + reflectionHeight, src.getConfig());
        Canvas canvas = new Canvas(ret);
        canvas.drawBitmap(src, 0, 0, null);
        canvas.drawBitmap(reflectionBitmap, 0, srcHeight + REFLECTION_GAP, null);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        LinearGradient shader = new LinearGradient(
                0, srcHeight,
                0, ret.getHeight() + REFLECTION_GAP,
                0x70FFFFFF,
                0x00FFFFFF,
                Shader.TileMode.MIRROR);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawRect(0, srcHeight + REFLECTION_GAP, srcWidth, ret.getHeight(), paint);
        if (!reflectionBitmap.isRecycled()) reflectionBitmap.recycle();
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * Return the bitmap with text watermarking.
     *
     * @param src      The source of bitmap.
     * @param content  The content of text.
     * @param textSize The size of text.
     * @param color    The color of text.
     * @param x        The x coordinate of the first pixel.
     * @param y        The y coordinate of the first pixel.
     * @return the bitmap with text watermarking
     */
    public static Bitmap addTextWatermark(final Bitmap src,
                                          final String content,
                                          final int textSize,
                                          @ColorInt final int color,
                                          final float x,
                                          final float y) {
        return addTextWatermark(src, content, textSize, color, x, y, false);
    }

    /**
     * Return the bitmap with text watermarking.
     *
     * @param src      The source of bitmap.
     * @param content  The content of text.
     * @param textSize The size of text.
     * @param color    The color of text.
     * @param x        The x coordinate of the first pixel.
     * @param y        The y coordinate of the first pixel.
     * @param recycle  True to recycle the source of bitmap, false otherwise.
     * @return the bitmap with text watermarking
     */
    public static Bitmap addTextWatermark(final Bitmap src,
                                          final String content,
                                          final float textSize,
                                          @ColorInt final int color,
                                          final float x,
                                          final float y,
                                          final boolean recycle) {
        if (isEmptyBitmap(src) || content == null) return null;
        Bitmap ret = src.copy(src.getConfig(), true);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Canvas canvas = new Canvas(ret);
        paint.setColor(color);
        paint.setTextSize(textSize);
        Rect bounds = new Rect();
        paint.getTextBounds(content, 0, content.length(), bounds);
        canvas.drawText(content, x, y + textSize, paint);
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * Return the bitmap with image watermarking.
     *
     * @param src       The source of bitmap.
     * @param watermark The image watermarking.
     * @param x         The x coordinate of the first pixel.
     * @param y         The y coordinate of the first pixel.
     * @param alpha     The alpha of watermark.
     * @return the bitmap with image watermarking
     */
    public static Bitmap addImageWatermark(final Bitmap src,
                                           final Bitmap watermark,
                                           final int x, final int y,
                                           final int alpha) {
        return addImageWatermark(src, watermark, x, y, alpha, false);
    }

    /**
     * Return the bitmap with image watermarking.
     *
     * @param src       The source of bitmap.
     * @param watermark The image watermarking.
     * @param x         The x coordinate of the first pixel.
     * @param y         The y coordinate of the first pixel.
     * @param alpha     The alpha of watermark.
     * @param recycle   True to recycle the source of bitmap, false otherwise.
     * @return the bitmap with image watermarking
     */
    public static Bitmap addImageWatermark(final Bitmap src,
                                           final Bitmap watermark,
                                           final int x,
                                           final int y,
                                           final int alpha,
                                           final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Bitmap ret = src.copy(src.getConfig(), true);
        if (!isEmptyBitmap(watermark)) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            Canvas canvas = new Canvas(ret);
            paint.setAlpha(alpha);
            canvas.drawBitmap(watermark, x, y, paint);
        }
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * Return the alpha bitmap.
     *
     * @param src The source of bitmap.
     * @return the alpha bitmap
     */
    public static Bitmap toAlpha(final Bitmap src) {
        return toAlpha(src, false);
    }

    /**
     * Return the alpha bitmap.
     *
     * @param src     The source of bitmap.
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return the alpha bitmap
     */
    public static Bitmap toAlpha(final Bitmap src, final Boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Bitmap ret = src.extractAlpha();
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * Return the gray bitmap.
     *
     * @param src The source of bitmap.
     * @return the gray bitmap
     */
    public static Bitmap toGray(final Bitmap src) {
        return toGray(src, false);
    }

    /**
     * Return the gray bitmap.
     *
     * @param src     The source of bitmap.
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return the gray bitmap
     */
    public static Bitmap toGray(final Bitmap src, final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Bitmap ret = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        Canvas canvas = new Canvas(ret);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorMatrixColorFilter);
        canvas.drawBitmap(src, 0, 0, paint);
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * Return the blur bitmap fast.
     * <p>zoom out, blur, zoom in</p>
     *
     * @param src    The source of bitmap.
     * @param scale  The scale(0...1).
     * @param radius The radius(0...25).
     * @return the blur bitmap
     */
    public static Bitmap fastBlur(final Bitmap src,
                                  @FloatRange(
                                          from = 0, to = 1, fromInclusive = false
                                  ) final float scale,
                                  @FloatRange(
                                          from = 0, to = 25, fromInclusive = false
                                  ) final float radius) {
        return fastBlur(src, scale, radius, false);
    }

    /**
     * Return the blur bitmap fast.
     * <p>zoom out, blur, zoom in</p>
     *
     * @param src     The source of bitmap.
     * @param scale   The scale(0...1).
     * @param radius  The radius(0...25).
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return the blur bitmap
     */
    public static Bitmap fastBlur(final Bitmap src,
                                  @FloatRange(
                                          from = 0, to = 1, fromInclusive = false
                                  ) final float scale,
                                  @FloatRange(
                                          from = 0, to = 25, fromInclusive = false
                                  ) final float radius,
                                  final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        int width = src.getWidth();
        int height = src.getHeight();
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        Bitmap scaleBitmap =
                Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
        Canvas canvas = new Canvas();
        PorterDuffColorFilter filter = new PorterDuffColorFilter(
                Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP);
        paint.setColorFilter(filter);
        canvas.scale(scale, scale);
        canvas.drawBitmap(scaleBitmap, 0, 0, paint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            scaleBitmap = renderScriptBlur(scaleBitmap, radius, recycle);
        } else {
            scaleBitmap = stackBlur(scaleBitmap, (int) radius, recycle);
        }
        if (scale == 1) {
            if (recycle && !src.isRecycled()) src.recycle();
            return scaleBitmap;
        }
        Bitmap ret = Bitmap.createScaledBitmap(scaleBitmap, width, height, true);
        if (!scaleBitmap.isRecycled()) scaleBitmap.recycle();
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * Return the blur bitmap using render script.
     *
     * @param src    The source of bitmap.
     * @param radius The radius(0...25).
     * @return the blur bitmap
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap renderScriptBlur(final Bitmap src,
                                          @FloatRange(
                                                  from = 0, to = 25, fromInclusive = false
                                          ) final float radius) {
        return renderScriptBlur(src, radius, false);
    }

    /**
     * Return the blur bitmap using render script.
     *
     * @param src     The source of bitmap.
     * @param radius  The radius(0...25).
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return the blur bitmap
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap renderScriptBlur(final Bitmap src,
                                          @FloatRange(
                                                  from = 0, to = 25, fromInclusive = false
                                          ) final float radius,
                                          final boolean recycle) {
        RenderScript rs = null;
        Bitmap ret = recycle ? src : src.copy(src.getConfig(), true);
        try {
            rs = RenderScript.create(MPUtils.getApp());
            rs.setMessageHandler(new RenderScript.RSMessageHandler());
            Allocation input = Allocation.createFromBitmap(rs,
                    ret,
                    Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            Allocation output = Allocation.createTyped(rs, input.getType());
            ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            blurScript.setInput(input);
            blurScript.setRadius(radius);
            blurScript.forEach(output);
            output.copyTo(ret);
        } finally {
            if (rs != null) {
                rs.destroy();
            }
        }
        return ret;
    }

    /**
     * Return the blur bitmap using stack.
     *
     * @param src    The source of bitmap.
     * @param radius The radius(0...25).
     * @return the blur bitmap
     */
    public static Bitmap stackBlur(final Bitmap src, final int radius) {
        return stackBlur(src, radius, false);
    }

    /**
     * Return the blur bitmap using stack.
     *
     * @param src     The source of bitmap.
     * @param radius  The radius(0...25).
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return the blur bitmap
     */
    public static Bitmap stackBlur(final Bitmap src, int radius, final boolean recycle) {
        Bitmap ret = recycle ? src : src.copy(src.getConfig(), true);
        if (radius < 1) {
            radius = 1;
        }
        int w = ret.getWidth();
        int h = ret.getHeight();

        int[] pix = new int[w * h];
        ret.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }
        ret.setPixels(pix, 0, w, 0, 0, w, h);
        return ret;
    }

    /**
     * Save the bitmap.
     *
     * @param src      The source of bitmap.
     * @param filePath The path of file.
     * @param format   The format of the image.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean save(final Bitmap src,
                               final String filePath,
                               final CompressFormat format) {
        return save(src, getFileByPath(filePath), format, false);
    }

    /**
     * Save the bitmap.
     *
     * @param src    The source of bitmap.
     * @param file   The file.
     * @param format The format of the image.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean save(final Bitmap src, final File file, final CompressFormat format) {
        return save(src, file, format, false);
    }

    /**
     * Save the bitmap.
     *
     * @param src      The source of bitmap.
     * @param filePath The path of file.
     * @param format   The format of the image.
     * @param recycle  True to recycle the source of bitmap, false otherwise.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean save(final Bitmap src,
                               final String filePath,
                               final CompressFormat format,
                               final boolean recycle) {
        return save(src, getFileByPath(filePath), format, recycle);
    }

    /**
     * Save the bitmap.
     *
     * @param src     The source of bitmap.
     * @param file    The file.
     * @param format  The format of the image.
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean save(final Bitmap src,
                               final File file,
                               final CompressFormat format,
                               final boolean recycle) {
        if (isEmptyBitmap(src) || !createFileByDeleteOldFile(file)) return false;
        OutputStream os = null;
        boolean ret = false;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            ret = src.compress(format, 100, os);
            if (recycle && !src.isRecycled()) src.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * Return whether it is a image according to the file name.
     *
     * @param file The file.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isImage(final File file) {
        return file != null && isImage(file.getPath());
    }

    /**
     * Return whether it is a image according to the file name.
     *
     * @param filePath The path of file.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isImage(final String filePath) {
        String path = filePath.toUpperCase();
        return path.endsWith(".PNG") || path.endsWith(".JPG")
                || path.endsWith(".JPEG") || path.endsWith(".BMP")
                || path.endsWith(".GIF") || path.endsWith(".WEBP");
    }

    /**
     * Return the type of image.
     *
     * @param filePath The path of file.
     * @return the type of image
     */
    public static String getImageType(final String filePath) {
        return getImageType(getFileByPath(filePath));
    }

    /**
     * Return the type of image.
     *
     * @param file The file.
     * @return the type of image
     */
    public static String getImageType(final File file) {
        if (file == null) return "";
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            String type = getImageType(is);
            if (type != null) {
                return type;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return getFileExtension(file.getAbsolutePath()).toUpperCase();
    }

    private static String getFileExtension(final String filePath) {
        if (isSpace(filePath)) return filePath;
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastPoi == -1 || lastSep >= lastPoi) return "";
        return filePath.substring(lastPoi + 1);
    }

    private static String getImageType(final InputStream is) {
        if (is == null) return null;
        try {
            byte[] bytes = new byte[8];
            return is.read(bytes, 0, 8) != -1 ? getImageType(bytes) : null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getImageType(final byte[] bytes) {
        if (isJPEG(bytes)) return "JPEG";
        if (isGIF(bytes)) return "GIF";
        if (isPNG(bytes)) return "PNG";
        if (isBMP(bytes)) return "BMP";
        return null;
    }

    private static boolean isJPEG(final byte[] b) {
        return b.length >= 2
                && (b[0] == (byte) 0xFF) && (b[1] == (byte) 0xD8);
    }

    private static boolean isGIF(final byte[] b) {
        return b.length >= 6
                && b[0] == 'G' && b[1] == 'I'
                && b[2] == 'F' && b[3] == '8'
                && (b[4] == '7' || b[4] == '9') && b[5] == 'a';
    }

    private static boolean isPNG(final byte[] b) {
        return b.length >= 8
                && (b[0] == (byte) 137 && b[1] == (byte) 80
                && b[2] == (byte) 78 && b[3] == (byte) 71
                && b[4] == (byte) 13 && b[5] == (byte) 10
                && b[6] == (byte) 26 && b[7] == (byte) 10);
    }

    private static boolean isBMP(final byte[] b) {
        return b.length >= 2
                && (b[0] == 0x42) && (b[1] == 0x4d);
    }

    private static boolean isEmptyBitmap(final Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }

    ///////////////////////////////////////////////////////////////////////////
    // about compress
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Return the compressed bitmap using scale.
     *
     * @param src       The source of bitmap.
     * @param newWidth  The new width.
     * @param newHeight The new height.
     * @return the compressed bitmap
     */
    public static Bitmap compressByScale(final Bitmap src,
                                         final int newWidth,
                                         final int newHeight) {
        return scale(src, newWidth, newHeight, false);
    }

    /**
     * Return the compressed bitmap using scale.
     *
     * @param src       The source of bitmap.
     * @param newWidth  The new width.
     * @param newHeight The new height.
     * @param recycle   True to recycle the source of bitmap, false otherwise.
     * @return the compressed bitmap
     */
    public static Bitmap compressByScale(final Bitmap src,
                                         final int newWidth,
                                         final int newHeight,
                                         final boolean recycle) {
        return scale(src, newWidth, newHeight, recycle);
    }

    /**
     * Return the compressed bitmap using scale.
     *
     * @param src         The source of bitmap.
     * @param scaleWidth  The scale of width.
     * @param scaleHeight The scale of height.
     * @return the compressed bitmap
     */
    public static Bitmap compressByScale(final Bitmap src,
                                         final float scaleWidth,
                                         final float scaleHeight) {
        return scale(src, scaleWidth, scaleHeight, false);
    }

    /**
     * Return the compressed bitmap using scale.
     *
     * @param src         The source of bitmap.
     * @param scaleWidth  The scale of width.
     * @param scaleHeight The scale of height.
     * @param recycle     True to recycle the source of bitmap, false otherwise.
     * @return he compressed bitmap
     */
    public static Bitmap compressByScale(final Bitmap src,
                                         final float scaleWidth,
                                         final float scaleHeight,
                                         final boolean recycle) {
        return scale(src, scaleWidth, scaleHeight, recycle);
    }

    /**
     * Return the compressed bitmap using quality.
     *
     * @param src     The source of bitmap.
     * @param quality The quality.
     * @return the compressed bitmap
     */
    public static Bitmap compressByQuality(final Bitmap src,
                                           @IntRange(from = 0, to = 100) final int quality) {
        return compressByQuality(src, quality, false);
    }

    /**
     * Return the compressed bitmap using quality.
     *
     * @param src     The source of bitmap.
     * @param quality The quality.
     * @param recycle True to recycle the source of bitmap, false otherwise.
     * @return the compressed bitmap
     */
    public static Bitmap compressByQuality(final Bitmap src,
                                           @IntRange(from = 0, to = 100) final int quality,
                                           final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(CompressFormat.JPEG, quality, baos);
        byte[] bytes = baos.toByteArray();
        if (recycle && !src.isRecycled()) src.recycle();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * Return the compressed bitmap using quality.
     *
     * @param src         The source of bitmap.
     * @param maxByteSize The maximum size of byte.
     * @return the compressed bitmap
     */
    public static Bitmap compressByQuality(final Bitmap src, final long maxByteSize) {
        return compressByQuality(src, maxByteSize, false);
    }

    /**
     * Return the compressed bitmap using quality.
     *
     * @param src         The source of bitmap.
     * @param maxByteSize The maximum size of byte.
     * @param recycle     True to recycle the source of bitmap, false otherwise.
     * @return the compressed bitmap
     */
    public static Bitmap compressByQuality(final Bitmap src,
                                           final long maxByteSize,
                                           final boolean recycle) {
        if (isEmptyBitmap(src) || maxByteSize <= 0) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(CompressFormat.JPEG, 100, baos);
        byte[] bytes;
        if (baos.size() <= maxByteSize) {
            bytes = baos.toByteArray();
        } else {
            baos.reset();
            src.compress(CompressFormat.JPEG, 0, baos);
            if (baos.size() >= maxByteSize) {
                bytes = baos.toByteArray();
            } else {
                // find the best quality using binary search
                int st = 0;
                int end = 100;
                int mid = 0;
                while (st < end) {
                    mid = (st + end) / 2;
                    baos.reset();
                    src.compress(CompressFormat.JPEG, mid, baos);
                    int len = baos.size();
                    if (len == maxByteSize) {
                        break;
                    } else if (len > maxByteSize) {
                        end = mid - 1;
                    } else {
                        st = mid + 1;
                    }
                }
                if (end == mid - 1) {
                    baos.reset();
                    src.compress(CompressFormat.JPEG, st, baos);
                }
                bytes = baos.toByteArray();
            }
        }
        if (recycle && !src.isRecycled()) src.recycle();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * Return the compressed bitmap using sample size.
     *
     * @param src        The source of bitmap.
     * @param sampleSize The sample size.
     * @return the compressed bitmap
     */

    public static Bitmap compressBySampleSize(final Bitmap src, final int sampleSize) {
        return compressBySampleSize(src, sampleSize, false);
    }

    /**
     * Return the compressed bitmap using sample size.
     *
     * @param src        The source of bitmap.
     * @param sampleSize The sample size.
     * @param recycle    True to recycle the source of bitmap, false otherwise.
     * @return the compressed bitmap
     */
    public static Bitmap compressBySampleSize(final Bitmap src,
                                              final int sampleSize,
                                              final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        if (recycle && !src.isRecycled()) src.recycle();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    /**
     * Return the compressed bitmap using sample size.
     *
     * @param src       The source of bitmap.
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @return the compressed bitmap
     */
    public static Bitmap compressBySampleSize(final Bitmap src,
                                              final int maxWidth,
                                              final int maxHeight) {
        return compressBySampleSize(src, maxWidth, maxHeight, false);
    }

    /**
     * Return the compressed bitmap using sample size.
     *
     * @param src       The source of bitmap.
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @param recycle   True to recycle the source of bitmap, false otherwise.
     * @return the compressed bitmap
     */
    public static Bitmap compressBySampleSize(final Bitmap src,
                                              final int maxWidth,
                                              final int maxHeight,
                                              final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        if (recycle && !src.isRecycled()) src.recycle();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    /**
     * Return the sample size.
     *
     * @param options   The options.
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @return the sample size
     */
    private static int calculateInSampleSize(final BitmapFactory.Options options,
                                             final int maxWidth,
                                             final int maxHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        while ((width >>= 1) >= maxWidth && (height >>= 1) >= maxHeight) {
            inSampleSize <<= 1;
        }
        return inSampleSize;
    }

    ///////////////////////////////////////////////////////////////////////////
    // other utils methods
    ///////////////////////////////////////////////////////////////////////////

    private static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    private static boolean createFileByDeleteOldFile(final File file) {
        if (file == null) return false;
        if (file.exists() && !file.delete()) return false;
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static byte[] input2Byte(final InputStream is) {
        if (is == null) return null;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int len;
            while ((len = is.read(b, 0, 1024)) != -1) {
                os.write(b, 0, len);
            }
            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @TargetApi(19)
    public static String getImageAbsolutePath(Context context, Uri imageUri) {
        if (context == null || imageUri == null)
            return null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    //byte数组到图片
    public static void byte2image(byte[] data, String path) {
        if (data.length < 3 || path.equals("")) return;
        try {
            FileOutputStream imageOutput = new FileOutputStream(new File(path));
            imageOutput.write(data, 0, data.length);
            imageOutput.close();
            System.out.println("Make Picture success,Please find image in " + path);
        } catch (Exception ex) {
            System.out.println("Exception: " + ex);
            ex.printStackTrace();
        }
    }

    /**
     * Store bitmap into specified image path
     *
     * @param bitmap
     * @param outPath
     * @throws FileNotFoundException
     */
    public static void storeImage(Context context, Bitmap bitmap, String outPath) {
        File f = new File(outPath);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            LogUtils.d("storeImage", "已经保存");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri data = Uri.fromFile(new File(outPath));
        mediaScanIntent.setData(data);
        context.sendBroadcast(mediaScanIntent);
    }

    /**
     * Store bitmap into specified image path
     *
     * @param bitmap
     * @param outPath
     * @throws FileNotFoundException
     */
    public static void storeImage(Context context, byte[] bitmap, String outPath) {
        File f = new File(outPath);
        if (f.exists()) {
            f.delete();
        }
        try {
            File file = new File(outPath);
            FileOutputStream fops = new FileOutputStream(file);
            fops.write(bitmap);
            fops.flush();
            fops.close();
            LogUtils.d("storeImage", "已经保存");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri data = Uri.fromFile(new File(outPath));
        mediaScanIntent.setData(data);
        context.sendBroadcast(mediaScanIntent);
    }

    /**
     * Store bitmap into specified image path
     *
     * @param bitmap
     * @param outPath
     * @throws FileNotFoundException
     */
    public static void storeImage(Bitmap bitmap, String outPath) {
        File f = new File(outPath);
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            LogUtils.d("storeImage", "已经保存");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Compress image by pixel, this will modify image width/height.
     * Used to get thumbnail
     *
     * @param imgPath image path
     * @param pixelW  target pixel of width
     * @param pixelH  target pixel of height
     * @return
     */
    public static Bitmap ratio(String imgPath, float pixelW, float pixelH) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true，即只读边不读内容
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        // Get bitmap info, but notice that bitmap is null now
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 想要缩放的目标尺寸
        float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
        float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        // 开始压缩图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        // 压缩好比例大小后再进行质量压缩
//        return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
    }

    /**
     * Compress image by size, this will modify image width/height.
     * Used to get thumbnail
     *
     * @param image
     * @param pixelW target pixel of width
     * @param pixelH target pixel of height
     * @return
     */
    public static Bitmap ratio(Bitmap image, float pixelW, float pixelH) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, os);
        if (os.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            os.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, os);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
        float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        is = new ByteArrayInputStream(os.toByteArray());
        bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        //压缩好比例大小后再进行质量压缩
//      return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
    }

    /**
     * Compress by quality,  and generate image to the path specified
     *
     * @param image
     * @param outPath
     * @param maxSize target will be compressed to be smaller than this size.(kb)
     * @throws IOException
     */
    public static void compressAndGenImage(Bitmap image, String outPath, int maxSize) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // scale
        int options = 100;
        // Store the bitmap into output stream(no compress)
        image.compress(Bitmap.CompressFormat.JPEG, options, os);
        // Compress by loop
        while (os.toByteArray().length / 1024 > maxSize) {
            // Clean up os
            os.reset();
            // interval 10
            options -= 10;
            Log.e("options", "options  :  " + options);
            if (options < 10) {
                return;
            }
            image.compress(Bitmap.CompressFormat.JPEG, options, os);
        }

        // Generate compressed image file
        FileOutputStream fos = new FileOutputStream(outPath);
        fos.write(os.toByteArray());
        fos.flush();
        fos.close();
    }

    private static int compressRatio(int currentKb, int targetKb) {
        int ratio;
        ratio = (int) Math.max(10, currentKb / Math.sqrt((double) currentKb / targetKb));
        return ratio;
    }

    /**
     * Compress by quality,  and generate image to the path specified
     *
     * @param imgPath
     * @param outPath
     * @param maxSize     target will be compressed to be smaller than this size.(kb)
     * @param needsDelete Whether delete original file after compress
     * @throws IOException
     */
    public static void compressAndGenImage(String imgPath, String outPath, int maxSize, boolean needsDelete) throws IOException {
        compressAndGenImage(getBitmap(imgPath), outPath, maxSize);

        // Delete original file
        if (needsDelete) {
            File file = new File(imgPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * Ratio and generate thumb to the path specified
     *
     * @param image
     * @param outPath
     * @param pixelW  target pixel of width
     * @param pixelH  target pixel of height
     * @throws FileNotFoundException
     */
    public static void ratioAndGenThumb(Bitmap image, String outPath, float pixelW, float pixelH) throws FileNotFoundException {
        Bitmap bitmap = ratio(image, pixelW, pixelH);
        storeImage(bitmap, outPath);
    }

    /**
     * Ratio and generate thumb to the path specified
     *
     * @param imgPath
     * @param outPath
     * @param pixelW      target pixel of width
     * @param pixelH      target pixel of height
     * @param needsDelete Whether delete original file after compress
     * @throws FileNotFoundException
     */
    public static void ratioAndGenThumb(String imgPath, String outPath, float pixelW, float pixelH, boolean needsDelete) throws FileNotFoundException {
        Bitmap bitmap = ratio(imgPath, pixelW, pixelH);
        storeImage(bitmap, outPath);

        // Delete original file
        if (needsDelete) {
            File file = new File(imgPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public static String getRealPathFromContentURI(Context ctx, Uri contentUri) {
        String result = "";
        if (null != contentUri && !"".equals(contentUri.toString())) {
            String scheme = contentUri.getScheme();
            if (ContentResolver.SCHEME_FILE.equals(scheme)) {
                result = contentUri.getPath();
            } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = null;
                try {
                    cursor = ctx.getContentResolver().query(contentUri, proj, null, null, null);
                    int column_index = 0;
                    if (cursor != null) {
                        column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        if (cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            result = cursor.getString(column_index);
                        } else {
                            result = "";
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }

            } else {
                result = contentUri.toString();
            }
        }

        return result;
    }

    public static void savePhotoToSysAlbum(Context context, String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri data = Uri.fromFile(new File(path));
        mediaScanIntent.setData(data);
        context.sendBroadcast(mediaScanIntent);
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


    public static Bitmap decodeBitmapFromFile(String path) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = 1;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap decodeSampledBitmapFromFile(String path,
                                                     int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap decodeSampledBitmapFromBytes(byte[] data,
                                                      int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    public static Bitmap compressBitmap(Bitmap image, int maxKb) {
        byte[] data = compressImage(image, maxKb);
        LogUtils.e("bitmap  " + data.length);
        ByteArrayInputStream isBm = new ByteArrayInputStream(data);//把压缩后的数据baos存放到ByteArrayInputStream中
        return BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
    }

    public static byte[] compressImage(Bitmap image, int maxKb) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        image.compress(Bitmap.CompressFormat.JPEG, options, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        while (baos.toByteArray().length / 1024 > maxKb && options > 10) {  //循环判断如果压缩后图片是否大于maxKb,大于继续压缩
            int de = Math.min(options / 2, 10);
            options -= de;
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        return baos.toByteArray();
    }

    private static byte[] compress(Bitmap image, int resw, int resh) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        int hh = resh;//这里设置高度为800f
        int ww = resw;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;

        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);

        return compressImage(bitmap, 100);//压缩好比例大小后再进行质量压缩
    }

    /**
     * 计算文件大小
     *
     * @param path
     * @return
     */
    public static int checkFileSize(String path) {
        File filenew = new File(path);
        int file_size = Integer.parseInt(String.valueOf(filenew.length() / 1024));
        return file_size;
    }

    public static String getFileType(String filePath) {
        return filePath.substring(filePath.lastIndexOf('.') + 1, filePath.length());
    }

    /**
     * 根据给定的宽进行缩放
     *
     * @param origin   原图
     * @param newWidth 新图的宽
     * @return new Bitmap
     */
    public static Bitmap scaleBitmap(Bitmap origin, int newWidth) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        if (newWidth > width) {
            return origin;
        }
        int height = origin.getHeight();
        int newHeight = newWidth * height / width;
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);// 使用后乘
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (!origin.isRecycled()) {
            origin.recycle();
        }
        return newBM;
    }

}
