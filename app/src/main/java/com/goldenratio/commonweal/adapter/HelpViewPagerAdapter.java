package com.goldenratio.commonweal.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldenratio.commonweal.R;

import java.util.ArrayList;
import java.util.logging.Handler;

/**
 * Created by 两个人 on 2016-06-14.
 */
public class HelpViewPagerAdapter extends PagerAdapter {

    private ArrayList<ImageView> mImageViewList;
    private Context mContext;

    private  int[] mImageIds = new int[]{R.mipmap.hander_one
                ,R.mipmap.hander_two,R.mipmap.hander_three};
   public HelpViewPagerAdapter(Context context) {
       super();
       mContext = context;


     mImageViewList = new ArrayList<ImageView>();

       for (int i = 0; i < mImageIds.length; i++) {
           ImageView view = new ImageView(mContext);
           view.setBackgroundResource(mImageIds[i]);// 通过设置背景,可以让宽高填充布局
           // view.setImageResource(resId)
           mImageViewList.add(view);
       }
   }


    public int getCount() {
            return mImageViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }
        //初始化布局
        @Override
        public Object instantiateItem(ViewGroup container, int position) {

           ImageView view = mImageViewList.get(position);
            container.addView(view);
            return view;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

