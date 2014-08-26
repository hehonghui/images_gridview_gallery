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
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ImageScanner extends Thread {

    List<String> mImages = new ArrayList<String>();

    ImageCallback mCallback;

    public static final int MSG = 123;
    Context mContext;
    Handler mHandler ;

    public ImageScanner(Context context , Handler handler) {
        mContext = context;
        mHandler = handler ;
    }

    public ImageScanner setCallback(ImageCallback callback) {
        mCallback = callback;
        return this;
    }

    @Override
    public void run() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(mContext, "no sdcard", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = mContext.getContentResolver();

        // ֻ��ѯjpeg��png��ͼƬ
        Cursor mCursor = mContentResolver.query(mImageUri, null,
                MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[] {
                        "image/jpeg", "image/png"
                }, MediaStore.Images.Media.DATE_MODIFIED);

        while (mCursor.moveToNext()) {
            // ��ȡͼƬ��·��
            String path = mCursor.getString(mCursor
                    .getColumnIndex(MediaStore.Images.Media.DATA));
            mImages.add(path);
        }
        mCursor.close();
        Log.d(getName(), "#### ggggggg") ;

        Message msg = mHandler.obtainMessage(MSG);
        msg.what = MSG ;
        msg.obj = mImages;
        mHandler.sendMessage(msg);
    }

    static interface ImageCallback {
        public void onComplete(List<String> imgLists);
    }

}
