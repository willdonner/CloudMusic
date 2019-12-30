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

import com.dongxun.lichunkai.cloudmusic.Class.BaseActivity;
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

public class LoginActivity extends BaseActivity implements View.OnClickListener {

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
        editText_password = findViewById(R.id.editText_password);
        editText_account = findViewById(R.id.editText_account);
        button_login = findViewById(R.id.button_login);
        button_login.setOnClickListener(this);
        textView_title = findViewById(R.id.textView_title);
        textView_phone = findViewById(R.id.textView_phone);
        textView_time = findViewById(R.id.textView_time);

        changeLayoutDisplay(LinearLayout_account);
    }

    /**
     * 检查号码是否已经被注册
     * @param phone
     * @return
     */
    private void checkPhoneRegister(final String phone) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                    Request request = new Request.Builder()
                            .url("http://www.willdonner.top:3000/cellphone/existence/check?phone="+ phone +"")
                            .build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String responseData = response.body().string();//处理返回的数据
                            Log.e(TAG, "onResponse: "+responseData);
                            //处理JSON
                            //{"exist":1,"nickname":"木子不是楷","hasPassword":true,"code":200}  //已注册
                            //{"exist":-1,"nickname":null,"hasPassword":false,"code":200}   //未注册
                            try {
                                String code = new JSONObject(responseData).getString("code");
                                if (code.equals("200")){
                                    //获取成功
                                    String exist = new JSONObject(responseData).getString("exist"); //是否被注册
                                    if (exist.equals("1")){
                                        //已注册
//                                        String nickname = new JSONObject(responseData).getString("nickname");   //昵称
//                                        Boolean hasPassword = new JSONObject(responseData).getBoolean("hasPassword");   //是否有密码
                                        //显示密码页
                                        changeLayoutDisplay(LinearLayout_password);

                                    }else {
                                        //未注册（发送验证码->设置密码->登录）
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(LoginActivity.this,"该号码未被注册，请去找个注册过的！",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        //修改UI
                                        button_next.setText("下一步");
                                        //弹出软键盘
                                        showInput(editText_account);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 登录
     * @param phone 电话号码
     * @param password 密码
     */
    private void loginWithPhone(final String phone, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                    Request request = new Request.Builder()
                            .url("http://www.willdonner.top:3000/login/cellphone?phone="+  phone +"&password="+ password +"")
                            .build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String responseData = response.body().string();//处理返回的数据
                            Log.e(TAG, "onResponse: "+responseData);
                            //处理JSON
                            //{"msg":"密码错误","code":502,"message":"密码错误"}
                            //{"loginType":1,"code":200,"account":{"id":371561497,"userName":"1_15559655397","type":1,"status":0,"whitelistAuthority":0,"createTime":1480681277427,"salt":"[B@2de8bbf4","tokenVersion":3,"ban":0,"baoyueVersion":0,"donateVersion":0,"vipType":0,"viptypeVersion":0,"anonimousUser":false},"token":"09f4336e5ab6314a852ac627bc1698687e588a7c819d66d69465c79d6a63433a39ce82f58c69adffc872c7103b0c250525dbaa2522b3e31a","profile":{"userId":371561497,"vipType":0,"gender":0,"accountStatus":0,"avatarImgId":109951163812537760,"birthday":631123200000,"nickname":"木子不是楷","city":530100,"userType":0,"backgroundImgId":2002210674180203,"province":530000,"defaultAvatar":false,"avatarUrl":"https://p3.music.126.net/7vjHrzwzx0TShFOeSQAZVA==/109951163812537760.jpg","djStatus":0,"experts":{},"mutual":false,"remarkName":null,"expertTags":null,"authStatus":0,"detailDescription":"","followed":false,"backgroundUrl":"http://p2.music.126.net/bmA_ablsXpq3Tk9HlEg9sA==/2002210674180203.jpg","avatarImgIdStr":"109951163812537760","backgroundImgIdStr":"2002210674180203","description":"","signature":"","authority":0,"avatarImgId_str":"109951163812537760","followeds":0,"follows":7,"eventCount":1,"playlistCount":3,"playlistBeSubscribedCount":0},"bindings":[{"userId":371561497,"tokenJsonStr":"{\"countrycode\":\"\",\"cellphone\":\"15559655397\",\"hasPassword\":true}","bindingTime":1480681311099,"expiresIn":2147483647,"expired":false,"refreshTime":1480681311,"url":"","id":2930400000,"type":1},{"userId":371561497,"tokenJsonStr":"{\"access_token\":\"27A398785CE42F8A7868246A20FE9608\",\"openid\":\"D4997CAD716FCDE61F8E99ED52C1DDF2\",\"query_authority_cost\":98,\"nickname\":\"木子不是楷\",\"partnerType\":\"0\",\"expires_in\":7776000,\"login_cost\":111,\"authority_cost\":5482}","bindingTime":1480681277433,"expiresIn":7776000,"expired":false,"refreshTime":1577518661,"url":"","id":2930400001,"type":5},{"userId":371561497,"tokenJsonStr":"{\"uid\":\"4403a91540ef1236a96460a1f011efbf\"}","bindingTime":1482153980097,"expiresIn":2147483647,"expired":false,"refreshTime":1482153980,"url":"","id":2938801690,"type":11}]}
                            try {
                                JSONObject newResponse = new JSONObject(responseData);
                                String code = newResponse.getString("code");
                                if (code.equals("200")){
                                    //密码正确
                                    Common.loginJSONOString = responseData;
                                    //跳转主页
                                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                    startActivity(intent);
                                }else {
                                    //密码错误
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(LoginActivity.this,"密码错误！",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
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
                    button_next.setText("下一步");
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
                    //关闭输入法
                    hideInput();
                    //等待
                    button_next.setText("稍等...");
                    //发起号码验证请求
                    checkPhoneRegister(account);
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
                    //发起登录请求
                    account = editText_account.getText().toString().trim();
                    password = editText_password.getText().toString().trim();
                    loginWithPhone(account,password);
                }
                break;
        }
    }

    /**
     * 隐藏键盘
     */
    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /**
     * 显示键盘
     *
     * @param et 输入焦点
     */
    public void showInput(final EditText et) {
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 更改布局显示
     * @param linearLayout 传入需要显示的布局
     */
    private void changeLayoutDisplay(final LinearLayout linearLayout) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
                        //弹出输入法
                        showInput(editText_password);
                        break;
                    case R.id.LinearLayout_checkPhone:
                        //验证码
                        LinearLayout_account.setVisibility(View.GONE);
                        LinearLayout_password.setVisibility(View.GONE);
                        LinearLayout_checkPhone.setVisibility(View.VISIBLE);
                        //弹出输入法
//                        showInput(editText_password);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (LinearLayout_account.getVisibility() == View.GONE) {
            changeLayoutDisplay(LinearLayout_account);
            editText_account.setText(account);
            button_next.setText("下一步");
            password = "";
            return;
        }else {
            super.onBackPressed();
        }
    }
}
