package com.goldenratio.commonweal.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Help;

import java.util.List;

import cn.bmob.v3.datatype.BmobDate;


/**
 * Created by Kiuber on 2016-06-28.
 */
public class HelpListViewAdapter extends BaseAdapter {

    List<Help> mHelpList;

    public Context mContext;

    public HelpListViewAdapter(Context mContext, List<Help> help) {
        this.mContext = mContext;
        this.mHelpList = help;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_help_listview, parent, false);
            viewHolder.initView(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.initData(position);
        return convertView;
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
            Glide.with(mContext).load(getItem(position)
                    .getHelp_Pic())
                    .into(mIvPic);
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
}
