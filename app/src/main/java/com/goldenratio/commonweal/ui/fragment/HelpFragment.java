package com.goldenratio.commonweal.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.HelpListViewAdapter;
import com.goldenratio.commonweal.bean.Help;
import com.goldenratio.commonweal.bean.Help_Top;
import com.goldenratio.commonweal.ui.activity.HelpDetailActivity;
import com.goldenratio.commonweal.ui.activity.HelpTopDetailActivity;

import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class
HelpFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener
        , BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener, BGARefreshLayout.BGARefreshLayoutDelegate {

    private LinearLayout mLlNoNet;
    private List<Help> mHelp;
    private ListView mLv;
    private View view;
    private BGARefreshLayout mBGARefreshLayout;
    private View mTopView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_help, null);
        initView();
        initData();
        return view;
    }

    private void initView() {
        mLlNoNet = (LinearLayout) view.findViewById(R.id.ll_no_net);
        mLv = (ListView) view.findViewById(R.id.lv_help);
        mLv.setOnItemClickListener(this);

        mBGARefreshLayout = (BGARefreshLayout) view.findViewById(R.id.rl_BGA_refresh);
        // 为BGARefreshLayout设置代理
        mBGARefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(getContext(), false);
        // 设置下拉刷新和上拉加载更多的风格
        mBGARefreshLayout.setRefreshViewHolder(refreshViewHolder);
    }

    private void initData() {
        queryHelpData();
    }

    private void queryHelp_TopData() {
        BmobQuery<Help_Top> help_topBmobQuery = new BmobQuery<>();
        help_topBmobQuery.order("-createdAt");
        help_topBmobQuery.findObjects(new FindListener<Help_Top>() {
            @Override
            public void done(List<Help_Top> list, BmobException e) {
                if (e == null) {
                    if (list.size() != 0) {
                        //轮播有数据时才显示
                        mTopView = View.inflate(getContext(), R.layout.item_help_listview_top, null);
                        initSliderLayout(mTopView, list);
                    } else {
                    }
                    mLv.setAdapter(new HelpListViewAdapter(getContext(), mHelp));
                    hideLinearLayout();
                    //收起刷新
                    mBGARefreshLayout.endRefreshing();
                } else {
                    //收起刷新
                    mBGARefreshLayout.endRefreshing();
                    Log.d("Kiuber_LOG", "done: " + e.getMessage() + e.getErrorCode());
                    Toast.makeText(getContext(), e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void queryHelpData() {
        BmobQuery<Help> bmobQuery = new BmobQuery<>();
        bmobQuery.order("-createdAt");
        bmobQuery.findObjects(new FindListener<Help>() {
            @Override
            public void done(List<Help> list, BmobException e) {
                if (e == null) {
                    mHelp = list;
                    queryHelp_TopData();
                } else {
                    //收起刷新
                    mBGARefreshLayout.endRefreshing();
                    Toast.makeText(getContext(), e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initSliderLayout(View view, List<Help_Top> list) {
        SliderLayout mDemoSlider = (SliderLayout) view.findViewById(R.id.slider);

        for (int i = 0; i < list.size(); i++) {
            TextSliderView textSliderView = new TextSliderView(getContext());
            // initialize a SliderLayout
            textSliderView
                    .description(list.get(i).getHelp_Top_Title())
                    .image(list.get(i).getHelp_Top_Pic())
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", list.get(i).getHelp_Top_Url());

            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);
        mLv.addHeaderView(view);
    }


    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getContext(), HelpDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("HelpList", mHelp.get(position - 1));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void hideLinearLayout() {
        if (mLlNoNet.getVisibility() == View.VISIBLE) {
            mLlNoNet.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Intent intent = new Intent(getContext(), HelpTopDetailActivity.class);
        intent.putExtra("TopUrl", slider.getBundle().get("extra").toString());
        startActivity(intent);
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

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        //重新装填数据
        if (mHelp != null) {
            mHelp.clear();
            mLv.removeHeaderView(mTopView);
            initData();
        }else {
            mLv.removeHeaderView(mTopView);
            initData();
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }
}