package com.dongxun.lichunkai.cloudmusic.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dongxun.lichunkai.cloudmusic.Class.BaseActivity;
import com.dongxun.lichunkai.cloudmusic.Common.Common;
import com.dongxun.lichunkai.cloudmusic.Dialog.PermissionDialog;
import com.dongxun.lichunkai.cloudmusic.R;
import com.dongxun.lichunkai.cloudmusic.Util.PermissionUtil;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.os.SystemClock.sleep;
import static com.dongxun.lichunkai.cloudmusic.Util.ToolHelper.saveAccount;
import static com.dongxun.lichunkai.cloudmusic.Util.ToolHelper.showToast;

/**
 * 启动页
 */
public class LunchActivity extends BaseActivity implements Animation.AnimationListener, View.OnClickListener {

    private ImageView imageView_logo;
    private TextView textView_name;
    private Button button_phoneLogin;
    private SharedPreferences sp;
    private Activity context;
    //请求状态
    private Boolean isRequesting = false;
    private String TAG = "LunchActivity";

    private AlphaAnimation alphaAnimation_appear;//logo及下方字体透明度动画（出现）
    private AlphaAnimation alphaAnimation_hide;//logo及下方字体透明度动画（消失）
    private TranslateAnimation translateAnimation_logo;//logo平移动画
    private AlphaAnimation alphaAnimation_loginAppear;//登录透明度动画（出现）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView(R.layout.activity_lunch);
        initBar();
        initView();
        initAnimation();
        //透明度动画
        startAnimation();
        sp = getSharedPreferences("Login",Context.MODE_PRIVATE);
        String a = sp.getString("Account", "");
        String p = sp.getString("Password", "");
        if(sp!=null) {
            if (sp.getBoolean("LoginBool", false)) {
                loginWithPhone(sp.getString("Account", ""), sp.getString("Password", ""));
            }
        }
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
                            try {
                                JSONObject newResponse = new JSONObject(responseData);
                                String code = newResponse.getString("code");
                                if (code.equals("200")){

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

                                    //跳转主页
                                    Intent intent = new Intent(LunchActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    showToast(LunchActivity.this,"用户名或密码错误");
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
     * 初始化组件
     */
    private void initView() {
        imageView_logo = findViewById(R.id.imageView_logo);
        textView_name = findViewById(R.id.textView_name);
        button_phoneLogin = findViewById(R.id.button_phoneLogin);
        button_phoneLogin.setOnClickListener(this);
    }

    /**
     * 初始化动画
     */
    private void initAnimation() {
        //透明度（显示）
        alphaAnimation_appear = new AlphaAnimation(0.0f, 1.0f);//第一个参数开始的透明度，第二个参数结束的透明度
        alphaAnimation_appear.setDuration(500);//多长时间完成这个动作
        alphaAnimation_appear.setAnimationListener(this);
        //透明度（隐藏）
        alphaAnimation_hide = new AlphaAnimation(1.0f, 0.0f);//第一个参数开始的透明度，第二个参数结束的透明度
        alphaAnimation_hide.setDuration(500);//多长时间完成这个动作
        alphaAnimation_hide.setAnimationListener(this);
        //logo平移动画
        translateAnimation_logo = new TranslateAnimation(0,0, 0, -400);//前两个参数是设置x轴的起止位置，后两个参数设置y轴的起止位置
        translateAnimation_logo.setDuration(500);
        translateAnimation_logo.setFillAfter(true);
        //登录模块透明度（显示）
        alphaAnimation_loginAppear = new AlphaAnimation(0.0f, 1.0f);//第一个参数开始的透明度，第二个参数结束的透明度
        alphaAnimation_loginAppear.setDuration(800);//多长时间完成这个动作
        alphaAnimation_loginAppear.setAnimationListener(this);
    }

    /**
     * 初始化标题栏状态栏
     */
    private void initBar() {
        //隐藏标题栏
        getSupportActionBar().hide();
        //隐藏状态栏
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).init();
    }

    /**
     * 启动动画
     */
    private void startAnimation() {
        //启动动画
        imageView_logo.startAnimation(alphaAnimation_appear);
        textView_name.startAnimation(alphaAnimation_appear);
    }

    /**
     * 动画事件
     * @param animation
     */
    @Override
    public void onAnimationStart(Animation animation) {
    }
    @Override
    public void onAnimationEnd(Animation animation) {
        //结束
        if (animation.equals(alphaAnimation_appear)){   //logo及name显示
            showPermissionDialog();
        }
        if (animation.equals(alphaAnimation_hide)){   //name消失
            textView_name.setVisibility(View.GONE);
        }
        if (animation.equals(alphaAnimation_loginAppear)){   //logo平移
            button_phoneLogin.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    /**
     * 权限
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 502:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    hideLogo();
                }else {
                    Toast.makeText(this,"用户拒绝了权限申请",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    /**
     * 移动logo及其隐藏Name
     */
    public void hideLogo() {
        //显示状态栏
        ImmersionBar.with(this).reset().init();
        //下方字体消失动画
        textView_name.startAnimation(alphaAnimation_hide);
        //logo平移动画
        imageView_logo.startAnimation(translateAnimation_logo);
        //登录按钮出现
        button_phoneLogin.startAnimation(alphaAnimation_loginAppear);
    }

    /**
     * 获取权限对话框
     */
    public void showPermissionDialog() {
        new Thread( new Runnable( ) {
            @Override
            public void run() {
                //耗时任务，比如加载网络数据
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 这里可以睡几秒钟，如果要放广告的话
                        sleep(500);
                        //权限申请
                        if (ContextCompat.checkSelfPermission(LunchActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED){
                            new PermissionDialog(LunchActivity.this).builder().setTitle("云音乐权限申请")
                                    .setMsg("云音乐需要获取存储空间和设备信息权限，以保证歌曲正常播放下载以及您的帐号安全。")
                                    .setCancelable(false)
                                    .setPositiveButton("授权", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //获取权限
                                            PermissionUtil.getInstance().requestSD(LunchActivity.this);
                                        }
                                    }).setNegativeButton("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //退出
                                    finish();
                                }
                            }).show();
                        }else {
                            hideLogo();
                        }
                    }
                });
            }
        } ).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_phoneLogin:
                Intent intent = new Intent(LunchActivity.this,LoginActivity.class);
                startActivity(intent);
                break;
        }
    }
}
