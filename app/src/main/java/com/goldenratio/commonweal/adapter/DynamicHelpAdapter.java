package com.goldenratio.commonweal.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Dynamic_Help;
import com.goldenratio.commonweal.ui.activity.DynamicPhotoShow;
import com.jaeger.ninegridimageview.NineGridImageView;
import com.jaeger.ninegridimageview.NineGridImageViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 龙啸天 - Jxfen on 2016/8/15.
 * Email:jxfengmtx@163.com
 */
public class DynamicHelpAdapter extends BaseAdapter {


    private List<Dynamic_Help> mDynamic_helpList;
    private LayoutInflater mInflater;
    private Context mContext;


    public DynamicHelpAdapter(List<Dynamic_Help> dynamic_helpList, Context context) {
        mInflater = LayoutInflater.from(context);
        mDynamic_helpList = dynamic_helpList;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mDynamic_helpList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDynamic_helpList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.view_dyc_help_item, null);
            holder = new ViewHolder(convertView);
            holder.mIvDycHelpPic.setAdapter(new NineGridImageViewAdapter<String>() {

                // 图片点击事件,启动浏览模式
                @Override
                protected void onItemImageClick(Context context, int index, List<String> list) {
                    Intent intent = new Intent(mContext, DynamicPhotoShow.class);
                    intent.putExtra("index", index);
                    intent.putStringArrayListExtra("list", (ArrayList<String>) list);
                    mContext.startActivity(intent);
                    //设置切换动画
                    ((Activity) mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }

                @Override
                protected void onDisplayImage(Context context, ImageView imageView, String str) {
                    if (str != null) {
                        Glide.with(mContext)
                                .load(str)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imageView);
                    }

                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Dynamic_Help help = mDynamic_helpList.get(position);
        holder.mTvOrganizationName.setText(help.getInitiator_Name());
        holder.mTvReleaseTime.setText(help.getUpdatedAt());
        holder.mTvDycHelpContent.setText(help.getHelp_Content());
        holder.mIvDycHelpPic.setImagesData(help.getHelp_Pic());
        Glide.with(mContext)
                .load(help.getInitiator_Image())
                .into(holder.mIvOrganizationAvatar);

        return convertView;
    }

    class ViewHolder {
        ImageView mIvOrganizationAvatar;
        TextView mTvOrganizationName;
        TextView mTvReleaseTime;
        TextView mTvDycHelpContent;
        NineGridImageView mIvDycHelpPic;

        ViewHolder(View view) {
            mIvOrganizationAvatar = (ImageView) view.findViewById(R.id.iv_organization_avatar);
            mTvOrganizationName = (TextView) view.findViewById(R.id.tv_organization_name);
            mTvReleaseTime = (TextView) view.findViewById(R.id.tv_release_time);
            mTvDycHelpContent = (TextView) view.findViewById(R.id.tv_dyc_help_content);
            mIvDycHelpPic = (NineGridImageView) view.findViewById(R.id.iv_dyc_help_pic);
        }
    }
}
