package com.dongxun.lichunkai.cloudmusic.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.dongxun.lichunkai.cloudmusic.Class.BaseActivity;
import com.dongxun.lichunkai.cloudmusic.R;
import com.gyf.immersionbar.ImmersionBar;

public class WebActivity extends BaseActivity {

    private WebView webView;
    private ImageView imageView_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        initBar();
        initView();
        setWebView();
    }

    private void setWebView() {
        //获取跳转地址
        Intent intent =getIntent();
        String url = intent.getStringExtra("WEBURL");
        //设置地址
        webView.loadUrl(url);
        //系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //使用WebView加载显示url
                view.loadUrl(url);
                //返回true
                return true;
            }
        });
    }

    private void initView() {
        webView = findViewById(R.id.webView);
        imageView_back = findViewById(R.id.imageView_back);
        imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initBar() {
        ImmersionBar.with(this).init();
        getSupportActionBar().hide();
    }
}
