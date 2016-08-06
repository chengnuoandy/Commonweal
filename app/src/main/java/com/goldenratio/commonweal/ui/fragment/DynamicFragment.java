package com.goldenratio.commonweal.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.MyFragmentPagerAdapter;
import com.goldenratio.commonweal.ui.activity.DynamicRelease;
import com.goldenratio.commonweal.ui.fragment.dynamic.HelpDynamicFragment;
import com.goldenratio.commonweal.ui.fragment.dynamic.OfficialDynamicFragment;
import com.goldenratio.commonweal.ui.fragment.dynamic.PersonalDynamicFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 龙啸天 on 2016/6/21 0021.
 */
public class DynamicFragment extends Fragment implements ViewPager.OnPageChangeListener {

    @BindView(R.id.tv_official)
    TextView mTvOfficial;
    @BindView(R.id.tv_Help)
    TextView mTvDynamic;
    @BindView(R.id.tv_personal)
    TextView mTvPersonal;
    @BindView(R.id.vp_dynamic)
    ViewPager mVpDynamic;
    @BindView(R.id.iv_tab_line)
    ImageView mTabLineIv;
    @BindView(R.id.id_switch_tab_ll)
    LinearLayout mSwitchTabLl;
    @BindView(R.id.iv_add_dynamic)
    ImageView mIvAddDynamic;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dynamic, null);

        ButterKnife.bind(this, view);

        mFragmentList = new ArrayList<Fragment>();
        mFragmentList.add(new OfficialDynamicFragment());
        mFragmentList.add(new HelpDynamicFragment());
        mFragmentList.add(new PersonalDynamicFragment());

        initTabLineWidth();
        mVpDynamic.setAdapter(new MyFragmentPagerAdapter(getFragmentManager(), mFragmentList));
        mVpDynamic.addOnPageChangeListener(this);
        return view;
    }

    @OnClick({R.id.tv_official, R.id.tv_Help, R.id.tv_personal,R.id.iv_add_dynamic})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_official:
                mVpDynamic.setCurrentItem(0);
                break;
            case R.id.tv_Help:
                mVpDynamic.setCurrentItem(1);
                break;
            case R.id.tv_personal:
                mVpDynamic.setCurrentItem(2);
                break;
            case R.id.iv_add_dynamic:
                Intent intent = new Intent(getContext(), DynamicRelease.class);
                startActivity(intent);
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
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
                .getLayoutParams();

        Log.e("positionOffset:", positionOffset + "");

        if (currentIndex == 0 && position == 0)// 0->1
        {
            lp.leftMargin = (int) (positionOffset * (screenWidth * 1.0 / 3) + currentIndex
                    * (screenWidth / 3) + (screenWidth / 12) + 1);
            Log.i("由0---1", lp.leftMargin + "");

        } else if (currentIndex == 1 && position == 0) // 1->0
        {
            lp.leftMargin = (int) (-(1 - positionOffset)
                    * (screenWidth * 1.0 / 3) + currentIndex
                    * (screenWidth / 3) + (screenWidth / 12) + 1);
            Log.i("由1---0", lp.leftMargin + "");

        } else if (currentIndex == 1 && position == 1) // 1->2
        {
            lp.leftMargin = (int) (positionOffset * (screenWidth * 1.0 / 3) + currentIndex
                    * (screenWidth / 3) + (screenWidth / 12) + 3);
            Log.i("由1---2", lp.leftMargin + "");
        } else if (currentIndex == 2 && position == 1) // 2->1
        {
            lp.leftMargin = (int) (-(1 - positionOffset)
                    * (screenWidth * 1.0 / 3) + currentIndex
                    * (screenWidth / 3) + (screenWidth / 12) + 3);
            Log.i("由2---1", lp.leftMargin + "");
        }
        Log.i("margin", "onPageScrolled: " + lp.leftMargin);
        mTabLineIv.setLayoutParams(lp);
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
            switch (mVpDynamic.getCurrentItem()) {
                case 0:
                    changeTitleColor(R.color.colorPrimary, R.color.ordinary, R.color.ordinary);
                    break;
                case 1:
                    changeTitleColor(R.color.ordinary, R.color.colorPrimary, R.color.ordinary);
                    break;
                case 2:
                    changeTitleColor(R.color.ordinary, R.color.ordinary, R.color.colorPrimary);
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
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
                .getLayoutParams();
        lp.width = screenWidth / 6;
        Log.i("进度条width", lp.width + "");
        mTabLineIv.setLayoutParams(lp);
    }

    //改变标题颜色
    private void changeTitleColor(int color1, int color2, int color3) {
        mTvOfficial.setTextColor(getResources().getColor(color1));
        mTvDynamic.setTextColor(getResources().getColor(color2));
        mTvPersonal.setTextColor(getResources().getColor(color3));
    }


    @OnClick(R.id.iv_add_dynamic)
    public void onClick() {
    }
}
