package com.goldenratio.commonweal.ui.activity.my;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.MyFragmentPagerAdapter;
import com.goldenratio.commonweal.ui.fragment.MessageDynamicFragment;
import com.goldenratio.commonweal.ui.fragment.PrivateLetterFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);

        mFragmentList = new ArrayList<>();
        mFragmentList.add(new MessageDynamicFragment());
        mFragmentList.add(new PrivateLetterFragment());
        mFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList);
        mVpMessage.setCurrentItem(0);
        mVpMessage.addOnPageChangeListener(this);
        mVpMessage.setAdapter(mFragmentPagerAdapter);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
