package com.dongxun.lichunkai.cloudmusic.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dongxun.lichunkai.cloudmusic.PopWindow.LoginWindow;
import com.dongxun.lichunkai.cloudmusic.R;
import com.gyf.immersionbar.ImmersionBar;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView_phone;
    private ImageView imageView_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initStateBar();
        initView();
    }

    /**
     * 初始化状态栏
     */
    private void initStateBar() {
        ImmersionBar.with(this).init();
        getSupportActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    /**
     * 初始化组件
     */
    private void initView() {
        imageView_phone = findViewById(R.id.imageView_phone);
        imageView_phone.setOnClickListener(this);
        imageView_email = findViewById(R.id.imageView_email);
        imageView_email.setOnClickListener(this);
    }

    /**
     * 点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView_phone:
                //电话号码登录
                Toast.makeText(this,"电话号码登录",Toast.LENGTH_SHORT).show();
                LoginWindow loginWindow_phone = new LoginWindow(this,"电话号码");
                loginWindow_phone.show();
                break;
            case R.id.imageView_email:
                //电子邮箱登录
                Toast.makeText(this,"电子邮箱登录",Toast.LENGTH_SHORT).show();
                LoginWindow loginWindow_email = new LoginWindow(this,"邮箱");
                loginWindow_email.show();
                break;
        }
    }
}
