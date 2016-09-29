package com.goldenratio.commonweal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.goldenratio.commonweal.R;

import java.util.List;

/**
 * Created by Kiuber on 2016/6/17.
 */

public class MyGoodPicAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<String> mImagePath;
    private GridView mGv;

    public MyGoodPicAdapter(Context mContext, List<String> imagePath, GridView gridView) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        this.mImagePath = imagePath;
        this.mGv = gridView;
    }

    @Override
    public int getCount() {
        return mImagePath.size();
    }

    @Override
    public Object getItem(int position) {
        return mImagePath.get(position).toString();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.view_add_good_pic_item, null);
            viewHolder.mSelectedPic = (ImageView) convertView.findViewById(R.id.iv_selected_pic);
            viewHolder.mDeletePic = (ImageView) convertView.findViewById(R.id.iv_delete_pic);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Glide.with(mContext)
                .load(getItem(position).toString())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder.mSelectedPic);

        viewHolder.mDeletePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImagePath.remove(position);
                notifyDataSetChanged();
                if (mImagePath.size() == 0) {
                    mGv.setVisibility(View.GONE);
                }
            }
        });

        return convertView;
    }

    class ViewHolder {
        ImageView mSelectedPic;
        ImageView mDeletePic;
    }
}