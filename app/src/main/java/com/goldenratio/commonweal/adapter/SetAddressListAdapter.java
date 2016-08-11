package com.goldenratio.commonweal.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goldenratio.commonweal.R;

import java.util.List;

/**
 * Created by Lxt- Jxfen on 2016/8/6.
 * Email:jxfengmtx@163.com
 */
public class SetAddressListAdapter extends BaseAdapter implements View.OnClickListener {

    private List<List<String>> mAddressList;
    private LayoutInflater mInflater;
    private Callback mCallback;
    private int defutPosition = -1;

    public SetAddressListAdapter(Context context, List<List<String>> addressList, Callback callback) {
        mAddressList = addressList;
        mCallback = callback;
        mInflater = LayoutInflater.from(context);
        Log.i("adapter", "SetAddressListAdapter: " + addressList.size());
    }

    @Override
    public void onClick(View v) {
        defutPosition = mCallback.click(v);
    }

    /**
     * 自定义接口，回掉按钮点击事件到SetAddressActivity
     */
    public interface Callback {
        int click(View v);
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
            holder.mCbSelectDefaultAddress = (CheckBox) convertView.findViewById(R.id.cb_default_address);
            //holder.mTvDefalutAddress = (TextView) convertView.findViewById(R.id.tv_defut_address);
            holder.mTvDeleteAddress = (TextView) convertView.findViewById(R.id.tv_delete_address);
            //  holder.mTvDiv = convertView.findViewById(R.id.v_div);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        List<String> address = mAddressList.get(position);
        int i = 0;
        if (defutPosition == -1) {
            defutPosition = Integer.parseInt(address.get(0));
        } else if (position == 0) {
            i = 1;
        } else i = 0;
        boolean isDefutAddre = defutPosition == position;
        holder.mCbSelectDefaultAddress.setChecked(isDefutAddre);
        Log.i("是否是默认地址", defutPosition + "----" + position + "getView: " + isDefutAddre);
        Log.i("address数据", position + "--positions--" + address);
        holder.mTvConsignees.setText(address.get(i));
        holder.mTvConsigneesPhone.setText(address.get(i + 1));
        holder.mTvConsigneesAddress.setText(address.get(i + 2));

        holder.mTvDeleteAddress.setTag(position);
        holder.mCbSelectDefaultAddress.setId(position);
        holder.mRlAddress.setOnClickListener(this);
        // holder.mRbSelectDefaultAddress.setOnCheckedChangeListener(this);
        // holder.mCbSelectDefaultAddress.setOnCheckedChangeListener(this);
        holder.mCbSelectDefaultAddress.setOnClickListener(this);
        //  holder.mTvDefalutAddress.setOnClickListener(this);

        holder.mTvDeleteAddress.setOnClickListener(this);
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
