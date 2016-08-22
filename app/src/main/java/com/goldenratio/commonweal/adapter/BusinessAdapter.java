package com.goldenratio.commonweal.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.goldenratio.commonweal.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/20.
 */

public class BusinessAdapter extends BaseAdapter {
    private ArrayList mArrayList;
    private Context mContext;

    public BusinessAdapter(ArrayList arrayList , Context context){
        this.mArrayList = arrayList;
        this.mContext = context;
    }
    @Override
    public int getCount() {
        return mArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view= null;
        if (convertView != null) {
            view = convertView;
        } else {
            view = View.inflate(mContext, R.layout.item_business, null);
        }
        TextView tv_bus_name = (TextView) view.findViewById(R.id.tv_bus_name);
        TextView tv_bus_time = (TextView) view.findViewById(R.id.tv_bus_time);
        TextView tv_bus_state = (TextView) view.findViewById(R.id.tv_bus_state);
        TextView tv_bus_money = (TextView) view.findViewById(R.id.tv_bus_money);

//        BusinessUtil businessUtil = (BusinessUtil) mArrayList.get(position);
//
//        tv_bus_name.setText(businessUtil.Business_Name);
//        tv_bus_state.setText(businessUtil.Business_state);
//        tv_bus_money.setText(businessUtil.Business_money);
//        tv_bus_time.setText(businessUtil.Business_time);
        return view;
    }
}
