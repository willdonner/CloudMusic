package com.dongxun.lichunkai.cloudmusic.PopWindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dongxun.lichunkai.cloudmusic.Activity.PlayActivity;
import com.dongxun.lichunkai.cloudmusic.Bean.Song;
import com.dongxun.lichunkai.cloudmusic.Common.Common;
import com.dongxun.lichunkai.cloudmusic.R;
import com.gyf.immersionbar.ImmersionBar;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 歌曲详情popWindow
 */
public class SongDetailsWindow extends PopupWindow implements View.OnClickListener {

    private String TAG = "ShareMenu";
    private Context mContext;
    private View mMenuView;

    private LinearLayout LinearLayout_addToList;
    private LinearLayout LinearLayout_altist;
    private LinearLayout LinearLayout_album;
    private LinearLayout LinearLayout_similar;
    private LinearLayout LinearLayout_video;

    private LocalBroadcastManager mLocalBroadcastManager; //创建本地广播管理器类变量

    public SongDetailsWindow(final Context context) {
        super(context);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popwindow_songdetails, null);
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

    /**
     * 更新UI
     */
    private void refreshUI() {

    }

    /**
     * 初始化组件
     */
    private void initView() {
        LinearLayout_addToList = mMenuView.findViewById(R.id.LinearLayout_addToList);
        LinearLayout_addToList.setOnClickListener(this);
        LinearLayout_altist = mMenuView.findViewById(R.id.LinearLayout_altist);
        LinearLayout_altist.setOnClickListener(this);
        LinearLayout_album = mMenuView.findViewById(R.id.LinearLayout_album);
        LinearLayout_album.setOnClickListener(this);
        LinearLayout_similar = mMenuView.findViewById(R.id.LinearLayout_similar);
        LinearLayout_similar.setOnClickListener(this);
        LinearLayout_video = mMenuView.findViewById(R.id.LinearLayout_video);
        LinearLayout_video.setOnClickListener(this);
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
            case R.id.LinearLayout_addToList:
                //添加到当前歌单
                Common.songList.add(Common.song_playing);
                Toast.makeText(mContext,"添加成功",Toast.LENGTH_SHORT).show();
                for (Song song:Common.songList){
                    Log.e(TAG, "当前歌单歌曲: "+song.getName());
                }
                break;
            case R.id.LinearLayout_altist:
                Toast.makeText(mContext,"歌手",Toast.LENGTH_SHORT).show();
                break;
            case R.id.LinearLayout_album:
                Toast.makeText(mContext,"专辑",Toast.LENGTH_SHORT).show();
                break;
            case R.id.LinearLayout_similar:
                Toast.makeText(mContext,"相似推荐",Toast.LENGTH_SHORT).show();
                break;
            case R.id.LinearLayout_video:
                Toast.makeText(mContext,"查看视频",Toast.LENGTH_SHORT).show();
                break;
        }
    }

}