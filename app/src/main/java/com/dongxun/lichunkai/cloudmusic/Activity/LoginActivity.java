package com.dongxun.lichunkai.cloudmusic.Activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dongxun.lichunkai.cloudmusic.Bean.User;
import com.dongxun.lichunkai.cloudmusic.Class.BaseActivity;
import com.dongxun.lichunkai.cloudmusic.Common.Common;
import com.dongxun.lichunkai.cloudmusic.R;
import com.dongxun.lichunkai.cloudmusic.Util.PhoneFormatCheckUtils;
import com.dongxun.lichunkai.cloudmusic.Util.ToolHelper;
import com.gyf.immersionbar.ImmersionBar;

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

import static com.dongxun.lichunkai.cloudmusic.Util.ToolHelper.getAccount;
import static com.dongxun.lichunkai.cloudmusic.Util.ToolHelper.saveAccount;
import static com.dongxun.lichunkai.cloudmusic.Util.ToolHelper.showToast;

/**
 * 登录页
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private String TAG = "LoginActivity";

    private ImageView imageView_back;
    private Button button_next;
    private LinearLayout LinearLayout_account;
    private LinearLayout LinearLayout_password;
    private LinearLayout LinearLayout_checkPhone;
    private LinearLayout LinearLayout_forgetPassword;
    private EditText editText_password;
    private EditText editText_account;
    private Button button_login;
    private TextView textView_title;
    private TextView textView_phone;
    private TextView textView_time;
    private TextView textView_forgetPassword;
    private Button button_forgetPwdNext;
    private EditText editText_forgetPassword;
    private EditText editText_checkCode;
    private TextView textView_checkCode1;
    private TextView textView_checkCode2;
    private TextView textView_checkCode3;
    private TextView textView_checkCode4;

    private String account;
    private String password;
    private String newPassword;

    //验证码倒计时
    private Timer timer;
    private int time = 60;

    //请求状态
    private Boolean isRequesting = false;

    //账号信息
    private String nickName = "";

    //记住账号SharePreferences对象
    private SharedPreferences sp;

  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initStateBar();
        sp = getSharedPreferences("Login",Context.MODE_PRIVATE);
        account = sp.getString("Account","");
        password = sp.getString("Password","");
        if(sp==null){
            initView();
        }
        else{
            // 判断是否刚注销
            if (sp.getBoolean("LoginBool", false)) {
                loginWithPhone(account,password);
            } else {
                initView();
            }
        }
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
        LinearLayout_forgetPassword = findViewById(R.id.LinearLayout_forgetPassword);
        editText_password = findViewById(R.id.editText_password);
        editText_account = findViewById(R.id.editText_account);
        button_login = findViewById(R.id.button_login);
        button_login.setOnClickListener(this);
        textView_title = findViewById(R.id.textView_title);
        textView_phone = findViewById(R.id.textView_phone);
        textView_time = findViewById(R.id.textView_time);
        textView_time.setOnClickListener(this);
        textView_forgetPassword = findViewById(R.id.textView_forgetPassword);
        textView_forgetPassword.setOnClickListener(this);
        button_forgetPwdNext = findViewById(R.id.button_forgetPwdNext);
        button_forgetPwdNext.setOnClickListener(this);
        editText_forgetPassword = findViewById(R.id.editText_forgetPassword);

        textView_checkCode1 = findViewById(R.id.textView_checkCode1);
        textView_checkCode1.setOnClickListener(this);
        textView_checkCode2 = findViewById(R.id.textView_checkCode2);
        textView_checkCode2.setOnClickListener(this);
        textView_checkCode3 = findViewById(R.id.textView_checkCode3);
        textView_checkCode3.setOnClickListener(this);
        textView_checkCode4 = findViewById(R.id.textView_checkCode4);
        textView_checkCode4.setOnClickListener(this);
        editText_checkCode = findViewById(R.id.editText_checkCode);
        editText_checkCode.addTextChangedListener(this);

        //布局显示
        changeLayoutDisplay(LinearLayout_account);
        if (getAccount(this).length()!=0){
            //读取历史账号
            editText_account.setText(getAccount(this));
        }else {
            //弹出键盘
            showInput(editText_account);
        }
    }

    /**
     * 检查号码是否已经被注册
     * @param phone
     * @return
     */
    private void checkPhoneRegister(final String phone) {
        isRequesting = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                    Request request = new Request.Builder()
                            .url("https://neteasecloudmusicapi.willdonner.top/cellphone/existence/check?phone="+ phone +"")
                            .build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            isRequesting = false;
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
                                        nickName = new JSONObject(responseData).getString("nickname");   //昵称
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
        isRequesting = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                    Request request = new Request.Builder()
                            .url("https://neteasecloudmusicapi.willdonner.top/login/cellphone?phone="+  phone +"&password="+ password +"")
                            .build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            isRequesting = false;
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
                                    //保存此次登录账号
                                    saveAccount(LoginActivity.this,account);
                                    Common.loginJSONOString = responseData;
                                    //解析信息
                                    JSONObject profile = newResponse.getJSONObject("profile");
                                    String userId = profile.getString("userId");//用户ID
                                    String gender = profile.getString("gender");//性别
                                    String birthday = profile.getString("birthday");//生日
                                    String nickname = profile.getString("nickname");//昵称
                                    String city = profile.getString("city");//城市
                                    String province = profile.getString("province");//省份
                                    String avatarUrl = profile.getString("avatarUrl");//头像Url
                                    String backgroundUrl = profile.getString("backgroundUrl");//背景图Url

                                    Common.user.setUserId(userId);
                                    Common.user.setGender(gender);
                                    Common.user.setBirthday(birthday);
                                    Common.user.setNickname(nickname);
                                    Common.user.setCity(city);
                                    Common.user.setProvince(province);
                                    Common.user.setAvatarUrl(avatarUrl);
                                    Common.user.setBackgroundUrl(backgroundUrl);

                                    sp = getSharedPreferences("Login",Context.MODE_PRIVATE);
                                    if (sp.getBoolean("LoginBool", true)) {
                                        Editor editor = sp.edit();
                                        editor.putString("Account",editText_account.getText().toString());
                                        editor.putString("Password",editText_password.getText().toString());
                                        editor.putBoolean("LoginBool",true);
                                        editor.commit();
                                    }


                                    //跳转主页
                                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    //密码错误
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showToast(LoginActivity.this,"用户名或密码错误");
                                            button_login.setText("登录");
                                            showInput(editText_password);
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
     * 发送验证码
     * @param phone
     */
    private void sendCheckCode(final String phone) {
        isRequesting = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                    Request request = new Request.Builder()
                            .url("https://neteasecloudmusicapi.willdonner.top/captcha/sent?phone="+  phone +"")
                            .build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            isRequesting = false;
                            final String responseData = response.body().string();//处理返回的数据
                            Log.e(TAG, "onResponse: "+responseData);
                            //{"code":200}
                            try {
                                JSONObject newResponse = new JSONObject(responseData);
                                String code = newResponse.getString("code");
                                if (code.equals("200")){
                                    //发送成功
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(LoginActivity.this,"验证码已发送！",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else {
                                    //发送失败
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(LoginActivity.this,"验证码发送失败！",Toast.LENGTH_SHORT).show();
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
     * 修改密码/注册(注册需要nickname参数)，返回参数和登录一致
     * @param phone 电话号码
     * @param checkCode 验证码
     * @param password  密码
     */
    private void changePassword(final String phone, final String checkCode, final String password) {
        isRequesting = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                    Request request = new Request.Builder()
                            .url("https://neteasecloudmusicapi.willdonner.top/register/cellphone?phone="+ phone +"&password="+ password +"&captcha="+ checkCode +"")
                            .build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            isRequesting = false;
                            final String responseData = response.body().string();//处理返回的数据
                            copy(responseData);
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
                                    //密码修改错误
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(LoginActivity.this,"密码修改出错！",Toast.LENGTH_SHORT).show();
                                            changeLayoutDisplay(LinearLayout_forgetPassword);
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
     * 复制内容到剪切板
     *
     * @param copyStr
     * @return
     */
    private boolean copy(String copyStr) {
        try {
            //获取剪贴板管理器
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", copyStr);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView_back:
                backManager();
                break;
            case R.id.button_next:
                if (isRequesting) return;
                //验证电话号码
                account = editText_account.getText().toString().trim();
                if (PhoneFormatCheckUtils.isPhoneLegal(account)){
//                    Log.e("TAG", "onClick: "+account );
//                    Toast.makeText(this,"验证号码是否被注册！",Toast.LENGTH_SHORT).show();
                    //关闭输入法
                    hideInput();
                    //等待
                    button_next.setText("稍等...");
                    //发起号码验证请求
                    checkPhoneRegister(account);
                }else {
                    showToast(this,"请输入11位数的手机号");
                }
                break;
            case R.id.button_login:
                if (isRequesting) return;
                //登录
                if (editText_password.getText().toString().trim().equals("")){
                    showToast(this,"请输入密码");
                }else {
//                    Toast.makeText(this,"登录！",Toast.LENGTH_SHORT).show();
                    button_login.setText("登录中...");
                    hideInput();
                    //发起登录请求
                    account = editText_account.getText().toString().trim();
                    password = editText_password.getText().toString().trim();
                    loginWithPhone(account,password);
                }
                break;
            case R.id.textView_forgetPassword:
                //忘记密码
                password = editText_password.getText().toString();
                changeLayoutDisplay(LinearLayout_forgetPassword);
                showInput(editText_forgetPassword);
                break;
            case R.id.button_forgetPwdNext:
                if (isRequesting) return;
                //忘记密码模块，下一步
                //密码要求：不少于6位
                newPassword = editText_forgetPassword.getText().toString();
                if (newPassword.length() >= 6) {
                    changeLayoutDisplay(LinearLayout_checkPhone);
                    //发送验证码
                    textView_phone.setText(account.replace(account.substring(3,7),"****"));
                    sendCheckCode(account);
                    //倒计时
                    countDown();
                }else {
                    showToast(LoginActivity.this,"密码应不少于6位");
                }
                break;
            case R.id.textView_checkCode1: case R.id.textView_checkCode2:case R.id.textView_checkCode3:case R.id.textView_checkCode4:
                //弹出输入框
                showInput(editText_checkCode);
                break;
            case R.id.textView_time:
                //重新发送验证码
                sendCheckCode(account);
                countDown();
                break;
        }
    }

    /**
     * 倒计时
     */
    private void countDown() {
        textView_time.setTextColor(Color.parseColor("#cccccc"));
        time = 60;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (time>=1){
                    Log.e(TAG, "run: "+time);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView_time.setText(time+"s");
                        }
                    });
                    time--;
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cancel();
                            textView_time.setText("重新发送");
                            textView_time.setTextColor(Color.parseColor("#5f7a98"));
                        }
                    });
                }
            }
        },0,1000);
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
                        textView_title.setText("手机号登录");
                        LinearLayout_account.setVisibility(View.VISIBLE);
                        LinearLayout_password.setVisibility(View.GONE);
                        LinearLayout_checkPhone.setVisibility(View.GONE);
                        LinearLayout_forgetPassword.setVisibility(View.GONE);
                        break;
                    case R.id.LinearLayout_password:
                        //密码
                        textView_title.setText("手机号登录");
                        LinearLayout_account.setVisibility(View.GONE);
                        LinearLayout_password.setVisibility(View.VISIBLE);
                        LinearLayout_checkPhone.setVisibility(View.GONE);
                        LinearLayout_forgetPassword.setVisibility(View.GONE);
                        //弹出输入法
                        showInput(editText_password);
                        break;
                    case R.id.LinearLayout_checkPhone:
                        //验证码
                        textView_title.setText("手机号验证");
                        LinearLayout_account.setVisibility(View.GONE);
                        LinearLayout_password.setVisibility(View.GONE);
                        LinearLayout_checkPhone.setVisibility(View.VISIBLE);
                        LinearLayout_forgetPassword.setVisibility(View.GONE);
                        //弹出输入法(第一个输入框)
                        showInput(editText_checkCode);
                        break;
                    case R.id.LinearLayout_forgetPassword:
                        //忘记密码
                        textView_title.setText("忘记密码");
                        LinearLayout_account.setVisibility(View.GONE);
                        LinearLayout_password.setVisibility(View.GONE);
                        LinearLayout_checkPhone.setVisibility(View.GONE);
                        LinearLayout_forgetPassword.setVisibility(View.VISIBLE);
                        //弹出输入法
                        showInput(editText_password);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 返回键管理器
     */
    private void backManager() {
        if (LinearLayout_account.getVisibility() == View.VISIBLE) {
            //账号页返回启动页
            super.onBackPressed();
        }else if (LinearLayout_password.getVisibility() == View.VISIBLE){
            //密码页返回账号页
            password = "";
            editText_account.setText(account);
            button_next.setText("下一步");
            changeLayoutDisplay(LinearLayout_account);
        }else if (LinearLayout_checkPhone.getVisibility() == View.VISIBLE){
            //验证码页返回忘记密码页
            textView_checkCode1.setText("");
            textView_checkCode2.setText("");
            textView_checkCode3.setText("");
            textView_checkCode4.setText("");
            editText_forgetPassword.setText("");
            changeLayoutDisplay(LinearLayout_forgetPassword);
        }else if (LinearLayout_forgetPassword.getVisibility() == View.VISIBLE){
            //忘记密码页返回密码页
            newPassword = "";
            changeLayoutDisplay(LinearLayout_password);
        }
    }


    @Override
    public void onBackPressed() {
        backManager();
    }

    /**
     * 输入框监听
     * @param s
     * @param start
     * @param count
     * @param after
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String checkCode = s.toString();
        String code1 = checkCode.length()>=1?checkCode.substring(0,1):"";
        String code2 = checkCode.length()>=2?checkCode.substring(1,2):"";
        String code3 = checkCode.length()>=3?checkCode.substring(2,3):"";
        String code4 = checkCode.length()>=4?checkCode.substring(3,4):"";
        textView_checkCode1.setText(code1);
        textView_checkCode2.setText(code2);
        textView_checkCode3.setText(code3);
        textView_checkCode4.setText(code4);
        if (checkCode.length() == 4) {
            //修改密码
            changePassword(editText_account.getText().toString().trim(),checkCode,editText_forgetPassword.getText().toString().trim());
            hideInput();
            timer.cancel();
        }
    }
    @Override
    public void afterTextChanged(Editable s) {
    }
}
