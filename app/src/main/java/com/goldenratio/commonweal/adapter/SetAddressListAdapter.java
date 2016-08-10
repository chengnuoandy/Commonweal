package com.goldenratio.commonweal.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goldenratio.commonweal.R;

import java.util.List;
import java.util.Objects;

/**
 * Created by Lxt- Jxfen on 2016/8/6.
 * Email:jxfengmtx@163.com
 */
public class SetAddressListAdapter extends BaseAdapter implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private List<List<String>> mAddressList;
    private LayoutInflater mInflater;
    private Callback mCallback;
    private int temp = -1;

    public SetAddressListAdapter(Context context, List<List<String>> addressList, Callback callback) {
        mAddressList = addressList;
        mCallback = callback;
        mInflater = LayoutInflater.from(context);
        Log.i("adapter", "SetAddressListAdapter: " + addressList.size());
    }

    @Override
    public void onClick(View v) {
        mCallback.click(v);

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        temp = mCallback.onCheckedChanged(buttonView, isChecked, temp);
    }

    /**
     * 自定义接口，回掉按钮点击事件到SetAddressActivity
     */
    public interface Callback {
        void click(View v);

        int onCheckedChanged(CompoundButton buttonView, boolean isChecked, int temp);
    }

    @Override
    public int getCount() {
        return mAddressList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAddressList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.view_address_item, null);
            holder.mTvConsignees = (TextView) convertView.findViewById(R.id.tv_consignees);
            holder.mTvConsigneesPhone = (TextView) convertView.findViewById(R.id.tv_tv_consignees_phone);
            holder.mTvConsigneesAddress = (TextView) convertView.findViewById(R.id.tv_consignees_address);
            holder.mRlAddress = (RelativeLayout) convertView.findViewById(R.id.rl_address);
            // holder.mRbSelectDefaultAddress = (RadioButton) convertView.findViewById(R.id.rb_select_defut_address);
            holder.mCbSelectDefaultAddress = (CheckBox) convertView.findViewById(R.id.cb_default_address);
            //holder.mTvDefalutAddress = (TextView) convertView.findViewById(R.id.tv_defut_address);
            holder.mTvDeleteAddress = (TextView) convertView.findViewById(R.id.tv_delete_address);
            //  holder.mTvDiv = convertView.findViewById(R.id.v_div);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        List<String> address = mAddressList.get(position);
        Log.i("默认地址", position + "--positions--" + address.get(0));

        holder.mCbSelectDefaultAddress.setChecked(Objects.equals(address.get(0), position + ""));
        int i;
        if (position == 0) {
            i = 1;
        } else i = 0;
        holder.mTvConsignees.setText(address.get(i));
        holder.mTvConsigneesPhone.setText(address.get(i + 1));
        holder.mTvConsigneesAddress.setText(address.get(i + 2));
        holder.mCbSelectDefaultAddress.setId(position);
        holder.mRlAddress.setOnClickListener(this);
        // holder.mRbSelectDefaultAddress.setOnCheckedChangeListener(this);
        holder.mCbSelectDefaultAddress.setOnCheckedChangeListener(this);
        //  holder.mTvDefalutAddress.setOnClickListener(this);

        holder.mTvDeleteAddress.setOnClickListener(this);

        if (position == temp) {
            holder.mCbSelectDefaultAddress.setChecked(true);//将本次点击的RadioButton设置为选中状态
            holder.mCbSelectDefaultAddress.setClickable(false);
        } else {
            holder.mCbSelectDefaultAddress.setChecked(false);
            holder.mCbSelectDefaultAddress.setClickable(true);
        }
        return convertView;

    }

    class ViewHolder {
        public TextView mTvConsignees;
        public TextView mTvConsigneesPhone;
        public TextView mTvConsigneesAddress;
        public RelativeLayout mRlAddress;
        // public RadioButton mRbSelectDefaultAddress;
        public CheckBox mCbSelectDefaultAddress;
        //   public TextView mTvDefalutAddress;
        public TextView mTvDeleteAddress;
        //   public View mTvDiv;
    }
}
