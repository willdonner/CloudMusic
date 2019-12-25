package com.dongxun.lichunkai.cloudmusic.PopWindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dongxun.lichunkai.cloudmusic.R;
import com.gyf.immersionbar.ImmersionBar;

public class ListWindow extends PopupWindow implements View.OnClickListener {

    private String TAG = "ShareMenu";
    private Context mContext;
    private View mMenuView;

    private TextView content1;
    private TextView textView_author;
    private ImageView imageView_close;
    private ImageView imageView_coverImg;
    private SeekBar seekBar;
    private ImageView imageView_lastSong;
    private ImageView imageView_playOrPause;
    private ImageView imageView_nextSong;
    private ImageView imageView_like;
    private ImageView imageView_loop;
    private ImageView imageView_comments;
    private ImageView imageView_list;

    private LocalBroadcastManager mLocalBroadcastManager; //创建本地广播管理器类变量

    public ListWindow(final Context context) {
        super(context);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popwindow_list, null);
        setContentView(mMenuView);

        //设置弹窗宽度
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置弹窗高度
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 取得焦点
        this.setFocusable(true);
        //点击空白消失
        this.setOutsideTouchable(true);
        //设置可以点击
        this.setTouchable(true);
        //进入退出的动画，指定刚才定义的style
        this.setAnimationStyle(R.style.mypopwindow_anim_style);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

        initView();
        refreshUI();



    }

    @Override
    public void dismiss() {
        //还原状态栏样式
        ImmersionBar.with((Activity) mContext).statusBarColor(R.color.white).init();
        super.dismiss();
    }

    /**
     * 更新UI
     */
    private void refreshUI() {

    }

    /**
     * 初始化组件
     */
    private void initView() {
    }

    /**
     * 弹出popWindow
     */
    public void show() {
        View rootview = LayoutInflater.from(mContext).inflate(R.layout.activity_main, null);
        showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
//            case R.id.content1:
//                dismiss();
//                break;
        }
    }
}