package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.goldenratio.commonweal.R;

/**
 * Created by Kiuber on 2016/8/23.
 */
public class PayRecordDetailActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_pay_record_detailed);
    }
}
