
package com.image.picker;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author mrsimple
 */
public class UImageLoader {
    private LruCache<String, Bitmap> mMemoryCache;
    /**
     * 
     */
    private static UImageLoader mInstance = new UImageLoader();
    /**
     * 
     */
    private ExecutorService mImageThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime()
            .availableProcessors());
    /**
     * 
     */
    private Map<ImageView, String> mImageViewMap = Collections
            .synchronizedMap(new WeakHashMap<ImageView, String>());

    /**
     * 
     */
    private UImageLoader() {
        // 计算可使用的最大内存
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // 取四分之一的可用内存作为缓存
        final int cacheSize = maxMemory / 4;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

            // 获取图片的大小
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
    }

    /**
     * get the singleton
     * 
     * @return
     */
    public static UImageLoader getInstance() {
        return mInstance;
    }

    /**
     * @param path
     * @param mCallBack
     * @return
     */
    public Bitmap displayImage(ImageView imageView, final String path,
            final OnImageLoadListener mCallBack) {
        return this.displayImage(imageView, path, null, mCallBack);
    }

    /**
     * @param path
     * @param mPoint
     * @param mCallBack
     * @return
     */
    @SuppressLint("HandlerLeak")
    public Bitmap displayImage(final ImageView imageView, final String path, final Point mPoint,
            final OnImageLoadListener mCallBack) {
        // 从缓存中获取图片
        Bitmap bitmap = getBitmapFromMemCache(path);
        mImageViewMap.put(imageView, path);
        final Handler mHander = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mCallBack.onImageLoaded(imageView, (Bitmap) msg.obj, path);
            }

        };
        // the bitmap has not cache in the lrucache, and then decode it from
        // file
        if (bitmap == null) {
            mImageThreadPool.execute(new Runnable() {

                @Override
                public void run() {
                    //
                    Bitmap mBitmap = decodeThumbBitmapForFile(path,
                            mPoint == null ? 200 : mPoint.x,
                            mPoint == null ? 200 : mPoint.y);
                    Message msg = mHander.obtainMessage();
                    msg.obj = mBitmap;
                    mHander.sendMessage(msg);

                    //
                    addBitmapToMemoryCache(path, mBitmap);
                }
            });
        }
        return bitmap;

    }

    /**
     * 将图片存入缓存中
     * 
     * @param key
     * @param bitmap
     */
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null && bitmap != null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 从缓存中获取图片
     * 
     * @param key
     * @return
     */
    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * 从文件中加载Bitmap图片
     * 
     * @param path
     * @param viewWidth
     * @param viewHeight
     * @return
     */
    private Bitmap decodeThumbBitmapForFile(String path, int viewWidth, int viewHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = computeScale(options, viewWidth, viewHeight);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * @param options
     * @param width
     * @param height
     */
    private int computeScale(BitmapFactory.Options options, int viewWidth, int viewHeight) {
        int inSampleSize = 1;
        if (viewWidth == 0 || viewWidth == 0) {
            return inSampleSize;
        }
        int bitmapWidth = options.outWidth;
        int bitmapHeight = options.outHeight;

        // ����Bitmap�Ŀ�Ȼ�߶ȴ��������趨ͼƬ��View�Ŀ�ߣ���������ű���
        if (bitmapWidth > viewWidth || bitmapHeight > viewWidth) {
            int widthScale = Math.round((float) bitmapWidth / (float) viewWidth);
            int heightScale = Math.round((float) bitmapHeight / (float) viewWidth);

            // Ϊ�˱�֤ͼƬ�����ű��Σ�����ȡ��߱�����С���Ǹ�
            inSampleSize = widthScale < heightScale ? widthScale : heightScale;
        }
        return inSampleSize;
    }

    /**
     * ���ر���ͼƬ�Ļص��ӿ�
     * 
     * @author xiaanming
     */
    public interface OnImageLoadListener {
        /**
         * �����̼߳������˱��ص�ͼƬ����Bitmap��ͼƬ·���ص��ڴ˷�����
         * 
         * @param bitmap
         * @param path
         */
        public void onImageLoaded(ImageView imageView, Bitmap bitmap, String path);
    }
}
