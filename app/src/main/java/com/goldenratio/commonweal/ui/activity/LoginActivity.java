package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.bean.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;

/**
 * Created by lvxue on 2016/6/7 0007.
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "bfchengnuo";
    @BindView(R.id.my_login_back)
    ImageView mMyLoginBack;
    @BindView(R.id.my_login_register)
    TextView mMyLoginRegister;
    @BindView(R.id.relativeLayout1)
    RelativeLayout mRelativeLayout1;
    @BindView(R.id.login_phone)
    EditText mLoginPhone;
    @BindView(R.id.login_password)
    EditText mLoginPassword;
    @BindView(R.id.login_btn)
    Button mLoginBtn;
    @BindView(R.id.imageView1)
    ImageView mImageView1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                //判断密码是否正确
                isLogin(mLoginPhone.getText().toString(), mLoginPassword.getText().toString());
                break;
        }
    }

    private void isLogin(String Phone, final String Password) {
        BmobQuery<User> bmobQuery = new BmobQuery<User>();
        bmobQuery.getObject(this, "hdYuaaaf", new GetListener<User>() {
            @Override
            public void onSuccess(User user) {
                if (Password.equals(user.getUser_Password())) {
                    Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(LoginActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
