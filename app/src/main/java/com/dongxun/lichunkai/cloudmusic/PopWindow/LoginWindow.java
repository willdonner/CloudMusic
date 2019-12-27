package com.dongxun.lichunkai.cloudmusic.PopWindow;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dongxun.lichunkai.cloudmusic.Adapter.SongListAdapter;
import com.dongxun.lichunkai.cloudmusic.Bean.Song;
import com.dongxun.lichunkai.cloudmusic.Common.Common;
import com.dongxun.lichunkai.cloudmusic.LocalBroadcast.SendLocalBroadcast;
import com.dongxun.lichunkai.cloudmusic.R;

import java.util.ArrayList;
import java.util.List;

public class LoginWindow extends PopupWindow implements View.OnClickListener {

    private String TAG = "ShareMenu";
    private Context mContext;
    private View mMenuView;

    private EditText editText_account;
    private TextView textView_loginStyle;
    private TextView editText_password;
    private Button button_login;

    private List<Song> songList = new ArrayList<>();


    public LoginWindow(final Context context,String loginStyle) {
        super(context);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popwindow_login, null);
        setContentView(mMenuView);

        //设置弹窗宽度
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
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
        refreshUI(loginStyle);
    }



    /**
     * 更新UI
     */
    private void refreshUI(String loginStyle) {
        textView_loginStyle.setText(loginStyle+"登录");
        editText_account.setHint(loginStyle);
    }

    /**
     * 初始化组件
     */
    private void initView() {
        editText_account = mMenuView.findViewById(R.id.editText_account);
        textView_loginStyle = mMenuView.findViewById(R.id.textView_loginStyle);
        editText_password = mMenuView.findViewById(R.id.editText_password);
        button_login = mMenuView.findViewById(R.id.button_login);
        button_login.setOnClickListener(this);
    }

    /**
     * 弹出popWindow
     */
    public void show() {
        View rootview = LayoutInflater.from(mContext).inflate(R.layout.activity_main, null);
        showAtLocation(rootview, Gravity.CENTER, 0, 0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_login:
                //登录

                break;
        }
    }

}