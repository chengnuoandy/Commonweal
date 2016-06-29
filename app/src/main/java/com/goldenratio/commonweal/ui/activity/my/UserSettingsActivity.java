package com.goldenratio.commonweal.ui.activity.my;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.goldenratio.commonweal.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserSettingsActivity extends Activity {


    @BindView(R.id.tv_user_name)
    TextView mTvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        ButterKnife.bind(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    String userName = data.getStringExtra("user_Name");
                    mTvUserName.setText(userName);
                }
                break;
            case 3:
                break;
        }
    }

    @OnClick({R.id.iv_us_back, R.id.rl_set_avatar, R.id.rl_set_userName, R.id.rl_set_userNickName, R.id.rl_set_userSex, R.id.rl_set_autograph, R.id.tv_set_address})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.iv_us_back:
                finish();
                break;
            case R.id.rl_set_avatar:
                intent = new Intent(this, SetAvatarActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.rl_set_userName:
                intent = new Intent(this, SetUserNameActivity.class);
                startActivityForResult(intent, 2);
                break;
            case R.id.rl_set_userNickName:
                break;
            case R.id.rl_set_userSex:
                break;
            case R.id.rl_set_autograph:
                break;
            case R.id.tv_set_address:
                intent = new Intent(this, SetAddressActivity.class);
                startActivityForResult(intent, 3);
                break;
        }
    }
}
