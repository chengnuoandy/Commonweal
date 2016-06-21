package com.goldenratio.commonweal.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private List<Fragment> mFragmentList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dynamic, null);

        mFragmentList = new ArrayList<Fragment>();
        mFragmentList.add(new OfficialDynamicFragment());
        mFragmentList.add(new HelpDynamicFragment());
        mFragmentList.add(new PersonalDynamicFragment());

    /*    mPtsTitle = (PagerTabStrip) view.findViewById(R.id.pts_title);
        mPtsTitle.setDrawFullUnderline(false);
        mPtsTitle.setTabIndicatorColor(Color.RED);*/
        ButterKnife.bind(this, view);
        mVpDynamic.setAdapter(new MyDynFragmentPagerAdapter(getFragmentManager(), mFragmentList));
        mVpDynamic.addOnPageChangeListener(this);
        return view;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

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

    //改变标题颜色
    private void changeTitleColor(int color1, int color2, int color3) {
        mTvOfficial.setTextColor(getResources().getColor(color1));
        mTvDynamic.setTextColor(getResources().getColor(color2));
        mTvPersonal.setTextColor(getResources().getColor(color3));
    }
}
