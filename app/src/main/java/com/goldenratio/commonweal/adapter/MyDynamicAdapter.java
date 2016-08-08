package com.goldenratio.commonweal.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Dynamic;
import com.goldenratio.commonweal.bean.U_FamousP;
import com.goldenratio.commonweal.bean.U_NormalP;
import com.goldenratio.commonweal.bean.User_Profile;
import com.jaeger.ninegridimageview.NineGridImageView;
import com.jaeger.ninegridimageview.NineGridImageViewAdapter;

import java.util.List;

/**
 * Created by 冰封承諾Andy on 2016/8/7 0007.
 * 个人动态的适配器
 */
public class MyDynamicAdapter extends BaseAdapter {

    private List<Dynamic> mList;
    private LayoutInflater mInflater;
    private Context mContext;

    public MyDynamicAdapter(Context context, List<Dynamic> list) {
        mList = list;
        mContext = context;
        mInflater = LayoutInflater.from(context);
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
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.view_dynamic_item, null);

            viewHolder.initView(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.initData(position);
        return convertView;
    }


    class ViewHolder {
        private TextView mTvTime;
        private TextView mTvName;
        private TextView mText;
        private TextView mLocation;
        private ImageView mUserPic;
        private NineGridImageView mNineGridImageView;

        private void initView(View view) {
            mTvTime = (TextView) view.findViewById(R.id.tv_time);
            mTvName = (TextView) view.findViewById(R.id.tv_name);
            mText = (TextView) view.findViewById(R.id.tv_text);
            mNineGridImageView = (NineGridImageView) view.findViewById(R.id.iv_pic);
            mLocation = (TextView) view.findViewById(R.id.tv_location);
            mUserPic = (ImageView) view.findViewById(R.id.iv_user_avatar);

            mNineGridImageView.setAdapter(new NineGridImageViewAdapter<String>() {
                @Override
                protected void onDisplayImage(Context context, ImageView imageView, String str) {
                    if (str != null) {
                        Glide.with(mContext)
                                .load(str)
                                .into(imageView);
                    }
                }
            });
        }

        public void initData(int position) {
            User_Profile user = mList.get(position).getDynamics_user();
            mTvName.setText(user.getUser_Nickname());
            mTvTime.setText(mList.get(position).getDynamics_time());
            mText.setText(mList.get(position).getDynamics_title());
            mLocation.setText(mList.get(position).getDynamics_location());
            mNineGridImageView.setImagesData(mList.get(position).getDynamics_pic());

            Glide.with(mContext)
                    .load(user.getUser_image_hd())
                    .into(mUserPic);
//            Log.d("lxc", "initData: "+mList.get(position).getDynamics_u_pic());
        }

    }
}
