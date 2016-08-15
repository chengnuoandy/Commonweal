package com.goldenratio.commonweal.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.goldenratio.commonweal.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class HelpTopDetailActivity extends Activity {

    @BindView(R.id.iv_helpTop_back)
    ImageView mIvHelpTopBack;
    @BindView(R.id.wv_helptop)
    WebView mWvHelptop;

    private ProgressDialog mPd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_top_detail);
        ButterKnife.bind(this);

        String helpTopUrl = getIntent().getStringExtra("TopUrl");

        mWvHelptop.getSettings().setJavaScriptEnabled(true);//设置使用够执行JS脚本
        mWvHelptop.getSettings().setBuiltInZoomControls(false);//设置使支持缩放
        mWvHelptop.loadUrl(helpTopUrl);

        mWvHelptop.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mWvHelptop.setWebChromeClient(new WebChromeClient() {  //设置web浏览器客户端
            @Override
            public void onProgressChanged(WebView view, int newProgress) {  //进度条改变事件
                if (newProgress == 100) {
                    closeProgressDialog();
                } else {
                    showProgressDialog();
                }
            }
        });
    }


    @OnClick(R.id.iv_helpTop_back)
    public void onClick() {
        finish();
    }

    private void showProgressDialog() {
        if (mPd == null) {
            mPd = new ProgressDialog(this);
            mPd.setMessage("加载中");
            mPd.setCancelable(true);
            mPd.show();
        }
    }

    private void closeProgressDialog() {
        if (mPd != null && mPd.isShowing()) {
            mPd.dismiss();
            mPd = null;
        }
    }
}
