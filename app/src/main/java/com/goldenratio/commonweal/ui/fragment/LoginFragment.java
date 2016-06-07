package com.goldenratio.commonweal.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.ui.activity.MainActivity;

/**
 * Created by lvxue on 2016/6/7 0007.
 */
public class LoginFragment extends Fragment implements View.OnClickListener{

    private EditText mLoginPhone;
    private EditText mLoginPassword;
    private Button mLoginBtn;

    //创建视图 加载布局调用
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login, null);

        mLoginPhone = (EditText) view.findViewById(R.id.login_phone);
        mLoginPassword = (EditText) view.findViewById(R.id.login_password);
        mLoginBtn = (Button) view.findViewById(R.id.login_btn);
        mLoginBtn.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_btn:
                Toast.makeText(getActivity(), "cess", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
