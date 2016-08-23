package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.PayRecord;

/**
 * Created by Kiuber on 2016/8/23.
 */
public class WalletActivity extends Activity implements View.OnClickListener {
    private View mTvPayRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        initView();
        initListtener();
    }

    private void initView() {
        mTvPayRecord = findViewById(R.id.ll_pay_record);
    }

    private void initListtener() {
        mTvPayRecord.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_pay_record:
                startActivity(new Intent(this, PayRecordActivity.class));
                break;
        }
    }
}
