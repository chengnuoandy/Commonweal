package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.goldenratio.commonweal.R;

import java.util.List;

public class DynamicPhotoShow extends BaseActivity {

    private ViewPager mPager;
    private TextView mText;
    private int index=0;
    private List<String> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_photo_show);

        Intent intent = getIntent();
        index = intent.getIntExtra("index", 0);
        mList = intent.getStringArrayListExtra("list");

        mPager = (ViewPager) findViewById(R.id.pager);
        mText = (TextView) findViewById(R.id.indicator);
        //设置图片浏览模式
        mPager.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));
        mPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return mList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                //PhotoView 本身也是个 ImageView
                PhotoView view = new PhotoView(DynamicPhotoShow.this);
                view.enable();
                view.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Glide.with(DynamicPhotoShow.this)
                        .load(mList.get(position))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(view);
                container.addView(view);
                //点击图片关闭
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        //设置切换动画
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
        mPager.setCurrentItem(index);

        //设置页码
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                CharSequence text = getString(R.string.viewpager_indicator, position + 1, mPager.getAdapter().getCount());
                mText.setText(text);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
