/**
 *
 *	created by Mr.Simple, Aug 26, 20141:13:19 PM.
 *	Copyright (c) 2014, hehonghui@umeng.com All Rights Reserved.
 *
 *                #####################################################
 *                #                                                   #
 *                #                       _oo0oo_                     #   
 *                #                      o8888888o                    #
 *                #                      88" . "88                    #
 *                #                      (| -_- |)                    #
 *                #                      0\  =  /0                    #   
 *                #                    ___/`---'\___                  #
 *                #                  .' \\|     |# '.                 #
 *                #                 / \\|||  :  |||# \                #
 *                #                / _||||| -:- |||||- \              #
 *                #               |   | \\\  -  #/ |   |              #
 *                #               | \_|  ''\---/''  |_/ |             #
 *                #               \  .-\__  '-'  ___/-. /             #
 *                #             ___'. .'  /--.--\  `. .'___           #
 *                #          ."" '<  `.___\_<|>_/___.' >' "".         #
 *                #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 *                #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 *                #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 *                #                       `=---='                     #
 *                #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 *                #                                                   #
 *                #               佛祖保佑         永无BUG              #
 *                #                                                   #
 *                #####################################################
 */

package com.image.picker;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author mrsimple
 */
public enum BitmapManager {
    INSTANCE;

    /**
     * 
     */
    private final Map<String, SoftReference<Bitmap>> cache;
    /**
     * 
     */
    private final ExecutorService pool;
    /**
     * 
     */
    private Map<ImageView, String> imageViews = Collections
            .synchronizedMap(new WeakHashMap<ImageView, String>());
    private Bitmap placeholder;

    /**
     * 
     */
    private BitmapManager() {
        cache = new HashMap<String, SoftReference<Bitmap>>();
        pool = Executors.newFixedThreadPool(5);
    }

    public void setPlaceholder(Bitmap bmp) {
        placeholder = bmp;
    }

    public Bitmap getBitmapFromCache(String url) {
        if (cache.containsKey(url)) {
            return cache.get(url).get();
        }

        return null;
    }

    @SuppressLint("HandlerLeak")
    public void queueJob(final String url, final ImageView imageView,
            final int width, final int height) {
        /* Create handler in UI thread. */
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String tag = imageViews.get(imageView);
                if (tag != null && tag.equals(url)) {
                    if (msg.obj != null) {
                        imageView.setImageBitmap((Bitmap) msg.obj);
                    } else {
                        imageView.setImageBitmap(placeholder);
                        Log.d(null, "fail " + url);
                    }
                }
            }
        };

        pool.submit(new Runnable() {
            @Override
            public void run() {
                Bitmap bmp = null;
                if (url.startsWith("http") || url.startsWith("https")) {
                    bmp = downloadBitmap(url, width, height);
                } else {
                    bmp = decodeBitmapFromFile(url, width, height);
                }
                Message message = Message.obtain();
                message.obj = bmp;
                Log.d(null, "Item downloaded: " + url);

                handler.sendMessage(message);
            }
        });
    }

    /**
     * @param url
     * @param imageView
     * @param width
     * @param height
     */
    public void loadBitmap(final String url, final ImageView imageView,
            final int width, final int height) {
        imageViews.put(imageView, url);
        Bitmap bitmap = getBitmapFromCache(url);

        // check in UI thread, so no concurrency issues
        if (bitmap != null) {
            Log.d(null, "Item loaded from cache: " + url);
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageBitmap(placeholder);
            queueJob(url, imageView, width, height);
        }
    }

    /**
     * 从文件中加载Bitmap图片
     * 
     * @param path
     * @param viewWidth
     * @param viewHeight
     * @return
     */
    private Bitmap decodeBitmapFromFile(String path, int viewWidth, int viewHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = computeSmallSize(options, viewWidth, viewHeight);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * @param options
     * @param width
     * @param height
     */
    private int computeSmallSize(BitmapFactory.Options options, int viewWidth, int viewHeight) {
        int inSampleSize = 1;
        if (viewWidth == 0 || viewWidth == 0) {
            return inSampleSize;
        }
        int bitmapWidth = options.outWidth;
        int bitmapHeight = options.outHeight;

        if (bitmapWidth > viewWidth || bitmapHeight > viewWidth) {
            int widthScale = Math.round((float) bitmapWidth / (float) viewWidth);
            int heightScale = Math.round((float) bitmapHeight / (float) viewWidth);

            inSampleSize = widthScale < heightScale ? widthScale : heightScale;
        }
        return inSampleSize;
    }

    /**
     * 从网络上下载图片
     * 
     * @param url
     * @param width
     * @param height
     * @return
     */
    private Bitmap downloadBitmap(String url, int width, int height) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(
                    url).getContent());
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            cache.put(url, new SoftReference<Bitmap>(bitmap));
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
