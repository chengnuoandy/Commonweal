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
                    R.layout.view_help_item, parent, false);
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
        private TextView mTvType;
        private TextView mTvOneSentence;
        private TextView mTvDonateSum;
        private TextView mTvMoney;
        private TextView mTvLeftDay;
        private ProgressBar mPbProgress;

        public void initView(View view) {
            mTvCity = (TextView) view.findViewById(R.id.tv_city);
            mIvPic = (ImageView) view.findViewById(R.id.iv_pic);
            mTvTitle = (TextView) view.findViewById(R.id.tv_title);
            mTvType = (TextView) view.findViewById(R.id.tv_type);
            mTvOneSentence = (TextView) view.findViewById(R.id.tv_one_sentence);
            mTvDonateSum = (TextView) view.findViewById(R.id.tv_donate_sum);
            mTvMoney = (TextView) view.findViewById(R.id.tv_money);
            mTvLeftDay = (TextView) view.findViewById(R.id.tv_left_day);
            mPbProgress = (ProgressBar) view.findViewById(R.id.pb_progress);
        }

        private void initData(final int position) {
            mTvCity.setText(getItem(position).getHelp_SmilePro() + "" + getItem(position).getHelp_SmileCity());
            Glide.with(mContext).load(getItem(position).getHelp_Pic()).into(mIvPic);
            mTvTitle.setText(getItem(position).getHelp_Title());
            mTvType.setText(getItem(position).getHelp_Type());
            mTvOneSentence.setText(getItem(position).getHelp_OneSentence());
            mTvDonateSum.setText(getItem(position).getHelp_DonateSum());
            mTvMoney.setText(getItem(position).getHelp_Money());

            long endTime = BmobDate.getTimeStamp(getItem(position).getHelp_EndDate().getDate());
            long startTime = BmobDate.getTimeStamp(getItem(position).getHelp_StartDate().getDate());
            String LeftTime = (endTime - System.currentTimeMillis()) / (86400000) + "";
            mTvLeftDay.setText(LeftTime);
            String allTime = ((endTime - startTime) / (86400000)) + "";

            int leftDay = Integer.parseInt(mTvLeftDay.getText().toString());
            int allDay = Integer.parseInt(allTime);
            int usedDay = allDay - leftDay;
            Log.d("111111111111111111111", "position-->" + position + "leftDay--> " + leftDay + "allDay-->" + allDay + "usedDay-->" + usedDay);
            mPbProgress.setMax(allDay);
            mPbProgress.setProgress(usedDay);
        }
    }
}
