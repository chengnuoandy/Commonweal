package com.goldenratio.commonweal.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.util.CommentUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 评论的Adapter
 * Created by Administrator on 2016/8/8.
 */

public class HelpCommentAdapter extends BaseAdapter {

    public ArrayList<CommentUtils> list;
    public Context context;

    //通过构造方法传递list
public HelpCommentAdapter(Context mContext, ArrayList<CommentUtils> list){
    this.context = mContext;
    this.list=list;
}

    //listview一共有多少个item
    @Override
    public int getCount() {
        return list.size();
    }

    //获得相应数据集合中特定位置的数据项
    @Override
    public Object getItem(int position) {
        return null;
    }

    //它返回的是该postion对应item的id
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view=null;
        //convertView
        if(convertView!=null){
            view = convertView;
        }else {
            view=View.inflate(context, R.layout.item_help_comment,null);
        }
        de.hdodenhof.circleimageview.CircleImageView icom = (CircleImageView) view.findViewById(R.id.civ_hair);
        TextView tv_comment = (TextView) view.findViewById(R.id.tv_comment);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_id);
        TextView tv_time = (TextView) view.findViewById(R.id.tv_time);

        CommentUtils utils = list.get(position);

        tv_comment.setText(utils.comment);
        tv_name.setText(utils.UserName);
        tv_time.setText(utils.times);
        Picasso.with(context).load(utils.icom).into(icom);
        return view;
    }
}
