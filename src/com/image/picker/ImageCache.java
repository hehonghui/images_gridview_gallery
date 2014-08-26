/**
 *
 *	created by Mr.Simple, Aug 26, 20141:27:39 PM.
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

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * 图片缓存
 * 
 * @author mrsimple
 */
public class ImageCache {
    /**
     * 
     */
    private LruCache<String, Bitmap> mMemoryCache;

    /**
     * 
     */
    public ImageCache() {
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
     * @param key
     * @return
     */
    public synchronized Bitmap get(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * @param key
     * @return
     */
    public void put(String key, Bitmap bitmap) {

        if (mMemoryCache.get(key) == null && bitmap != null) {
            synchronized (mMemoryCache) {
                mMemoryCache.put(key, bitmap);
            }
        }
    }

}
