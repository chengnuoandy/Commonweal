package com.goldenratio.commonweal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.goldenratio.commonweal.R;

import java.util.List;

/**
 * Created by 冰封承諾Andy on 2016/8/6 0006.
 * 位置选择list适配器
 */
public class DynamicPoiSearchAdapter extends BaseAdapter {

    private Context mContext;
    private List<PoiItem> mPoiItems;

    public DynamicPoiSearchAdapter(Context context,List<PoiItem> list){
        mContext = context;
        mPoiItems = list;
    }

    @Override
    public int getCount() {
        return mPoiItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mPoiItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_dynamic_location_item,null);
            viewHolder.initView(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText(mPoiItems.get(position).getTitle());
        viewHolder.text.setText(mPoiItems.get(position).getSnippet());
        return convertView;
    }

    private class ViewHolder{
        public TextView title;
        public TextView text;

        private void initView(View view){
            title = (TextView) view.findViewById(R.id.item_title);
            text = (TextView) view.findViewById(R.id.item_text);
        }
    }
}
