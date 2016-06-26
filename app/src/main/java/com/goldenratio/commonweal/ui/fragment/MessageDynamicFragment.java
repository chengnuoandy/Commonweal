package com.goldenratio.commonweal.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goldenratio.commonweal.R;

/**
 * 作者：Created by 龙啸天 on 2016/6/25 0025.
 * 邮箱：jxfengmtx@163.com ---17718
 *
 * 消息-->动态 后台代码
 */
public class MessageDynamicFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_message_dynamic, null);

        return view;
    }

}
