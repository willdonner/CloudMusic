package com.dongxun.lichunkai.cloudmusic.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectChangeListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import static com.dongxun.lichunkai.cloudmusic.Util.ToolHelper.addCityData;
import static com.dongxun.lichunkai.cloudmusic.Util.ToolHelper.addProvinceData;
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
    private EditText editText_signature;
    private LinearLayout LinearLayout_editSignature;
    private TextView textView_sinatureTextCount;

    private User user_update = new User();//上传的用户参数

    //省市选择器
    private ArrayList<String> options_province = new ArrayList<>();//省
    private ArrayList<ArrayList<String>> options_city = new ArrayList<>();//市
    private com.bigkoo.pickerview.view.OptionsPickerView OptionsPickerView;

    //修改哪一项数据的标识符(修改完成改为默认值：false)
    private Boolean mark_nickName = false;//昵称
    private Boolean mark_gender = false;//性别
    private Boolean mark_birthday = false;//生日
    private Boolean mark_place = false;//地区
    private Boolean mark_signature = false;//签名


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_info);

        initStateBar();
        initView();
        updateUI();
    }



    private void updateUI() {
        CircleImageView_head.setImageBitmap(user_update.getAvatarUrl_bitmap());
        textView_name.setText(user_update.getNickname());
        textView_gender.setText(user_update.getGender().equals("0")?"保密":user_update.getGender().equals("1")?"男":"女");//性别 0:保密 1:男性 2:女性
//        imageView_QRCode.setImageBitmap(user_update.getAvatarUrl_bitmap());
        textView_birthday.setText(ToolHelper.millisecondToDate(new Long(user_update.getBirthday())));
        textView_place.setText(ProvinceAndCodeUtil.getCityByCode(user_update.getProvince().substring(0,2)) +" "+ CityAndCodeUtil.getCityByCode(user_update.getCity().substring(0,4)));
        Log.e(TAG, "updateUI: "+user_update.getProvince()+"，"+user_update.getCity());
        if (user_update.getSignature() != null) textView_signature.setText(user_update.getSignature());
//        textView_school.setText(user_update.getNickname());
    }

    private void initView() {
        user_update.setAvatarUrl_bitmap(Common.user.getAvatarUrl_bitmap());
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

        editText_signature = findViewById(R.id.editText_signature);
        LinearLayout_editSignature = findViewById(R.id.LinearLayout_editSignature);
        LinearLayout_editSignature.setVisibility(View.GONE);
        textView_sinatureTextCount = findViewById(R.id.textView_sinatureTextCount);
        editText_signature.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                textView_sinatureTextCount.setText(300 - editText_signature.getText().length()+"");
            }
        });
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
                                    if (LinearLayout_editNickName.getVisibility() == View.VISIBLE){
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
                                                updateUI();
                                            }
                                        });
                                    }
                                    //修改地区
                                    if (mark_place){
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                updateUI();
                                            }
                                        });
                                        Common.user.setProvince(user_update.getProvince());
                                        Common.user.setCity(user_update.getCity());
                                        mark_place = false;
                                    }
                                }else {
                                    Log.e(TAG, "更新用户信息失败");
                                    //显示错误信息
                                    if (LinearLayout_editNickName.getVisibility() == View.VISIBLE){
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
        et.setSelection(et.getText().length());
    }

    /**
     * 隐藏键盘
     */
    public void hideInput() {
        InputMethodManager m=(InputMethodManager) getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
        m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 性别修改dialog(信息修改完成后不做提示)
     */
    private void initGenderDialog() {
        final GenderDialog dialog = new GenderDialog(this,user_update);
        dialog.getUser(user_update).setOnClickBottomListener(new GenderDialog.OnClickBottomListener() {
            @Override
            public void onFemaleClick() {
                //男
                user_update.setGender("1");
                updateUI();
                updateMyInfo();
                dialog.dismiss();
            }
            @Override
            public void onMaleClick() {
                //女
                user_update.setGender("2");
                updateUI();
                updateMyInfo();
                dialog.dismiss();
            }
        }).show();
    }

    /**
     * 省，市选择器
     */
    private void initOptionPicker() {
        //添加数据
        options_province = addProvinceData(options_province);
        options_city = addCityData(options_city);
        //获取默认选中项下标
        int index_province = options_province.indexOf(ProvinceAndCodeUtil.getCityByCode(user_update.getProvince().substring(0,2)));
        Log.e(TAG, "省份代码: "+user_update.getProvince());
        Log.e(TAG, "省份默认选中项: "+index_province);
        int index_city = options_city.get(index_province).indexOf(CityAndCodeUtil.getCityByCode(user_update.getCity().substring(0,4)));
        Log.e(TAG, "省份代码: "+CityAndCodeUtil.getCityByCode(user_update.getCity().substring(0,4)));
        Log.e(TAG, "省份默认选中项: "+index_city);
        //初始化选择器
        OptionsPickerView = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String province = options_province.get(options1);//省名
                String city = options_city.get(options1).get(options2);//市名
                Toast.makeText(EditMyInfoActivity.this, province+"，"+city, Toast.LENGTH_SHORT).show();
                //将名称转化为省市级代码
                String code_province = ProvinceAndCodeUtil.getCodeByCity(province);
                String code_city = CityAndCodeUtil.getCodeByCity(city);
                Log.e(TAG, "省级代码: "+code_province);
                Log.e(TAG, "市级代码: "+code_city);
                showToast(EditMyInfoActivity.this,code_province+"，"+code_city);
                //直辖市及其特别行政区作特殊判断，当前湖北省、海南省、新疆维吾尔族自治区、地区代码不完全，台湾省、海外全空。
                if (code_province != null && code_city != null) {
                    //修改数据
                    user_update.setProvince(code_province+"0000");
                    user_update.setCity(code_city+"00");
                    Log.e(TAG, "省级代码: "+user_update.getProvince());
                    Log.e(TAG, "市级代码: "+user_update.getCity());
                    mark_place = true;
                    updateMyInfo();
                }


            }
        })
                .setTitleText("城市选择")
                .setContentTextSize(20)//设置滚轮文字大小
                .setDividerColor(Color.LTGRAY)//设置分割线的颜色
                .setSelectOptions(index_province, index_city)//默认选中项
                .setBgColor(Color.WHITE)
                .setTitleBgColor(Color.WHITE)
                .setTitleColor(Color.BLACK)
                .setCancelColor(Color.RED)
                .setSubmitColor(Color.RED)
                .setTextColorCenter(Color.RED)
                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setLabels("", "", "区")//不适应label,添加数据的时候已经带有"省","市"
                .setOutSideColor(0x00000000) //设置外部遮罩颜色
                .setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
                    @Override
                    public void onOptionsSelectChanged(int options1, int options2, int options3) {
                        String province = options_province.get(options1);
                        String city = options_city.get(options1).get(options2);
//                        Toast.makeText(EditMyInfoActivity.this, province+"，"+city, Toast.LENGTH_SHORT).show();
                        String code_province = ProvinceAndCodeUtil.getCodeByCity(province);
                        String code_city = CityAndCodeUtil.getCodeByCity(city);
                        Log.e(TAG, "省级代码: "+code_province);
                        Log.e(TAG, "市级代码: "+code_city);
                        showToast(EditMyInfoActivity.this,code_province+"，"+code_city);
                    }
                })
                .build();
        OptionsPickerView.setPicker(options_province, options_city);//二级选择器
        //弹出选择器
        OptionsPickerView.show();
    }

    /**
     * 日期选择对话框
     */
    private void initDataPicker() {
        Date d = new Date(new Long(user_update.getBirthday()));
        SimpleDateFormat sdf_year = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdf_month = new SimpleDateFormat("MM");
        SimpleDateFormat sdf_day = new SimpleDateFormat("dd");
        final int mYear = Integer.parseInt(sdf_year.format(d));
        final int mMonth = Integer.parseInt(sdf_month.format(d));
        final int mDay = Integer.parseInt(sdf_day.format(d));

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                R.style.MyDatePickerDialogTheme,//主题颜色
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //设置生日
                        Log.e(TAG, "onClick: "+year+"，"+month+"，"+dayOfMonth);
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth, 0, 0, 0);
                        user_update.setBirthday(calendar.getTimeInMillis()+"");
                        updateMyInfo();
                        updateUI();
                    }
                },
                mYear, mMonth-1, mDay);
        datePickerDialog.show();
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

                    LinearLayout_editSignature.setVisibility(View.GONE);

                }else {
                    finish();
                }
                break;
            case R.id.textView_save:
                //保存
                if (LinearLayout_editNickName.getVisibility() == View.VISIBLE) {
                    user_update.setNickname(editText_nickName.getText().toString());
                    LinearLayout_editting.setVisibility(View.VISIBLE);
                }
                if (editText_signature.getVisibility() == View.VISIBLE) {
                    user_update.setSignature(editText_signature.getText().toString());
                    hideInput();
                    updateUI();
                    LinearLayout_editSignature.setVisibility(View.GONE);
                    textView_save.setVisibility(View.GONE);
                    textView_title.setText("我的资料");
                    LinearLayout_main.setVisibility(View.VISIBLE);
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
                editText_nickName.setText(user_update.getNickname());
                showInput(editText_nickName);
                break;
            case R.id.LinearLayout_gender:
                //性别，dialog
                initGenderDialog();
                break;
            case R.id.LinearLayout_QRCode:
                //二维码
                showToast(this,"二维码");
                break;
            case R.id.LinearLayout_birthday:
                //生日
                initDataPicker();
                break;
            case R.id.LinearLayout_place:
                //地区(根据工具类里的省份和城市编码表实现)
                initOptionPicker();
                break;
            case R.id.LinearLayout_school:
                //大学
                showToast(this,"大学");
                break;
            case R.id.LinearLayout_signature:
                //个性签名
                //昵称
                textView_title.setText("修改签名");
                textView_save.setVisibility(View.VISIBLE);
                LinearLayout_main.setVisibility(View.GONE);
                LinearLayout_editSignature.setVisibility(View.VISIBLE);
                editText_signature.setText(user_update.getSignature());
                showInput(editText_signature);
                break;

        }
    }

}
