package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.goldenratio.commonweal.R;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends BaseActivity {

    /**
     * ViewPager中ImageView的容器
     */
    private List<ImageView> imageViewContainer = null;

    /**
     * 上一个被选中的小圆点的索引，默认值为0
     */
    private int preDotPosition = 0;

    private ViewPager viewPager;

    /**
     * 小圆点的父控件
     */
    private LinearLayout llDotGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        initView();
    }


    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        llDotGroup = (LinearLayout) findViewById(R.id.ll_dot_group);
        viewPager.setPageTransformer(true,new RotateDownPageTransformer());

        imageViewContainer = new ArrayList<ImageView>();
        int[] imageIDs = new int[]{
                R.mipmap.hander_one,
                R.mipmap.hander_two,
                R.mipmap.hander_three,
                R.mipmap.tab_my_test_wangwang,
        };

        ImageView imageView = null;
        View dot = null;
        LinearLayout.LayoutParams params = null;
        for (int id : imageIDs) {
            imageView = new ImageView(this);
            imageView.setBackgroundResource(id);
            imageViewContainer.add(imageView);

            // 每循环一次添加一个点到线行布局中
            dot = new View(this);
            dot.setBackgroundResource(R.drawable.dot_bg_selector);
            params = new LinearLayout.LayoutParams(15, 15);
            params.leftMargin = 10;
            dot.setEnabled(false);
            dot.setLayoutParams(params);
            llDotGroup.addView(dot); // 向线性布局中添加"点"
        }

        viewPager.setAdapter(new BannerAdapter());
        viewPager.addOnPageChangeListener(new BannerPageChangeListener());

        // 选中第一个图片
        llDotGroup.getChildAt(0).setEnabled(true);
        viewPager.setCurrentItem(0);
    }

    /**
     * ViewPager的适配器
     */
    private class BannerAdapter extends PagerAdapter {

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imageViewContainer.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = imageViewContainer.get(position);
            //为最后一直图片设置点击事件
            if (position == imageViewContainer.size() - 1) {
                view.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        finish();
                    }

                });
            }
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return imageViewContainer.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    /**
     * Banner的Page切换监听器
     */
    private class BannerPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // Nothing to do
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // Nothing to do
        }

        @Override
        public void onPageSelected(int position) {
            // 把上一个点设置为被选中
            llDotGroup.getChildAt(preDotPosition).setEnabled(false);
            // 根据索引设置那个点被选中
            llDotGroup.getChildAt(position).setEnabled(true);
            // 新索引赋值给上一个索引的位置
            preDotPosition = position;
        }

    }

    /**
     * VP的动画效果
     */
    public class RotateDownPageTransformer implements ViewPager.PageTransformer {

        private static final float ROT_MAX = 20.0f;
        private float mRot;


        public void transformPage(View view, float position) {
            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                ViewHelper.setRotation(view, 0);

            } else if (position <= 1) // a页滑动至b页 ； a页从 0.0 ~ -1 ；b页从1 ~ 0.0
            { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                if (position < 0) {

                    mRot = (ROT_MAX * position);
                    ViewHelper.setPivotX(view, view.getMeasuredWidth() * 0.5f);
                    ViewHelper.setPivotY(view, view.getMeasuredHeight());
                    ViewHelper.setRotation(view, mRot);
                } else {

                    mRot = (ROT_MAX * position);
                    ViewHelper.setPivotX(view, view.getMeasuredWidth() * 0.5f);
                    ViewHelper.setPivotY(view, view.getMeasuredHeight());
                    ViewHelper.setRotation(view, mRot);
                }

                // Scale the page down (between MIN_SCALE and 1)

                // Fade the page relative to its size.

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                ViewHelper.setRotation(view, 0);
            }
        }
    }

}
