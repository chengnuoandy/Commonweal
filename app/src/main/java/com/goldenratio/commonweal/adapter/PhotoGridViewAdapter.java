package com.goldenratio.commonweal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.util.FileUtils;

import java.util.ArrayList;

/**
 * Created by Kiuber on 2016/6/17.
 */

public class PhotoGridViewAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<String> mAlPhotoPath;

    public PhotoGridViewAdapter(Context mContext, ArrayList<String> mAlPhotoPath) {
        this.mContext = mContext;
        this.mAlPhotoPath = mAlPhotoPath;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mAlPhotoPath.size();
    }

    @Override
    public String getItem(final int position) {
        return mAlPhotoPath.get(position);
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
            convertView = mLayoutInflater.inflate(R.layout.gridview_photo_item, null);
            viewHolder.mIvDelete = (ImageView) convertView.findViewById(R.id.iv_delete_selected_photo);
            viewHolder.mIvPhoto = (ImageView) convertView.findViewById(R.id.iv_selected_photo);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mIvPhoto.setImageBitmap(FileUtils.getLocalBitmap(getItem(position)));
        return convertView;
    }

    private class ViewHolder {
        private ImageView mIvDelete;
        private ImageView mIvPhoto;
    }

}
