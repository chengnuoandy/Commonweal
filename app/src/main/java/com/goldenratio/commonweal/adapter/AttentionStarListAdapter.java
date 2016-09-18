package com.goldenratio.commonweal.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.U_Attention;
import com.goldenratio.commonweal.bean.User_Profile;
import com.goldenratio.commonweal.ui.activity.StarInfoActivity;
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

    private List<U_Attention> mInfoList;
    private LayoutInflater mInflater;
    private Context mContext;

    private boolean isAttention;

    public AttentionStarListAdapter(List<U_Attention> InfoList, boolean attention, Context context) {
        mInfoList = InfoList;
        mContext = context;
        isAttention = attention;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.view_attention_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        User_Profile starOrUserInfo = (isAttention ? (mInfoList.get(position).getStar_Info()) :
                mInfoList.get(position).getUser_Info());
        Picasso.with(mContext).load(starOrUserInfo.getUser_image_hd()).into(viewHolder.mCivAttentionAvatar);
        viewHolder.mTvAttentionName.setText(starOrUserInfo.getUser_Nickname());
        if (starOrUserInfo.isUser_IsV())
            viewHolder.mIvStarFlag.setVisibility(View.VISIBLE);
        viewHolder.mCivAttentionAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication myApplication = (MyApplication) ((Activity) mContext).getApplication();
                String mStrObjectId = myApplication.getObjectID();
                if (!mInfoList.get(position).getStar_Info().getObjectId().equals(mStrObjectId)) {
                    List<String> attenList;
                    attenList = mInfoList.get(position).getStar_Info().getUser_Attention();
                    int isHas = -1;
                    if (attenList != null)
                        isHas = attenList.indexOf(mInfoList.get(position).getStar_Info().getObjectId());
                    Intent intent = new Intent(mContext, StarInfoActivity.class);
                    intent.putExtra("ishas", isHas != -1);
                    intent.putExtra("id", mInfoList.get(position).getStar_Info().getObjectId());
                    intent.putExtra("autograph", mInfoList.get(position).getStar_Info().getUser_Autograph());
                    intent.putExtra("nickName", mInfoList.get(position).getStar_Info().getUser_Nickname());
                    intent.putExtra("isv", mInfoList.get(position).getStar_Info().isUser_IsV());
                    intent.putExtra("Avatar", mInfoList.get(position).getStar_Info().getUser_image_hd());
                    mContext.startActivity(intent);
                } else {
                    Toast.makeText(myApplication, "不要再点了，这里真的什么都没有~", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.civ_attention_avatar)
        CircleImageView mCivAttentionAvatar;
        @BindView(R.id.tv_attention_name)
        TextView mTvAttentionName;
        @BindView(R.id.rl_attention)
        RelativeLayout mRlAttention;
        @BindView(R.id.iv_star_flag)
        ImageView mIvStarFlag;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
