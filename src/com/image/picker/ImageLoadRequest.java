/**
 * ImageRequest.java
 * ImageChooser
 * 
 * Created by likebamboo on 2014-4-25
 * Copyright (c) 1998-2014 http://likebamboo.github.io/ All rights reserved.
 */

package com.image.picker;

import android.graphics.Point;

/**
 * 本地图片请求封装
 * 
 * @author likebamboo
 */
public class ImageLoadRequest {
    /**
     * 图片路径
     */
    private String mPath = "";

    /**
     * 图片size
     */
    private Point mSize = null;

    /**
     * 加载图片后回调
     */
    private CommonImageLoader.ImageCallBack mCallBack = null;

    public ImageLoadRequest(String mPath, Point mSize, CommonImageLoader.ImageCallBack mCallBack) {
        this.mPath = mPath;
        this.mSize = mSize;
        this.mCallBack = mCallBack;
    }

    public String getPath() {
        return mPath;
    }

    public Point getSize() {
        return mSize;
    }

    public void setSize(Point size) {
        this.mSize = size;
    }

    public CommonImageLoader.ImageCallBack getCallBack() {
        return mCallBack;
    }

    public void setCallBack(CommonImageLoader.ImageCallBack callBack) {
        this.mCallBack = callBack;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ImageLoadRequest other = (ImageLoadRequest) obj;
        if (this.mPath.equals(other.mPath)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "ImageLoadRequest [mPath=" + mPath + ", mSize=" + mSize;
    }

}
