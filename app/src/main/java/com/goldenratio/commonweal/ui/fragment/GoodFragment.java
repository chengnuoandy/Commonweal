package com.goldenratio.commonweal.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.adapter.MyGoodListViewAdapter;
import com.goldenratio.commonweal.bean.Good;
import com.goldenratio.commonweal.dao.UserDao;
import com.goldenratio.commonweal.ui.activity.GoodActivity;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;


public class GoodFragment extends Fragment {


    private View view;
    private ListView mLvGood;
    private MyGoodListViewAdapter myGoodListViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_good, null);
        view.findViewById(R.id.iv_add_good).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserTableExist()) {
                    startActivity(new Intent(getContext(), GoodActivity.class));
                } else {
                    Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
                }
            }
        });

        initView();
        getGoodDataFromBmob();
        return view;
    }

    private void initView() {
        mLvGood = (ListView) view.findViewById(R.id.lv_good_all);
    }

    private void getGoodDataFromBmob() {
        BmobQuery<Good> goodBmobQuery = new BmobQuery<>();
        goodBmobQuery.order("-createdAt");
        goodBmobQuery.findObjects(getContext(), new FindListener<Good>() {
            @Override
            public void onSuccess(List<Good> list) {
                myGoodListViewAdapter = new MyGoodListViewAdapter(getContext(), list);
                mLvGood.setAdapter(myGoodListViewAdapter);
            }

            @Override
            public void onError(int i, String s) {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != myGoodListViewAdapter) {
            myGoodListViewAdapter.startRefreshTime();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != myGoodListViewAdapter) {
            myGoodListViewAdapter.cancelRefreshTime();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != myGoodListViewAdapter) {
            myGoodListViewAdapter.cancelRefreshTime();
        }
    }

    /**
     * 判断本地用户表是否存在
     *
     * @return
     */
    private boolean isUserTableExist() {
        boolean isTableExist = true;
        String sqlCmd = "SELECT count(User_Avatar) FROM User ";
        UserDao ud = new UserDao(getContext());
        Cursor c = ud.query(sqlCmd);
        if (c.moveToNext()) {
            if (c.getInt(0) == 0) {
                isTableExist = false;
            }
        }
        c.close();
        return isTableExist;
    }
}
