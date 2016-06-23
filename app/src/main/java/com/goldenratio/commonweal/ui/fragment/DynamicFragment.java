package com.goldenratio.commonweal.ui.fragment;

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
import com.goldenratio.commonweal.adapter.MyDynFragmentPagerAdapter;
import com.goldenratio.commonweal.ui.fragment.Dynamic.HelpDynamicFragment;
import com.goldenratio.commonweal.ui.fragment.Dynamic.OfficialDynamicFragment;
import com.goldenratio.commonweal.ui.fragment.Dynamic.PersonalDynamicFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DynamicFragment extends Fragment implements ViewPager.OnPageChangeListener {

    @BindView(R.id.tv_official)
    TextView mTvOfficial;
    @BindView(R.id.tv_dynamic)
    TextView mTvDynamic;
    @BindView(R.id.tv_personal)
    TextView mTvPersonal;
    @BindView(R.id.vp_dynamic)
    ViewPager mVpDynamic;
    @BindView(R.id.iv_tab_line)
    ImageView mTabLineIv;
    @BindView(R.id.id_switch_tab_ll)
    LinearLayout mSwitchTabLl;

    private List<Fragment> mFragmentList;

    /**
     * ViewPager的当前选中页
     */
    private int currentIndex;
    /**
     * 屏幕宽度
     */
    private int screenWidth;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_dynamic, null);

        ButterKnife.bind(this, view);

        mFragmentList = new ArrayList<Fragment>();
        mFragmentList.add(new OfficialDynamicFragment());
        mFragmentList.add(new HelpDynamicFragment());
        mFragmentList.add(new PersonalDynamicFragment());

    /*    mPtsTitle = (PagerTabStrip) view.findViewById(R.id.pts_title);
        mPtsTitle.setDrawFullUnderline(false);
        mPtsTitle.setTabIndicatorColor(Color.RED);*/

        initTabLineWidth();
        //screenWidth = 1000;
        mVpDynamic.setAdapter(new MyDynFragmentPagerAdapter(getFragmentManager(), mFragmentList));
        mVpDynamic.addOnPageChangeListener(this);
        return view;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        /**
         * 利用currentIndex(当前所在页面)和position(下一个页面)以及offset来
         * 设置mTabLineIv的左边距 滑动场景：
         * 记3个页面,
         * 从左到右分别为0,1,2
         * 0->1; 1->2; 2->1; 1->0
         */

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
                .getLayoutParams();

        Log.e("positionOffset:", positionOffset + "");

        Log.e("11221", "onPageScrolled: " + currentIndex + ",," + position);
        if (currentIndex == 0 && position == 0)// 0->1
        {
            lp.leftMargin = (int) (positionOffset * (screenWidth * 1.0 / 3) + currentIndex
                    * (screenWidth / 3));
            Log.i("由0---1", lp.leftMargin + "");

        } else if (currentIndex == 1 && position == 0) // 1->0
        {
            lp.leftMargin = (int) (-(1 - positionOffset)
                    * (screenWidth * 1.0 / 3) + currentIndex
                    * (screenWidth / 3));
            Log.i("由1---0", lp.leftMargin + "");

        } else if (currentIndex == 1 && position == 1) // 1->2
        {
            lp.leftMargin = (int) (positionOffset * (screenWidth * 1.0 / 3) + currentIndex
                    * (screenWidth / 3));
            Log.i("由1---2", lp.leftMargin + "");
        } else if (currentIndex == 2 && position == 1) // 2->1
        {
            lp.leftMargin = (int) (-(1 - positionOffset)
                    * (screenWidth * 1.0 / 3) + currentIndex
                    * (screenWidth / 3));
            Log.i("由1---2", lp.leftMargin + "");
        }
        Log.i("margin", "onPageScrolled: " + lp.leftMargin);
        mTabLineIv.setLayoutParams(lp);
    }

    @Override
    public void onPageSelected(int position) {
        currentIndex = position;
    }

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

    private void initTabLineWidth() {
      /*  DisplayMetrics dpMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay()
                .getMetrics(dpMetrics);*/
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
                .getLayoutParams();
        lp.width = screenWidth / 3;
        Log.i("width", lp.width + "");
        mTabLineIv.setLayoutParams(lp);
    }

    //改变标题颜色
    private void changeTitleColor(int color1, int color2, int color3) {
        mTvOfficial.setTextColor(getResources().getColor(color1));
        mTvDynamic.setTextColor(getResources().getColor(color2));
        mTvPersonal.setTextColor(getResources().getColor(color3));
    }
}
