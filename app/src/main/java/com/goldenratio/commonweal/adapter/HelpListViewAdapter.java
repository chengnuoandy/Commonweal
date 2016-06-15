package com.goldenratio.commonweal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldenratio.commonweal.R;

/**
 * Created by 两个人 on 2016-06-14.
 */
public class HelpListViewAdapter extends BaseAdapter {

    public String[] img_text = {"我的ddsddd", "收藏ssfs", "购物车sdfsfs", "删除sfsfs", "消息sfsf", "添加sfsfdsfs"};
    public int[] imgs = {R.mipmap.meinv, R.mipmap.meinv,
            R.mipmap.meinv, R.mipmap.meinv,
            R.mipmap.meinv, R.mipmap.meinv};


    public Context mContext;
    public HelpListViewAdapter(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return img_text.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.view_help_list, parent, false);
            holder.tv_title =(TextView) convertView.findViewById(R.id.tv_title);
            holder.iv_icon=(ImageView)convertView.findViewById(R.id.iv_icon);
            convertView.setTag(holder);
        }
        else
        {
            holder =(ViewHolder)convertView.getTag();}
        holder.iv_icon.setBackgroundResource(imgs[position]);

        holder.tv_title.setText(img_text[position]);




        return convertView;
    }

    private class ViewHolder{
        private ImageView iv_icon;
        private TextView tv_title;

    }
}

