package com.goldenratio.commonweal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.Good;

import java.util.List;

/**
 * Created by 冰封承諾Andy on 2016/8/19 0019.
 * 卖出物品列表的适配器
 */
public class MySellGoodAdapter extends BaseAdapter {

    private List<Good> mList;
    private Context mContext;

    public MySellGoodAdapter(Context context, List<Good> list) {
        mContext = context;
        mList = list;
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_sellgood_item, null);
            viewHolder.initView(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.initData(position);
        return convertView;
    }

    private class ViewHolder {

        private ImageView mIvPic;
        private TextView mTvTitle;
        private TextView mTvStatus;
        private Button mBtnShip;

        private void initView(View view) {
            mIvPic = (ImageView) view.findViewById(R.id.iv_pic);
            mTvTitle = (TextView) view.findViewById(R.id.tv_title);
            mTvStatus = (TextView) view.findViewById(R.id.tv_status);
            mBtnShip = (Button) view.findViewById(R.id.btn_ship);

            mBtnShip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "测试测试", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void initData(int pos) {
            String str = "未知";
//            int flag = mList.get(pos).getGood_Status();
            int flag = 1;
            if (flag == 0) {
                str = "物品拍卖失败！";
            } else if (flag == 1) {
                str = "正在拍卖";
            } else if (flag == 2) {
                str = "拍卖成功，等待发货";
                mBtnShip.setVisibility(View.VISIBLE);
            } else if (flag == 3) {
                str = "已发货，等待签收";
            } else if (flag == 4) {
                str = "已签收，订单完成！";
            }
            mTvTitle.setText(mList.get(pos).getGood_Name() + "\n" + mList.get(pos).getGood_Description());
            mTvStatus.setText("状态：" + str +"(现在价格:" + mList.get(pos).getGood_NowCoin() + ")");

            Glide.with(mContext)
                    .load(mList.get(pos).getGood_Photos().get(0))
                    .into(mIvPic);
        }
    }
}
