/**
 *
 *	created by Mr.Simple, Aug 25, 20145:29:22 PM.
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.example.imagepicker.R;
import com.image.picker.SImageView.OnMeasureListener;
import com.image.picker.UImageLoader.OnImageLoadListener;

import java.util.ArrayList;
import java.util.List;

public class PickerActivity extends FragmentActivity {
    FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_main);
        mFragmentManager = getSupportFragmentManager();

        mFragmentManager.beginTransaction().add(R.id.container, new FragmentImages()).commit();
    }

    /**
     * @author mrsimple
     */
    public static class FragmentImages extends Fragment {
        GridView mGridView;
        List<String> mImages = new ArrayList<String>();

        private Handler mHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 123) {
                    Log.d("", "#### sfss 222");
                    mGridView.setAdapter(new ImageAdapter(getActivity(), mGridView, mImages));
                }
            };
        };

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, null);
            mGridView = (GridView) rootView.findViewById(R.id.gridview);
            scanImages();
            return rootView;
        }

        private void scanImages() {
            new Thread() {

                @Override
                public void run() {
                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        Toast.makeText(getActivity(), "no sdcard", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    ContentResolver mContentResolver = getActivity().getContentResolver();
                    final String[] columns = {
                            MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID
                    };
                    final String orderBy = MediaStore.Images.Media.DATE_TAKEN;

                    // ֻ��ѯjpeg��png��ͼƬ
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
                    Log.d("", "#### ggggggg");

                    Message msg = mHandler.obtainMessage(123);
                    msg.what = 123;
                    mHandler.sendMessage(msg);
                }
            }.start();
        }

    }

    static class ImageAdapter extends BaseAdapter {
        Point mImageViewSize = new Point();
        List<String> mImagesList = new ArrayList<String>();
        LayoutInflater mInflater;
        GridView mGridView;
        List<String> mSelectedImagesList = new ArrayList<String>();
        SparseBooleanArray mCheckBoxList = new SparseBooleanArray();
        ImageLoader loader;

        public ImageAdapter(Context context, GridView gv, List<String> imgList) {
            mImagesList.addAll(imgList);
            Log.d("", "### 图片数量 : " + mImagesList.size());
            mGridView = gv;
            mInflater = LayoutInflater.from(context);
            BitmapManager.INSTANCE.setPlaceholder(BitmapFactory.decodeResource(
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
                viewHolder.imageView = (SImageView) convertView.findViewById(R.id.imageview);
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

            Log.d("", "**** getView, position = " + position + ", image path = " + imagePath);
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

            // final SImageView mImageView = viewHolder.imageView;
            Log.d("", "### find image view " + mGridView.findViewWithTag(imagePath));
            // UImageLoader.getInstance().displayImage(viewHolder.imageView,
            // imagePath,
            // mImageViewSize,
            // new OnImageLoadListener() {
            //
            // @Override
            // public void onImageLoaded(ImageView imageView, Bitmap bitmap,
            // String path) {
            //
            // ImageView imageViewByTag = (ImageView)
            // mGridView.findViewWithTag(path);
            // if (imageViewByTag != null &&
            // imageViewByTag.getTag().equals(path)
            // && bitmap != null) {
            // imageViewByTag.setImageBitmap(bitmap);
            // } else if (imageViewByTag != null) {
            // imageViewByTag.setImageResource(R.drawable.ic_launcher);
            // }
            // }
            // });
            BitmapManager.INSTANCE.loadBitmap(imagePath, viewHolder.imageView, mImageViewSize.x,
                    mImageViewSize.y);

            return convertView;
        }

        /**
         * @param options
         * @param reqWidth
         * @param reqHeight
         * @return
         */
        public static int calculateInSampleSize(
                BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // Calculate the largest inSampleSize value that is a power of 2
                // and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) > reqHeight
                        && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            }

            return inSampleSize;
        }

        static class ViewHolder {
            SImageView imageView;
            CheckBox checkBox;
        }
    }
}
