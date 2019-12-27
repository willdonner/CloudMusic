package com.dongxun.lichunkai.cloudmusic.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dongxun.lichunkai.cloudmusic.R;

import static android.os.SystemClock.sleep;

public class LunchActivity extends AppCompatActivity implements Animation.AnimationListener {

    AlphaAnimation alphaAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView(R.layout.activity_lunch);
        setTranslucent(this);
        alphaAnimation();
    }

    /**
     * 透明度动画
     */
    private void alphaAnimation() {
        ImageView imageView = findViewById(R.id.imageView);
        TextView textView = findViewById(R.id.textView);
        //设置透明度变化
        alphaAnimation = new AlphaAnimation(0.0f, 1.0f);//第一个参数开始的透明度，第二个参数结束的透明度
        alphaAnimation.setDuration(500);//多长时间完成这个动作
        alphaAnimation.setAnimationListener(this);
        //开始
        imageView.startAnimation(alphaAnimation);
        textView.startAnimation(alphaAnimation);
    }

    /**
     * 隐藏状态栏标题栏
     * @param activity
     */
    public void setTranslucent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //隐藏标题栏
            getSupportActionBar().hide();
        }
    }

    /**
     * 跳转登录
     */
    public void goLogin() {
        Intent intent = new Intent(LunchActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 动画事件
     * @param animation
     */
    @Override
    public void onAnimationStart(Animation animation) {
        //开始
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        //结束
        if (animation.equals(alphaAnimation)){
            new Thread( new Runnable( ) {
                @Override
                public void run() {
                    //耗时任务，比如加载网络数据
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 这里可以睡几秒钟，如果要放广告的话
                            sleep(1000);
                            //权限申请


                        }
                    });
                }
            } ).start();
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        //重复
    }
}
