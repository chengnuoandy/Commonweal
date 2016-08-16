package com.goldenratio.commonweal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Help;

import java.util.List;

/**
 * Created by 冰封承諾Andy on 2016/8/15 0015.
 * 发起者详情页面展示项目列表的适配器
 */
public class HelpInitiatorListAdapter extends BaseAdapter {

    private List<Help> mList;
    private Context mContext;

    public HelpInitiatorListAdapter(Context context, List<Help> list){
        mList = list;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_help_initiator_item,null);
            viewHolder.initView(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.initData(position);

        return convertView;
    }


    private class ViewHolder{

        private ImageView mIvPic;
        private TextView mTvTitle;
        private TextView mTvText;

        private void initView(View view){
            mIvPic = (ImageView) view.findViewById(R.id.iv_pic);
            mTvTitle = (TextView) view.findViewById(R.id.tv_title);
            mTvText = (TextView) view.findViewById(R.id.tv_text);
        }

        private void initData(int position) {
            mTvTitle.setText(mList.get(position).getHelp_Title());
            mTvText.setText(mList.get(position).getHelp_Result());
            Glide.with(mContext).load(mList.get(position).getHelp_Pic())
                    .into(mIvPic);
        }
    }
}
