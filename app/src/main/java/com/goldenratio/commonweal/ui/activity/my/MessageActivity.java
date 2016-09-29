package com.goldenratio.commonweal.ui.activity.my;

import android.app.NotificationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.PrivateMessageListAdapter;
import com.goldenratio.commonweal.bean.Message;
import com.goldenratio.commonweal.ui.activity.BaseActivity;
import com.goldenratio.commonweal.util.ErrorCodeUtil;
import com.goldenratio.commonweal.util.ImmersiveUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MessageActivity extends BaseActivity {

    @BindView(R.id.lv_attention)
    ListView mLvAttention;
    @BindView(R.id.tv_no_data)
    TextView mTvNoData;
    @BindView(R.id.iv_back)
    ImageView mIvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(1);

        getMessageFromBmob();
        new ImmersiveUtil(this, R.color.white, true);
    }

    private void getMessageFromBmob() {
        BmobQuery<Message> BmobQuery = new BmobQuery<>();
        BmobQuery.order("createdAt");
                BmobQuery.findObjects(new FindListener<Message>() {
                    @Override
                    public void done(List<Message> list, BmobException e) {
                        if (e == null) {
                            if (list.size() == 0) {
                                mTvNoData.setVisibility(View.VISIBLE);
                            } else
                                mLvAttention.setAdapter(new PrivateMessageListAdapter(list, MessageActivity.this));
                        } else {
//                        Log.d("Kiuber_LOG", "done: " + e.getMessage());
                            ErrorCodeUtil.switchErrorCode(getApplication(), e.getErrorCode() + "");
                        }
                    }
                });
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        finish();
    }
}
