package com.goldenratio.commonweal.ui.activity.my;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.goldenratio.commonweal.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserSettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.iv_us_back)
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_us_back:
                finish();
                break;
        }
    }
}
