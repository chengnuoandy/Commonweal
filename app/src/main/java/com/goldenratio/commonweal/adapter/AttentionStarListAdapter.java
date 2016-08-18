package com.goldenratio.commonweal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.U_Attention;
import com.goldenratio.commonweal.bean.User_Profile;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 龙啸天 - Jxfen on 2016/8/18.
 * Email:jxfengmtx@163.com
 */
public class AttentionStarListAdapter extends BaseAdapter {

    private List<U_Attention> mStarInfoList;
    private LayoutInflater mInflater;
    private Context mContext;


    public AttentionStarListAdapter(List<U_Attention> starInfoList, Context context) {
        mStarInfoList = starInfoList;
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return mStarInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mStarInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.view_attention_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        User_Profile starInfo = mStarInfoList.get(position).getStar_Info();
        Picasso.with(mContext).load(starInfo.getUser_image_hd()).into(viewHolder.mCivAttentionAvatar);
        viewHolder.mTvAttentionName.setText(starInfo.getUser_Nickname());
        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.civ_attention_avatar)
        CircleImageView mCivAttentionAvatar;
        @BindView(R.id.tv_attention_name)
        TextView mTvAttentionName;
        @BindView(R.id.rl_attention)
        RelativeLayout mRlAttention;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
