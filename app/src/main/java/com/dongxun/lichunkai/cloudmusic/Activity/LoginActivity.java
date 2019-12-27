package com.dongxun.lichunkai.cloudmusic.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dongxun.lichunkai.cloudmusic.PopWindow.LoginWindow;
import com.dongxun.lichunkai.cloudmusic.R;
import com.dongxun.lichunkai.cloudmusic.Util.PhoneFormatCheckUtils;
import com.dongxun.lichunkai.cloudmusic.Util.ToolHelper;
import com.gyf.immersionbar.ImmersionBar;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView_back;
    private Button button_next;
    private LinearLayout LinearLayout_account;
    private LinearLayout LinearLayout_password;
    private EditText editText_password;
    private EditText editText_account;
    private Button button_login;

    private String account;
    private String password;

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
        imageView_back = findViewById(R.id.imageView_back);
        imageView_back.setOnClickListener(this);
        button_next = findViewById(R.id.button_next);
        button_next.setOnClickListener(this);
        LinearLayout_account = findViewById(R.id.LinearLayout_account);
        LinearLayout_password = findViewById(R.id.LinearLayout_password);
        editText_password = findViewById(R.id.editText_password);
        editText_account = findViewById(R.id.editText_account);
        button_login = findViewById(R.id.button_login);
        button_login.setOnClickListener(this);
    }

    /**
     * 点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView_back:
                if (LinearLayout_account.getVisibility() == View.GONE) {
                    LinearLayout_account.setVisibility(View.VISIBLE);
                    LinearLayout_password.setVisibility(View.GONE);
                    editText_account.setText(account);
                    password = "";
                    return;
                }else finish();
                break;
            case R.id.button_next:
                //验证电话号码
                account = editText_account.getText().toString().trim();
                if (PhoneFormatCheckUtils.isPhoneLegal(account)){
                    Log.e("TAG", "onClick: "+account );
                    LinearLayout_account.setVisibility(View.GONE);
                    LinearLayout_password.setVisibility(View.VISIBLE);
                }else {
                 Toast.makeText(this,"请输入正确的手机号！",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_login:
                //登录
                if (editText_password.getText().toString().trim().equals("")){
                    Toast.makeText(this,"请填写密码！",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this,"登录！",Toast.LENGTH_SHORT).show();
                    button_login.setText("登录中...");
                    //等待提示框

                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (LinearLayout_account.getVisibility() == View.GONE) {
            LinearLayout_account.setVisibility(View.VISIBLE);
            LinearLayout_password.setVisibility(View.GONE);
            editText_account.setText(account);
            password = "";
            return;
        }else {
            super.onBackPressed();
        }
    }
}
