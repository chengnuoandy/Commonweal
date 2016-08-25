package com.goldenratio.commonweal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Donate_Info;
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
public class DonateInfoListAdapter extends BaseAdapter {

    private List<Donate_Info> mInfoList;
    private LayoutInflater mInflater;
    private Context mContext;


    public DonateInfoListAdapter(List<Donate_Info> infoList, Context context) {
        mInfoList = infoList;
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.view_donate_info_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        User_Profile user_profile = mInfoList.get(position).getUser_Info();
        Picasso.with(mContext).load(user_profile.getUser_image_hd()).into(viewHolder.mCivDonateAvatar);
        viewHolder.mTvDonateName.setText(user_profile.getUser_Nickname());
        viewHolder.mTvDonateCoin.setText("捐赠" + mInfoList.get(position).getDonate_Coin() + "公益币");
        viewHolder.mTvDonateRanking.setText(String.valueOf("第" + (position + 1) + "名"));

        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.civ_donate_avatar)
        CircleImageView mCivDonateAvatar;
        @BindView(R.id.tv_donate_name)
        TextView mTvDonateName;
        @BindView(R.id.tv_donate_coin)
        TextView mTvDonateCoin;
        @BindView(R.id.tv_donate_ranking)
        TextView mTvDonateRanking;
        @BindView(R.id.rl_donate)
        RelativeLayout mRlDonate;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
