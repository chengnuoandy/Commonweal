package com.goldenratio.commonweal.ui.lib;

import android.view.View;
import android.widget.TextView;

import com.goldenratio.commonweal.R;


/**
 * Created by lvxue on 2016/6/20 0020.
 */
public class GoodKeypad {

    private View mView;
    private TextView num1TV;
    private TextView num2TV;
    private TextView num3TV;
    private TextView num4TV;
    private TextView num5TV;
    private TextView num6TV;
    private TextView num7TV;
    private TextView num8TV;
    private TextView num9TV;
    private TextView num0TV;
    private TextView dotTV;
    private TextView backTV;
    private TextView okTV;
    private TextView doneTV;
    private GoodEditText mGoodEditText;

    public GoodKeypad(View view,GoodEditText goodEditText){
        mView = view;
        mGoodEditText = goodEditText;
        init();
    }

    private void init() {
        initNode();
        initEventListener();
    }

    private void initEventListener() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.billSK_one:
                        mGoodEditText.clickNum("1");
                        break;
                    case R.id.billSK_two:
                        mGoodEditText.clickNum("2");
                        break;
                    case R.id.billSK_three:
                        mGoodEditText.clickNum("3");
                        break;
                    case R.id.billSK_four:
                        mGoodEditText.clickNum("4");
                        break;
                    case R.id.billSK_five:
                        mGoodEditText.clickNum("5");
                        break;
                    case R.id.billSK_six:
                        mGoodEditText.clickNum("6");
                        break;
                    case R.id.billSK_seven:
                        mGoodEditText.clickNum("7");
                        break;
                    case R.id.billSK_eight:
                        mGoodEditText.clickNum("8");
                        break;
                    case R.id.billSK_nine:
                        mGoodEditText.clickNum("9");
                        break;
                    case R.id.billSK_zero:
                        mGoodEditText.clickNum("0");
                        break;
                    case R.id.billSK_dot:
                        mGoodEditText.clickNum(".");
                        break;
                    case R.id.billSK_backspace:
                        mGoodEditText.clickOther(0);
                        break;
                }
            }
        };
        View[] views = new View[]{
                num1TV,
                num2TV,
                num3TV,
                num4TV,
                num5TV,
                num6TV,
                num7TV,
                num8TV,
                num9TV,
                num0TV,
                dotTV,
                backTV,
                okTV,
        };
        for (View view : views) {
            view.setOnClickListener(onClickListener);
        }
    }

    private void initNode() {
        num1TV = (TextView) mView.findViewById(R.id.billSK_one);
        num2TV = (TextView) mView.findViewById(R.id.billSK_two);
        num3TV = (TextView) mView.findViewById(R.id.billSK_three);
        num4TV = (TextView) mView.findViewById(R.id.billSK_four);
        num5TV = (TextView) mView.findViewById(R.id.billSK_five);
        num6TV = (TextView) mView.findViewById(R.id.billSK_six);
        num7TV = (TextView) mView.findViewById(R.id.billSK_seven);
        num8TV = (TextView) mView.findViewById(R.id.billSK_eight);
        num9TV = (TextView) mView.findViewById(R.id.billSK_nine);
        num0TV = (TextView) mView.findViewById(R.id.billSK_zero);
        dotTV = (TextView) mView.findViewById(R.id.billSK_dot);
        backTV = (TextView) mView.findViewById(R.id.billSK_backspace);
        okTV = (TextView) mView.findViewById(R.id.billSK_ok);
    }
}
