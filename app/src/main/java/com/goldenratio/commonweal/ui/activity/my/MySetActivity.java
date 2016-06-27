package com.goldenratio.commonweal.ui.activity.my;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.dao.UserDao;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 作者：Created by 龙啸天 on 2016/6/27 0025.
 * 邮箱：jxfengmtx@163.com ---17718
 * <p/>
 * 整个app的设置
 */
public class MySetActivity extends Activity {
    @BindView(R.id.tv_exit)
    TextView mTvExit;
    @BindView(R.id.tv_set_settings)
    TextView mTvSetSettings;
    private boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_set);
        ButterKnife.bind(this);

        isLogin = getIntent().getExtras().getBoolean("islogin");


    }


    private void deleteTable() {
        String sqlCmd = "DELETE FROM User";
        UserDao ud = new UserDao(this);
        ud.delete(sqlCmd);
    }


    @OnClick({R.id.tv_set_settings, R.id.tv_exit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_set_settings:
                break;
            case R.id.tv_exit:
                if (!isLogin) {
                    Toast.makeText(MySetActivity.this, "您尚未登陆", Toast.LENGTH_SHORT).show();
                } else {
                    deleteTable();
                    setResult(RESULT_OK, null);
                    finish();
                }
                break;
        }
    }
}
