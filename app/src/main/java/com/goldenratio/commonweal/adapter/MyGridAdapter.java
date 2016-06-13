package com.goldenratio.commonweal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldenratio.commonweal.R;

public class MyGridAdapter extends BaseAdapter {
    private Context mContext;

    public String[] img_text = {"我的", "收藏", "购物车", "删除", "消息", "添加"};
    public int[] imgs = {R.mipmap.goods, R.mipmap.goods,
            R.mipmap.goods, R.mipmap.goods,
            R.mipmap.goods, R.mipmap.goods};


    public MyGridAdapter(Context mContext) {
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
        BaseViewHolder holder;
        if (convertView == null) {
            holder = new BaseViewHolder();
           convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.grid_item, parent, false);
            holder.tv =(TextView) convertView.findViewById(R.id.tv_item);
            holder.iv=(ImageView)convertView.findViewById(R.id.iv_item);
            convertView.setTag(holder);
        }
        else
        {
            holder =(BaseViewHolder)convertView.getTag();}
        holder.iv.setBackgroundResource(imgs[position]);

        holder.tv.setText(img_text[position]);




        return convertView;
    }

    /*
          VIewHolder
     */
    public  class BaseViewHolder   {
        public TextView tv;
        public ImageView iv;


    }
}
