package com.dongxun.lichunkai.cloudmusic.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dongxun.lichunkai.cloudmusic.Bean.User;
import com.dongxun.lichunkai.cloudmusic.Class.CircleImageView;
import com.dongxun.lichunkai.cloudmusic.Common.Common;
import com.dongxun.lichunkai.cloudmusic.Dialog.GenderDialog;
import com.dongxun.lichunkai.cloudmusic.R;
import com.dongxun.lichunkai.cloudmusic.Util.CityAndCodeUtil;
import com.dongxun.lichunkai.cloudmusic.Util.ProvinceAndCodeUtil;
import com.dongxun.lichunkai.cloudmusic.Util.ToolHelper;
import com.gyf.immersionbar.ImmersionBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import static com.dongxun.lichunkai.cloudmusic.Util.ToolHelper.showToast;

public class EditMyInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "EditMyInfoActivity";

    private CircleImageView CircleImageView_head;
    private TextView textView_name;
    private TextView textView_gender;
    private ImageView imageView_QRCode;
    private TextView textView_birthday;
    private TextView textView_place;
    private TextView textView_school;
    private TextView textView_signature;
    private ImageView imageView_back;

    private TextView textView_title;
    private TextView textView_save;
    private LinearLayout LinearLayout_main;
    private LinearLayout LinearLayout_head;
    private LinearLayout LinearLayout_nickName;
    private LinearLayout LinearLayout_gender;
    private LinearLayout LinearLayout_QRCode;
    private LinearLayout LinearLayout_birthday;
    private LinearLayout LinearLayout_place;
    private LinearLayout LinearLayout_school;
    private LinearLayout LinearLayout_signature;

    private LinearLayout LinearLayout_editNickName;
    private EditText editText_nickName;
    private LinearLayout LinearLayout_errInfo;
    private TextView textView_errInfo;
    private LinearLayout LinearLayout_editting;

    private User user_update = new User();//上传的用户参数


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_info);

        initStateBar();
        initView();
        updateUI();

    }



    private void updateUI() {
        CircleImageView_head.setImageBitmap(Common.user.getAvatarUrl_bitmap());
        textView_name.setText(Common.user.getNickname());
        textView_gender.setText(Common.user.getGender().equals("0")?"保密":Common.user.getGender().equals("1")?"男":"女");//性别 0:保密 1:男性 2:女性
//        imageView_QRCode.setImageBitmap(Common.user.getAvatarUrl_bitmap());
        textView_birthday.setText(ToolHelper.millisecondToDate(new Long(Common.user.getBirthday())));
        textView_place.setText(ProvinceAndCodeUtil.getCityByCode(Common.user.getProvince().substring(0,2)) +" "+ CityAndCodeUtil.getCityByCode(Common.user.getCity().substring(0,4)));
        Log.e(TAG, "updateUI: "+Common.user.getProvince()+"，"+Common.user.getCity());
        if (Common.user.getSignature() != null) textView_signature.setText(Common.user.getSignature());
//        textView_school.setText(Common.user.getNickname());
    }

    private void initView() {
        user_update.setNickname(Common.user.getNickname());
        user_update.setGender(Common.user.getGender());
        user_update.setSignature(Common.user.getSignature());
        user_update.setCity(Common.user.getCity());
        user_update.setProvince(Common.user.getProvince());
        user_update.setBirthday(Common.user.getBirthday());

        textView_title = findViewById(R.id.textView_title);
        textView_save = findViewById(R.id.textView_save);
        textView_save.setVisibility(View.GONE);
        textView_save.setOnClickListener(this);
        LinearLayout_main = findViewById(R.id.LinearLayout_main);//控制页面显示

        LinearLayout_head = findViewById(R.id.LinearLayout_head);
        LinearLayout_head.setOnClickListener(this);
        LinearLayout_nickName = findViewById(R.id.LinearLayout_nickName);
        LinearLayout_nickName.setOnClickListener(this);
        LinearLayout_gender = findViewById(R.id.LinearLayout_gender);
        LinearLayout_gender.setOnClickListener(this);
        LinearLayout_QRCode = findViewById(R.id.LinearLayout_QRCode);
        LinearLayout_QRCode.setOnClickListener(this);
        LinearLayout_birthday = findViewById(R.id.LinearLayout_birthday);
        LinearLayout_birthday.setOnClickListener(this);
        LinearLayout_place = findViewById(R.id.LinearLayout_place);
        LinearLayout_place.setOnClickListener(this);
        LinearLayout_school = findViewById(R.id.LinearLayout_school);
        LinearLayout_school.setOnClickListener(this);
        LinearLayout_signature = findViewById(R.id.LinearLayout_signature);
        LinearLayout_signature.setOnClickListener(this);
        LinearLayout_editting = findViewById(R.id.LinearLayout_editting);
        LinearLayout_editting.setVisibility(View.GONE);

        LinearLayout_editNickName = findViewById(R.id.LinearLayout_editNickName);
        LinearLayout_editNickName.setVisibility(View.GONE);
        editText_nickName = findViewById(R.id.editText_nickName);
        editText_nickName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (editText_nickName.getText().length() == 0){
                    textView_save.setEnabled(false);
                    textView_save.setTextColor(Color.parseColor("#d0d0d0"));
                    //显示文本为空提示
                    LinearLayout_errInfo.setVisibility(View.VISIBLE);
                    textView_errInfo.setText("昵称不能少于两个汉字或四个英文字符");
                }else {
                    textView_save.setEnabled(true);
                    LinearLayout_errInfo.setVisibility(View.GONE);
                    textView_save.setTextColor(Color.parseColor("#000000"));
                }
            }
        });
        LinearLayout_errInfo = findViewById(R.id.LinearLayout_errInfo);
        LinearLayout_errInfo.setVisibility(View.GONE);
        textView_errInfo = findViewById(R.id.textView_errInfo);

        CircleImageView_head = findViewById(R.id.CircleImageView_head);
        textView_name = findViewById(R.id.textView_name);
        textView_gender = findViewById(R.id.textView_gender);
        imageView_QRCode = findViewById(R.id.imageView_QRCode);
        textView_birthday = findViewById(R.id.textView_birthday);
        textView_place = findViewById(R.id.textView_place);
        textView_school = findViewById(R.id.textView_school);
        textView_signature = findViewById(R.id.textView_signature);
        imageView_back = findViewById(R.id.imageView_back);
        imageView_back.setOnClickListener(this);
    }

    /**
     * 初始化状态栏
     */
    private void initStateBar() {
        ImmersionBar.with(this).init();
        getSupportActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//黑色状态栏字体
    }



    /**
     * 更新用户信息
     */
    private void updateMyInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    final Request request = new Request.Builder()
                            .url("http://www.willdonner.top:3000/user/update?gender="+  user_update.getGender() +"&signature="+ user_update.getSignature() +"" +
                                    "&city="+ user_update.getCity() +"&nickname="+ user_update.getNickname() +"&birthday="+ user_update.getBirthday() +"&province="+ user_update.getProvince() +"")
                            .build();
                    Call call = Common.mOkHttpClient.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String responseData = response.body().string();//处理返回的数据
                            Log.e(TAG, "onResponse: "+responseData);
                            //处理JSON
                            try {
                                JSONObject newResponse = new JSONObject(responseData);
                                String code = newResponse.getString("code");
                                //隐藏提示信息
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        LinearLayout_editting.setVisibility(View.GONE);
                                    }
                                });
                                if (code.equals("200")){
                                    Log.e(TAG, "更新用户信息成功");
                                    if (editText_nickName.getVisibility() == View.VISIBLE){
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                showToast(EditMyInfoActivity.this,"昵称修改成功");
                                                //关闭输入法
                                                hideInput();
                                                LinearLayout_main.setVisibility(View.VISIBLE);
                                                LinearLayout_editNickName.setVisibility(View.GONE);
                                                textView_save.setVisibility(View.GONE);
                                                textView_title.setText("我的资料");
                                            }
                                        });
                                    }
                                }else {
                                    Log.e(TAG, "更新用户信息失败");
                                    //显示错误信息
                                    if (editText_nickName.getVisibility() == View.VISIBLE){
                                        final String message = newResponse.getString("message");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                textView_errInfo.setText(message);
                                                LinearLayout_errInfo.setVisibility(View.VISIBLE);
                                                showToast(EditMyInfoActivity.this,message);
                                            }
                                        });
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
     * 显示键盘
     * @param et 输入焦点
     */
    public void showInput(final EditText et) {
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 隐藏键盘
     */
    public void hideInput() {
        InputMethodManager m=(InputMethodManager) getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
        m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 性别dialog
     */
    private void initDialog() {
        final GenderDialog dialog = new GenderDialog(this);
        dialog.setOnClickBottomListener(new GenderDialog.OnClickBottomListener() {
            @Override
            public void onFemaleClick() {
                //男
                showToast(EditMyInfoActivity.this,"男");
            }
            @Override
            public void onMaleClick() {
                //女
                showToast(EditMyInfoActivity.this,"女");
            }
        }).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView_back:
                if (textView_save.getVisibility() == View.VISIBLE) {
                    //隐藏修改页，显示信息页
                    hideInput();
                    LinearLayout_editNickName.setVisibility(View.GONE);
                    textView_save.setVisibility(View.GONE);
                    textView_title.setText("我的资料");
                    LinearLayout_main.setVisibility(View.VISIBLE);

                }else {
                    finish();
                }
                break;
            case R.id.textView_save:
                //保存
                if (editText_nickName.getVisibility() == View.VISIBLE) {
                    user_update.setNickname(editText_nickName.getText().toString());
                    LinearLayout_editting.setVisibility(View.VISIBLE);
                }
                updateMyInfo();
                break;
            case R.id.LinearLayout_head:
                //头像
                showToast(this,"头像");
                break;
            case R.id.LinearLayout_nickName:
                //昵称
                textView_title.setText("修改昵称");
                textView_save.setVisibility(View.VISIBLE);
                LinearLayout_main.setVisibility(View.GONE);
                LinearLayout_editNickName.setVisibility(View.VISIBLE);
                editText_nickName.setText(Common.user.getNickname());
                showInput(editText_nickName);
                break;
            case R.id.LinearLayout_gender:
                //性别，dialog
                initDialog();
                break;
            case R.id.LinearLayout_QRCode:
                //二维码
                showToast(this,"二维码");
                break;
            case R.id.LinearLayout_birthday:
                //生日

                break;
            case R.id.LinearLayout_place:
                //地区

                break;
            case R.id.LinearLayout_school:
                //大学
                showToast(this,"大学");
                break;
            case R.id.LinearLayout_signature:
                //个性签名

                break;

        }
    }

}
