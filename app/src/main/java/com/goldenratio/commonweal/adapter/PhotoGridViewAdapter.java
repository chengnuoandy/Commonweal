package com.goldenratio.commonweal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.util.GlideLoader;

import java.util.List;

/**
 * Created by Kiuber on 2016/6/17.
 */

public class PhotoGridViewAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<String> mImagePath;
    private GlideLoader glideLoader;

    public PhotoGridViewAdapter(Context mContext, List<String> imagePath) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        this.mImagePath = imagePath;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.gridview_photo_item, null);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_selected_photo);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Glide.with(mContext)
                .load(getItem(position).toString())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder.imageView);

//        glideLoader.displayImage(mContext, "http://img.ricedonate.com/project/201606/18/14662715274hAg24.jpg", viewHolder.imageView);

        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
    }
}