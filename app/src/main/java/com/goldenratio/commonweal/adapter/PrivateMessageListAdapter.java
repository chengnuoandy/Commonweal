package com.goldenratio.commonweal.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goldenratio.commonweal.MyApplication;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Message;
import com.goldenratio.commonweal.bean.NotifyManager;
import com.goldenratio.commonweal.util.ErrorCodeUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.view_message_private_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Message message = mMessageList.get(position);
//        Picasso.with(mContext).load(user_profile.getUser_image_hd()).into(viewHolder.mCivDonateAvatar);
        viewHolder.mTvName.setText(message.getTitle());
        viewHolder.mTvMessage.setText(message.getContent());
        viewHolder.mTvDate.setText(message.getUpdatedAt());

        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.mRlNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryNotifyFromBmob(position);
                showNotify(message.getContent());
            }
        });
        return convertView;
    }

    private void queryNotifyFromBmob(final int position) {
        BmobQuery<NotifyManager> user_profileBmobQuery = new BmobQuery<>();
        Message message = mMessageList.get(position);
        final String mUserID = ((MyApplication) ((Activity) mContext).getApplication()).getObjectID();
        user_profileBmobQuery.addWhereEqualTo("userID", mUserID);
        user_profileBmobQuery.addWhereEqualTo("notifyID", message.getObjectId());
        user_profileBmobQuery.findObjects(new FindListener<NotifyManager>() {
            @Override
            public void done(List<NotifyManager> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        addNotifyToManager(position);
                    } else {
                    }
                } else {
//                        Log.d("Kiuber_LOG", "done: " + e.getMessage());
                    ErrorCodeUtil.switchErrorCode(mContext, e.getErrorCode() + "");
                }
            }
        });
    }

    private void addNotifyToManager(int position) {
        final NotifyManager notifyManager = new NotifyManager();
        Message message = mMessageList.get(position);
        String mUserID = ((MyApplication) ((Activity) mContext).getApplication()).getObjectID();
        notifyManager.setNotifyID(message.getObjectId());
        notifyManager.setUserID(mUserID);
        notifyManager.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Log.i("保存成功", "done: " + s);
                } else {

                }

            }
        });
    }

    private void showNotify(String notifyDetail) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("详细信息");
        builder.setMessage(notifyDetail);
        builder.setPositiveButton("确定", null);
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    class ViewHolder {
        /*        @BindView(R.id.civ_donate_avatar)
                CircleImageView mCivDonateAvatar;*/
        @BindView(R.id.rl_notify)
        RelativeLayout mRlNotify;
        /*    @BindView(R.id.iv_notify)
            TextView mIvNotify;*/
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
