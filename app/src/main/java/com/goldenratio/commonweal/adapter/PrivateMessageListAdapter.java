package com.goldenratio.commonweal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Message;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 龙啸天 - Jxfen on 2016/8/18.
 * Email:jxfengmtx@163.com
 */
public class PrivateMessageListAdapter extends BaseAdapter {

    private List<Message> mMessageList;
    private LayoutInflater mInflater;
    private Context mContext;


    public PrivateMessageListAdapter(List<Message> messageList, Context context) {
        mMessageList = messageList;
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.view_message_private_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Message message = mMessageList.get(position);
//        Picasso.with(mContext).load(user_profile.getUser_image_hd()).into(viewHolder.mCivDonateAvatar);
        viewHolder.mTvName.setText(message.getTitle());
        viewHolder.mTvMessage.setText(message.getContent());
        viewHolder.mTvDate.setText(message.getUpdatedAt());

        return convertView;
    }

    class ViewHolder {
        /*        @BindView(R.id.civ_donate_avatar)
                CircleImageView mCivDonateAvatar;*/
        @BindView(R.id.tv_name)
        TextView mTvName;
        @BindView(R.id.tv_message)
        TextView mTvMessage;
        @BindView(R.id.tv_date)
        TextView mTvDate;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
