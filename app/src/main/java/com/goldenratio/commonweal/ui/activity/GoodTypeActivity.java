package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.goldenratio.commonweal.R;

/**
 * Created by Kiuber on 2016/6/24.
 */
public class GoodTypeActivity extends Activity {

    private ListView mLv;
    private String[] mGoodType =

            {
                    "演唱会物品", "日常衣服", "", "", "", "", "", "", "", "", "", ""
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
    }
}
