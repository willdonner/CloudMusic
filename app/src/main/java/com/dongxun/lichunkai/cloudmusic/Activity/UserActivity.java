package com.dongxun.lichunkai.cloudmusic.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.dongxun.lichunkai.cloudmusic.Adapter.MainPagerAdapter;
import com.dongxun.lichunkai.cloudmusic.R;
import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户主页（自己和其他用户）
 */
public class UserActivity extends AppCompatActivity {

    private String TAG = "UserActivity";

    //viewpager相关
    private View view_index, view_moments;
    private ViewPager viewPager;  //对应的viewPager
    private List<View> viewList;//view数组

    //view_index组件
    private TextView textView_index;
    private TextView textView_moment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        initStateBar();
        initView();
        setViewpager();
    }

    private void initView() {
        //实例化view
        viewPager = findViewById(R.id.viewpager);
        LayoutInflater inflater=getLayoutInflater();
        view_index = inflater.inflate(R.layout.viewpager_user_index, null);
        view_moments = inflater.inflate(R.layout.viewpager_user_moments,null);

        //view_index组件
        textView_index = findViewById(R.id.textView_index);
        textView_moment = findViewById(R.id.textView_moment);
    }

    /**
     * 设置Viewpager
     */
    private void setViewpager() {
        viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
        viewList.add(view_index);
        viewList.add(view_moments);

        //设置PagerAdapter
        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(viewList,this);
        viewPager.setAdapter(mainPagerAdapter);
        //监听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             * 页面滑动状态停止前一直调用
             *
             * @param position 当前点击滑动页面的位置
             * @param positionOffset 当前页面偏移的百分比
             * @param positionOffsetPixels 当前页面偏移的像素位置
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.e("vp", "滑动中=====position:" + position + "   positionOffset:" + positionOffset + "   positionOffsetPixels:" + positionOffsetPixels);
            }

            /**
             * 滑动后显示的页面和滑动前不同，调用
             *
             * @param position 选中显示页面的位置
             */
            @Override
            public void onPageSelected(int position) {
                Log.e(TAG, "显示页改变=====postion:" + position);
                //设置标题样式
                switch (position){
                    case 0:
                        textView_index.setTextColor(Color.parseColor("#fe3a3b"));
                        textView_moment.setTextColor(Color.parseColor("#000000"));
                        break;
                    case 1:
                        textView_index.setTextColor(Color.parseColor("#000000"));
                        textView_moment.setTextColor(Color.parseColor("#fe3a3b"));
                        break;
                }
            }

            /**
             * 页面状态改变时调用
             * @param state 当前页面的状态
             *              SCROLL_STATE_IDLE：空闲状态
             *              SCROLL_STATE_DRAGGING：滑动状态
             *              SCROLL_STATE_SETTLING：滑动后滑翔的状态
             */
            @Override
            public void onPageScrollStateChanged(int state) {
//                switch (state) {
//                    case ViewPager.SCROLL_STATE_IDLE:
//                        Log.e("vp", "状态改变=====SCROLL_STATE_IDLE====静止状态");
//                        break;
//                    case ViewPager.SCROLL_STATE_DRAGGING:
//                        Log.e("vp", "状态改变=====SCROLL_STATE_DRAGGING==滑动状态");
//                        break;
//                    case ViewPager.SCROLL_STATE_SETTLING:
//                        Log.e("vp", "状态改变=====SCROLL_STATE_SETTLING==滑翔状态");
//                        break;
//                }
            }
        });
    }

    /**
     * 初始化状态栏
     */
    private void initStateBar() {
        ImmersionBar.with(this).init();
        getSupportActionBar().hide();
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//黑色状态栏字体
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);//白色状态栏字体
    }
}
