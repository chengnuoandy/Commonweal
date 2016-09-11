package com.goldenratio.commonweal.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.PrivateMessageListAdapter;
import com.goldenratio.commonweal.bean.Message;
import com.goldenratio.commonweal.util.ErrorCodeUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 作者：Created by 龙啸天 on 2016/6/25 0025.
 * 邮箱：jxfengmtx@163.com ---17718
 * <p>
 * 消息-->私信 后台代码
 */
public class PrivateLetterFragment extends Fragment {

    @BindView(R.id.lv_attention)
    ListView mLvAttention;
    @BindView(R.id.tv_no_data)
    TextView mTvNoData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_private_letter, null);
        ButterKnife.bind(this, view);

        getMessageFromBmob();
        return view;
    }


    private void getMessageFromBmob() {
        BmobQuery<Message> user_profileBmobQuery = new BmobQuery<>();
        user_profileBmobQuery.findObjects(new FindListener<Message>() {
            @Override
            public void done(List<Message> list, BmobException e) {
                if (e == null) {
                    mLvAttention.setAdapter(new PrivateMessageListAdapter(list, getActivity()));
                } else {
//                        Log.d("Kiuber_LOG", "done: " + e.getMessage());
                    ErrorCodeUtil.switchErrorCode(getContext(), e.getErrorCode() + "");
                }
            }
        });
    }
}
