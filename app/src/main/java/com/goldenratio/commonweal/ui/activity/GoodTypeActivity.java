package com.goldenratio.commonweal.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.goldenratio.commonweal.R;
import com.goldenratio.commonweal.util.ImmersiveUtil;

/**
 * Created by Kiuber on 2016/6/24.
 */
public class GoodTypeActivity extends BaseActivity {

    private ListView mLv;
    private String[] mGoodType =

            {
                    "演唱会物品", "日常衣服", "演出道具", "珠宝黄金", "生活家电", "运动器材", "其它", "", "", "", "", ""
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_type);
        mLv = (ListView) findViewById(R.id.lv_good_type);
        mLv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, mGoodType));
        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("type", mGoodType[position]);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        new ImmersiveUtil(this, R.color.white, true);
    }
}
