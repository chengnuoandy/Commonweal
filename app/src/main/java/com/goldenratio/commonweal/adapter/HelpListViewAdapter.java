package com.goldenratio.commonweal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Help;

import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;

/**
 * Created by 两个人 on 2016-06-14.
 */
public class HelpListViewAdapter extends BaseAdapter {

//    public String[] img_text = {"我的", "收藏", "购物车", "身份", "消息", "查找"};
//    public int[] imgs = {R.mipmap.my_home, R.mipmap.my_collect,
//            R.mipmap.my_cart, R.mipmap.my_personalcenter,
//            R.mipmap.my_news, R.mipmap.my_search};





    List<Help> help;


    public Context mContext;

    public HelpListViewAdapter(Context mContext, List<Help> help) {

        this.mContext = mContext;
        this.help = help;



    }

    @Override
    public int getCount() {
        return help.size();
    }

    @Override
    public Object getItem(int position) {
        return help.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ;
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.view_help_list, parent, false);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }
        //holder.iv_icon.setBackgroundResource(imgs[position]);

        holder.tv_title.setText(help.get(position).getHelp_Title());
        Picasso.with(mContext).load(help.get(position).getHelp_pic().toString()).into(holder.iv_icon);
//        Picasso.with(mContext).load(url.get(position)).getPicUrl()).into(holder.iv_icon);


        return convertView;
    }

    private class ViewHolder {
        private ImageView iv_icon;
        private TextView tv_title;

    }
}

