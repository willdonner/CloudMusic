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

import com.dongxun.lichunkai.cloudmusic.Common.Common;
import com.dongxun.lichunkai.cloudmusic.LocalBroadcast.SendLocalBroadcast;
import com.dongxun.lichunkai.cloudmusic.PopWindow.LoginWindow;
import com.dongxun.lichunkai.cloudmusic.R;
import com.dongxun.lichunkai.cloudmusic.Util.PhoneFormatCheckUtils;
import com.dongxun.lichunkai.cloudmusic.Util.ToolHelper;
import com.gyf.immersionbar.ImmersionBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "LoginActivity";

    private ImageView imageView_back;
    private Button button_next;
    private LinearLayout LinearLayout_account;
    private LinearLayout LinearLayout_password;
    private LinearLayout LinearLayout_checkPhone;
    private EditText editText_password;
    private EditText editText_account;
    private Button button_login;
    private TextView textView_title;
    private TextView textView_phone;
    private TextView textView_time;

    private String account;
    private String password;

    //验证码倒计时
    private Timer timer;
    private int time = 5;

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
        LinearLayout_checkPhone = findViewById(R.id.LinearLayout_checkPhone);
        LinearLayout_password.setVisibility(View.GONE);
        LinearLayout_checkPhone.setVisibility(View.GONE);
        editText_password = findViewById(R.id.editText_password);
        editText_account = findViewById(R.id.editText_account);
        button_login = findViewById(R.id.button_login);
        button_login.setOnClickListener(this);
        textView_title = findViewById(R.id.textView_title);
        textView_phone = findViewById(R.id.textView_phone);
        textView_time = findViewById(R.id.textView_time);
    }

    /**
     * 检查号码是否已经被注册
     * @param phone
     * @return
     */
    private Boolean checkPhoneRegister(final String phone) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                    //和风请求方式
                    Request request = new Request.Builder()
                            .url("https://45.32.132.229:3000/cellphone/existence/check?phone="+ phone +"")
                            .build();//创建一个Request对象
                    //第三步构建Call对象
                    Call call = client.newCall(request);
                    //第四步:异步get请求
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String responseData = response.body().string();//处理返回的数据
                            Log.e(TAG, "onResponse: "+responseData);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        return false;
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
                    //显示账号布局
                    changeLayoutDisplay(LinearLayout_account);
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
                    Toast.makeText(this,"验证号码是否被注册！",Toast.LENGTH_SHORT).show();
                    if (checkPhoneRegister(account)){
                        //号码已被注册，直接显示密码界面
                        changeLayoutDisplay(LinearLayout_password);
                    }else {
                        //号码未注册，显示手机号验证界面
                        textView_title.setText("手机号验证");
                        changeLayoutDisplay(LinearLayout_checkPhone);
                        textView_phone.setText(account.replace(account.substring(3,7),"****"));
                        //开始一分钟倒计时
                        timer = new Timer();
                        timer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                if (time == 1) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            textView_time.setText("重新发送");
                                            textView_time.setTextColor(Color.parseColor("#5a7b9c"));
                                        }
                                    });
                                    cancel();
                                    return;
                                }
                                Log.e(TAG, "run: "+time);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textView_time.setText(time+"s");
                                    }
                                });
                                time--;
                            }
                        },0,1000);

                    }

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

    /**
     * 更改布局显示
     * @param linearLayout 传入需要显示的布局
     */
    private void changeLayoutDisplay(LinearLayout linearLayout) {
        switch (linearLayout.getId()){
            case R.id.LinearLayout_account:
                //账号
                LinearLayout_account.setVisibility(View.VISIBLE);
                LinearLayout_password.setVisibility(View.GONE);
                LinearLayout_checkPhone.setVisibility(View.GONE);
                break;
            case R.id.LinearLayout_password:
                //密码
                LinearLayout_account.setVisibility(View.GONE);
                LinearLayout_password.setVisibility(View.VISIBLE);
                LinearLayout_checkPhone.setVisibility(View.GONE);
                break;
            case R.id.LinearLayout_checkPhone:
                //验证码
                LinearLayout_account.setVisibility(View.GONE);
                LinearLayout_password.setVisibility(View.GONE);
                LinearLayout_checkPhone.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (LinearLayout_account.getVisibility() == View.GONE) {
            changeLayoutDisplay(LinearLayout_account);
            editText_account.setText(account);
            password = "";
            return;
        }else {
            super.onBackPressed();
        }
    }
}
