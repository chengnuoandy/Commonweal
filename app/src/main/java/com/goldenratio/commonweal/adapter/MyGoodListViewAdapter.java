package com.goldenratio.commonweal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Good;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Kiuber on 2016/6/26.
 */

public class MyGoodListViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<Good> mGoodList;
    private LayoutInflater mInflater;

    public MyGoodListViewAdapter(Context mContext, List<Good> mGoodList) {
        this.mContext = mContext;
        this.mGoodList = mGoodList;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mGoodList.size();
    }

    @Override
    public String getItem(int position) {
        return mGoodList.get(position).getUser_Name();
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
            convertView = mInflater.inflate(R.layout.view_good_all, null);
            viewHolder.mTvStarName = (TextView) convertView.findViewById(R.id.tv_star_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mTvStarName.setText(getItem(position));
        return convertView;
    }

    class ViewHolder {
        TextView mTvStarName;
    }
}
