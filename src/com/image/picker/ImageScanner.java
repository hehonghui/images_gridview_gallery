/**
 *
 *	created by Mr.Simple, Aug 25, 20145:32:52 PM.
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

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * 本地图片扫描类
 * 
 * @author mrsimple
 */
public class ImageScanner extends AsyncTask<Void, Void, List<String>> {

    List<String> mImages = new ArrayList<String>();

    ImageCallback mCallback;

    Context mContext;

    public ImageScanner(Context context) {
        mContext = context;
    }

    /**
     * @param callback
     * @return
     */
    public ImageScanner setCallback(ImageCallback callback) {
        this.mCallback = callback;
        return this;
    }

    @Override
    protected void onPreExecute() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(mContext, "no sdcard", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    protected List<String> doInBackground(Void... params) {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = mContext.getContentResolver();
        final String[] columns = {
                MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID
        };
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;

        // 查询
        Cursor mCursor = mContentResolver.query(mImageUri, columns,
                MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[] {
                        "image/jpeg", "image/png"
                }, orderBy + " DESC");

        while (mCursor.moveToNext()) {
            String path = mCursor.getString(mCursor
                    .getColumnIndex(MediaStore.Images.Media.DATA));
            if (!TextUtils.isEmpty(path)) {
                mImages.add(path);
            }
        }

        mCursor.close();
        return mImages;
    }

    @Override
    protected void onPostExecute(List<String> result) {
        if (mCallback != null) {
            mCallback.onComplete(mImages);
        }
    }

    /**
     * @author mrsimple
     */
    static interface ImageCallback {
        public void onComplete(List<String> imgLists);
    }

}
