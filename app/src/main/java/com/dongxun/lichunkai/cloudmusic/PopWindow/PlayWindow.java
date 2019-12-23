package com.dongxun.lichunkai.cloudmusic.PopWindow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dongxun.lichunkai.cloudmusic.Common.Common;
import com.dongxun.lichunkai.cloudmusic.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PlayWindow extends PopupWindow implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private String TAG = "ShareMenu";
    private Context mContext;
    private View mMenuView;

    private TextView textView_name;
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

    public PlayWindow(final Context context) {
        super(context);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popwindow_play, null);
        setContentView(mMenuView);

        //设置弹窗宽度
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置弹窗高度
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        // 取得焦点
        this.setFocusable(true);
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

    /**
     * 更新UI
     */
    private void refreshUI() {
        textView_name.setText(Common.song_playing.getName());
        textView_author.setText(Common.song_playing.getArtist());
        if (Common.song_playing.getCover() != null){
            imageView_coverImg.setImageBitmap(Common.song_playing.getCover());
        }else {
            getCoverImage(Common.song_playing.getCoverURL());
        }
    }

    /**
     * 加载网络图片，获取网络图片的bitmap
     * @param url：网络图片的地址
     * @return
     */
    public void getCoverImage(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = null;
                    URL myurl = new URL(url);
                    // 获得连接
                    HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
                    conn.setConnectTimeout(6000);//设置超时
                    conn.setDoInput(true);
                    conn.setUseCaches(false);//不缓存
                    conn.connect();
                    InputStream is = conn.getInputStream();//获得图片的数据流
                    bitmap = BitmapFactory.decodeStream(is);
                    //修改公共变量和更新UI
                    Common.song_playing.setCover(bitmap);
                    //切换主线程更新UI
                    imageView_coverImg.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView_coverImg.setImageBitmap(Common.song_playing.getCover());
                        }
                    });
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        textView_name = mMenuView.findViewById(R.id.textView_name);
        textView_author = mMenuView.findViewById(R.id.textView_author);
        imageView_close = mMenuView.findViewById(R.id.imageView_close);
        imageView_close.setOnClickListener(this);
        imageView_coverImg = mMenuView.findViewById(R.id.imageView_coverImg);
        imageView_coverImg.setOnClickListener(this);
        seekBar = mMenuView.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        imageView_lastSong = mMenuView.findViewById(R.id.imageView_lastSong);
        imageView_lastSong.setOnClickListener(this);
        imageView_playOrPause = mMenuView.findViewById(R.id.imageView_playOrPause);
        imageView_playOrPause.setOnClickListener(this);
        imageView_nextSong = mMenuView.findViewById(R.id.imageView_nextSong);
        imageView_nextSong.setOnClickListener(this);
        imageView_like = mMenuView.findViewById(R.id.imageView_like);
        imageView_like.setOnClickListener(this);
        imageView_loop = mMenuView.findViewById(R.id.imageView_loop);
        imageView_loop.setOnClickListener(this);
        imageView_comments = mMenuView.findViewById(R.id.imageView_comments);
        imageView_comments.setOnClickListener(this);
        imageView_list = mMenuView.findViewById(R.id.imageView_list);
        imageView_list.setOnClickListener(this);
    }

    /**
     * 弹出popWindow
     */
    public void show() {
        View rootview = LayoutInflater.from(mContext).inflate(R.layout.activity_main, null);
        showAtLocation(rootview, Gravity.NO_GRAVITY, 0, 0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageView_close:
                dismiss();
                break;
            case R.id.imageView_coverImg:
                Toast.makeText(mContext,"封面",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageView_lastSong:
                Toast.makeText(mContext,"上一曲",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageView_playOrPause:
                Toast.makeText(mContext,"播放/暂停",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageView_nextSong:
                Toast.makeText(mContext,"下一曲",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageView_like:
                Toast.makeText(mContext,"收藏",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageView_loop:
                Toast.makeText(mContext,"循环",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageView_comments:
                Toast.makeText(mContext,"评论",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageView_list:
                Toast.makeText(mContext,"歌单",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Toast.makeText(mContext,"当前进度：" + seekBar.getProgress(),Toast.LENGTH_SHORT).show();
    }
}