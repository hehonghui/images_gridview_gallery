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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.imagepicker.R;
import com.image.picker.ImageScanner.ImageCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mrsimple
 */
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

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, null);
            mGridView = (GridView) rootView.findViewById(R.id.gridview);
            new ImageScanner(getActivity()).setCallback(new ImageCallback() {

                @Override
                public void onComplete(List<String> imgLists) {
                    mGridView.setAdapter(new ImageAdapter(getActivity(), imgLists, mGridView));
                }
            }).execute();
            return rootView;
        }
    }
}
