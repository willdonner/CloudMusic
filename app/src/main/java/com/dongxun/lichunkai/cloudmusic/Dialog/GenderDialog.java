package com.dongxun.lichunkai.cloudmusic.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dongxun.lichunkai.cloudmusic.Bean.User;
import com.dongxun.lichunkai.cloudmusic.R;


/**
 * 自定义性别选择器
 */

public class GenderDialog extends Dialog {

    private TextView textView_female;//男
    private TextView textView_male;//女
    private ImageView imageView_femaleHook;//男勾图标
    private ImageView imageView_maleHook;//女勾图标
    private User user;

    public GenderDialog(Context context, User user) {
        super(context, R.style.CustomDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_gender);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(true);
        //初始化界面控件
        initView();
        //初始化界面控件的事件
        initEvent();
        //刷新界面
        refresh();
    }

    /**
     * 更改界面组件显示
     */
    private void refresh() {
        if (user.getGender().equals("0")){
            //保密
            imageView_femaleHook.setVisibility(View.GONE);
            imageView_maleHook.setVisibility(View.GONE);
        }else if (user.getGender().equals("1")){
            //男
            imageView_maleHook.setVisibility(View.GONE);
        }else if (user.getGender().equals("2")){
            //女
            imageView_femaleHook.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        textView_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( onClickBottomListener!= null) {
                    onClickBottomListener.onFemaleClick();
                }
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        textView_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( onClickBottomListener!= null) {
                    onClickBottomListener.onMaleClick();
                }
            }
        });
    }



    @Override
    public void show() {
        super.show();
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        textView_female = (TextView) findViewById(R.id.textView_female);
        textView_male = (TextView) findViewById(R.id.textView_male);
        imageView_femaleHook = (ImageView) findViewById(R.id.imageView_femaleHook);
        imageView_maleHook = (ImageView) findViewById(R.id.imageView_maleHook);
    }

    /**
     * 设置确定取消按钮的回调
     */
    public OnClickBottomListener onClickBottomListener;
    public GenderDialog setOnClickBottomListener(OnClickBottomListener onClickBottomListener) {
        this.onClickBottomListener = onClickBottomListener;
        return this;
    }

    public interface OnClickBottomListener{
        /**
         * 点击确定按钮事件
         */
        public void onFemaleClick();
        /**
         * 点击取消按钮事件
         */
        public void onMaleClick();
    }

    /**
     * 设置性别显示
     * @param user
     * @return
     */
    public GenderDialog getUser(User user) {
        this.user = user;
        return this;
    }

}

