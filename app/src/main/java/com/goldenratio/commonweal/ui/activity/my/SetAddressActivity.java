package com.goldenratio.commonweal.ui.activity.my;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.SetAddressListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetAddressActivity extends Activity implements SetAddressListAdapter.Callback {

    @BindView(R.id.iv_address_back)
    ImageView mIvBack;
    @BindView(R.id.lv_address_details)
    ListView mLvAddressDetails;
    @BindView(R.id.btn_add_address)
    Button mTvAddAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_address);
        ButterKnife.bind(this);

    }


    @OnClick({R.id.iv_address_back, R.id.btn_add_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_address_back:
                finish();
                break;
            case R.id.btn_add_address:
                Intent intent = new Intent(SetAddressActivity.this, EditAddressActivity.class);
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    public void click(View v) {

    }
}
