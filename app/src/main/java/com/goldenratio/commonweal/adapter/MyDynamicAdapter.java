package com.goldenratio.commonweal.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Dynamic;
import com.goldenratio.commonweal.bean.User_Profile;
import com.goldenratio.commonweal.ui.activity.DynamicPhotoShow;
import com.jaeger.ninegridimageview.NineGridImageView;
import com.jaeger.ninegridimageview.NineGridImageViewAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

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


    class ViewHolder implements View.OnClickListener{
        private TextView mTvTime;
        private TextView mTvName;
        private TextView mText;
        private TextView mLocation;
        private ImageView mUserPic;
        private TextView mDelete;
        private NineGridImageView mNineGridImageView;
        private int pos;

        private void initView(View view) {
            mTvTime = (TextView) view.findViewById(R.id.tv_time);
            mTvName = (TextView) view.findViewById(R.id.tv_name);
            mText = (TextView) view.findViewById(R.id.tv_text);
            mNineGridImageView = (NineGridImageView) view.findViewById(R.id.iv_pic);
            mLocation = (TextView) view.findViewById(R.id.tv_location);
            mDelete = (TextView) view.findViewById(R.id.tv_delete);
            mUserPic = (ImageView) view.findViewById(R.id.iv_user_avatar);

            mDelete.setOnClickListener(this);
            //九宫格加载图片
            mNineGridImageView.setAdapter(new NineGridImageViewAdapter<String>() {
//                    图片点击事件,启动浏览模式
                @Override
                protected void onItemImageClick(Context context, int index, List<String> list) {
                    Intent intent = new Intent(mContext, DynamicPhotoShow.class);
                    intent.putExtra("index",index);
                    intent.putStringArrayListExtra("list", (ArrayList<String>) list);
                    mContext.startActivity(intent);
                    //设置切换动画
                    ((Activity)mContext).overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                }

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
            pos = position;
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

        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("提示");
            builder.setMessage("你确定要删除这条动态吗？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //删除此动态
                    Dynamic dynamic = new Dynamic();
                    dynamic.setObjectId(mList.get(pos).getObjectId());
                    dynamic.delete(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null){
                                Toast.makeText(mContext, "删除成功！", Toast.LENGTH_SHORT).show();
                                mList.remove(pos);
                                notifyDataSetChanged();
                            }else {
                                Toast.makeText(mContext, "删除失败！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            builder.setNegativeButton("取消",null);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
}
