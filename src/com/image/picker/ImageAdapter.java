/**
 *
 *	created by Mr.Simple, Aug 26, 20141:56:54 PM.
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

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.example.imagepicker.R;
import com.image.picker.SquareImageView.OnMeasureListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mrsimple
 */
public class ImageAdapter extends BaseAdapter {
    /**
     * 存储View的大小
     */
    Point mImageViewSize = new Point();
    /**
     * 图片列表
     */
    List<String> mImagesList = new ArrayList<String>();
    /**
     * 
     */
    LayoutInflater mInflater;
    /**
     * 已经选择的列表
     */
    List<String> mSelectedImagesList = new ArrayList<String>();
    /**
     * 已经选中的CheckBox
     */
    SparseBooleanArray mCheckBoxList = new SparseBooleanArray();

    /**
     * @param context
     * @param imgList
     */
    public ImageAdapter(Context context, List<String> imgList) {
        mImagesList.addAll(imgList);
        Log.d("", "### 图片数量 : " + mImagesList.size());
        mInflater = LayoutInflater.from(context);
        UImageLoader.INSTANCE.setPlaceholder(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.ic_launcher));
    }

    @Override
    public int getCount() {
        return mImagesList.size();
    }

    @Override
    public String getItem(int position) {
        return mImagesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (SquareImageView) convertView.findViewById(R.id.imageview);
            viewHolder.imageView.mListener = new OnMeasureListener() {

                @Override
                public void onMeasureDone(int width, int height) {
                    mImageViewSize.set(width, height);
                }
            };
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbok);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String imagePath = getItem(position);
        // 给imageview设置tag,tag为图片的地址,异步加载放置位置错乱
        viewHolder.imageView.setTag(imagePath);
        // checkbox
        viewHolder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mSelectedImagesList.add(imagePath);
                } else if (mSelectedImagesList.contains(imagePath)) {
                    mSelectedImagesList.remove(imagePath);
                }
                mCheckBoxList.put(position, isChecked);
                Log.d("", "#### selected : " + mSelectedImagesList.size());
            }
        });
        viewHolder.checkBox.setChecked(mCheckBoxList.get(position));

        //
        UImageLoader.INSTANCE.displayBitmap(imagePath, viewHolder.imageView, mImageViewSize.x,
                mImageViewSize.y);

        return convertView;
    }

    /**
     * @author mrsimple
     */
    static class ViewHolder {
        SquareImageView imageView;
        CheckBox checkBox;
    }
}
