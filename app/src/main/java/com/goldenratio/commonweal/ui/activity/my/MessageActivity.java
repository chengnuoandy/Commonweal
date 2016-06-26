package com.goldenratio.commonweal.ui.activity.my;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.MyFragmentPagerAdapter;
import com.goldenratio.commonweal.ui.fragment.MessageDynamicFragment;
import com.goldenratio.commonweal.ui.fragment.PrivateLetterFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MessageActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {

    @BindView(R.id.tv_dynamicMessage)
    TextView mTvDynamicMessage;
    @BindView(R.id.tv_privateLetter)
    TextView mTvPrivateLetter;
    @BindView(R.id.iv_message_tab)
    ImageView mIvMessageTab;
    @BindView(R.id.vp_my_message)
    ViewPager mVpMessage;

    private FragmentPagerAdapter mFragmentPagerAdapter;

    private List<Fragment> mFragmentList;

    /**
     * ViewPager的当前选中页
     */
    private int currentIndex;
    /**
     * 屏幕宽度
     */
    private int screenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);

        mFragmentList = new ArrayList<Fragment>();
        mFragmentList.add(new MessageDynamicFragment());
        mFragmentList.add(new PrivateLetterFragment());

        mFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList);

        initTabLineWidth();
        mVpMessage.setAdapter(mFragmentPagerAdapter);
        mVpMessage.addOnPageChangeListener(this);

    }

    @OnClick({R.id.tv_dynamicMessage, R.id.tv_privateLetter})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_dynamicMessage:
                mVpMessage.setCurrentItem(0);
                break;
            case R.id.tv_privateLetter:
                mVpMessage.setCurrentItem(1);
                break;

        }
    }

    /**
     * 页面滑动时调用此方法---注意：当页面滑动时会一直调用（循环）
     *
     * @param position             当前页面，即你点击滑动的页面
     * @param positionOffset       当前页面偏移的百分比
     * @param positionOffsetPixels 当前页面偏移的像素位置
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        /**
         * 由左边距来控制导航条移动
         * 利用currentIndex(上一页面)和position(本页面)以及offset来
         * 设置mTabLineIv的左边距 滑动场景：
         * 记3个页面,  计算公式：左边距 = positionOffset*导航条宽度+上一页面位置*导航条宽度
         * 从左到右分别为0,1,2
         * 0->1; 1->2; 2->1; 1->0
         */
        LinearLayout.LayoutParams mLp = (LinearLayout.LayoutParams) mIvMessageTab
                .getLayoutParams();

        Log.i("positionOffset:", positionOffset + "");

        Log.i("leftMargin", mLp.leftMargin + "");
        if (currentIndex == 0 && position == 0)// 0->1
        {
            mLp.leftMargin = (int) (positionOffset * (screenWidth * 1.0 / 2) + currentIndex
                    * (screenWidth / 2) + (screenWidth / 6) + 1);
            Log.i("由0---1", mLp.leftMargin + "");

        } else if (currentIndex == 1 && position == 0) // 1->0
        {
            mLp.leftMargin = (int) (-(1 - positionOffset) * (screenWidth * 1.0 / 2) + currentIndex
                    * (screenWidth / 2) + (screenWidth / 6) + 1);
            Log.i("由1---0", mLp.leftMargin + "");

        }
        Log.i("leftMargin_a", mLp.leftMargin + "");
        mIvMessageTab.setLayoutParams(mLp);
    }


    /**
     * 此方法是页面跳转完后得到调用
     *
     * @param position 当前选中的页面的位置编号
     */
    @Override
    public void onPageSelected(int position) {
        currentIndex = position;
    }


    /**
     * 当页面改变状态后调用
     * state滑动中的状态 有三种状态（0，1，2） 1：正在滑动 2：滑动完毕 0：什么都没做。
     */
    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_SETTLING) {
            switch (mVpMessage.getCurrentItem()) {
                case 0:
                    changeTitleColor(R.color.colorPrimary, R.color.ordinary);
                    break;
                case 1:
                    changeTitleColor(R.color.ordinary, R.color.colorPrimary);
                    break;
            }
        }
    }


    /**
     * 初始化导航条宽度（由屏幕大小计算出）
     */
    private void initTabLineWidth() {
      /* DisplayMetrics dpMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay()
                .getMetrics(dpMetrics);  */
        //获取屏幕大小像素值
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        Log.i("width", screenWidth + "");
        LinearLayout.LayoutParams mLp = (LinearLayout.LayoutParams) mIvMessageTab
                .getLayoutParams();
        mLp.width = screenWidth / 6;
        Log.i("leftMargin0", mLp.leftMargin + "");
        mIvMessageTab.setLayoutParams(mLp);
    }

    //改变标题颜色
    private void changeTitleColor(int color1, int color2) {
        mTvDynamicMessage.setTextColor(getResources().getColor(color1));
        mTvPrivateLetter.setTextColor(getResources().getColor(color2));
    }
}
