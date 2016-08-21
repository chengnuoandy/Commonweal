package com.goldenratio.commonweal.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Help;
import com.goldenratio.commonweal.bean.Help_Top;

import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.datatype.BmobDate;

import static java.security.AccessController.getContext;


/**
 * Created by Kiuber on 2016-06-28.
 */
public class HelpListViewAdapter extends BaseAdapter implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    List<Help> mHelpList;

    public Context mContext;
    private SliderLayout mDemoSlider;


    public HelpListViewAdapter(Context context, List<Help> mHelp) {
        this.mContext = context;
        this.mHelpList = mHelp;
    }

    @Override
    public int getCount() {
        return mHelpList.size();
    }

    @Override
    public Help getItem(int position) {
        return mHelpList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (viewHolder == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_help_listview, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.initView(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.initData(position);
        return convertView;
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(mContext, slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();
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


    private class ViewHolder {

        private TextView mTvCity;
        private ImageView mIvPic;
        private TextView mTvTitle;
        private TextView mTvOneSentence;
        private TextView mTvCoin;
        private TextView mTvLeftDay;
        private TextView mTvLeftDayBottom;
        private ProgressBar mPbProgress;
        private int leftDay;


        public void initView(View view) {
            mTvCity = (TextView) view.findViewById(R.id.tv_city);
            mIvPic = (ImageView) view.findViewById(R.id.iv_pic);
            mTvTitle = (TextView) view.findViewById(R.id.tv_title);
            mTvOneSentence = (TextView) view.findViewById(R.id.tv_one_sentence);
            mTvCoin = (TextView) view.findViewById(R.id.tv_coin);
            mTvLeftDay = (TextView) view.findViewById(R.id.tv_left_day);
            mPbProgress = (ProgressBar) view.findViewById(R.id.pb_progress);
            mTvLeftDayBottom = (TextView) view.findViewById(R.id.tv_left_day_bottom);
        }

        private void initData(final int position) {
            mTvCity.setText(getItem(position).getHelp_SmilePro() + "" + getItem(position).getHelp_SmileCity());
            Glide.with(mContext).load(getItem(position).getHelp_Pic()).into(mIvPic);
            mTvTitle.setText(getItem(position).getHelp_Title());
            mTvOneSentence.setText(getItem(position).getHelp_OneSentence());
            mTvCoin.setText(getItem(position).getHelp_Coin());

            long endTime = BmobDate.getTimeStamp(getItem(position).getHelp_EndDate().getDate());
            long startTime = BmobDate.getTimeStamp(getItem(position).getHelp_StartDate().getDate());
            long leftTime = (endTime - System.currentTimeMillis()) / (86400000);
            if (leftTime > 0) {
                mTvLeftDay.setText(leftTime + "");
            } else {
                mTvLeftDay.setText("已结束");
                mTvLeftDay.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                mTvLeftDayBottom.setVisibility(View.GONE);
            }

            String allTime = ((endTime - startTime) / (86400000)) + "";

            String leftResult = mTvLeftDay.getText().toString();

            if (leftResult == "已结束") {
                mPbProgress.setProgress(100);
            } else {
                leftDay = Integer.parseInt(leftResult);
            }

            int allDay = Integer.parseInt(allTime);
            int usedDay = allDay - leftDay;
            Log.d("111111111111111111111", "position-->" + position + "leftDay--> " + leftDay + "allDay-->" + allDay + "usedDay-->" + usedDay);
            mPbProgress.setMax(allDay);
            mPbProgress.setProgress(usedDay);
        }

    }

    private void initSliderLayout(List<Help_Top> list, View view) {
        mDemoSlider = (SliderLayout) view.findViewById(R.id.slider);
        HashMap<String, String> urlMaps = new HashMap<>();

        for (int i = 0; i < list.size(); i++) {
            urlMaps.put(list.get(i).getHelp_Top_Title(), list.get(i).getHelp_Top_Pic());

            TextSliderView textSliderView = new TextSliderView(mContext);
            // initialize a SliderLayout
            textSliderView
                    .description(list.get(i).getHelp_Top_Title())
                    .image(urlMaps.get(list.get(i).getHelp_Top_Title()))
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
    }

}
