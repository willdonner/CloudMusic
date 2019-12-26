package com.dongxun.lichunkai.cloudmusic.PopWindow;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dongxun.lichunkai.cloudmusic.Activity.PlayActivity;
import com.dongxun.lichunkai.cloudmusic.Activity.SearchActivity;
import com.dongxun.lichunkai.cloudmusic.Adapter.SearchAdapter;
import com.dongxun.lichunkai.cloudmusic.Adapter.SongListAdapter;
import com.dongxun.lichunkai.cloudmusic.Bean.Song;
import com.dongxun.lichunkai.cloudmusic.Common.Common;
import com.dongxun.lichunkai.cloudmusic.LocalBroadcast.SendLocalBroadcast;
import com.dongxun.lichunkai.cloudmusic.R;
import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import java.util.List;

public class ListWindow extends PopupWindow implements View.OnClickListener {

    private String TAG = "ShareMenu";
    private Context mContext;
    private View mMenuView;

    private RecyclerView recyclerView_song;

    private SongListAdapter songListAdapter;
    private LinearLayoutManager layoutManager;
    private ImageView imageView_deleteAll;
    private TextView textView_like;
    private TextView textView_loop;
    private ImageView imageView_loop;

    private List<Song> songList = new ArrayList<>();


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
        setAdapter();
    }

    private void setAdapter() {
        layoutManager = new LinearLayoutManager(mContext);
        recyclerView_song.setLayoutManager(layoutManager);
        songListAdapter = new SongListAdapter(songList);
        recyclerView_song.setAdapter(songListAdapter);
        songListAdapter.setOnItemClickListener(new SongListAdapter.OnItemClickListener() {
            @Override
            public void onClickPlay(int position) {
                //播放
                Song song = songList.get(position);
                Toast.makeText(mContext,"播放"+song.getName(),Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onClickDelete(int position) {
                //删除
                Song song = songList.get(position);
                Toast.makeText(mContext,"删除"+song.getName(),Toast.LENGTH_SHORT).show();
                songList.remove(position);
                //刷新
                songListAdapter.notifyDataSetChanged();
            }
        });
    }


    /**
     * 更新UI
     */
    private void refreshUI() {
        //循环方式
        textView_loop.setText(Common.loopType[Common.loopType_playing]);
        switch (Common.loopType_playing){
            case 0: imageView_loop.setImageResource(R.drawable.logo_white_loop_random);break;
            case 1: imageView_loop.setImageResource(R.drawable.logo_white_loop_single);break;
            case 2: imageView_loop.setImageResource(R.drawable.logo_white_loop_list);break;
        }
    }

    /**
     * 初始化组件
     */
    private void initView() {
        recyclerView_song = mMenuView.findViewById(R.id.recyclerView_song);
        imageView_deleteAll = mMenuView.findViewById(R.id.imageView_deleteAll);
        imageView_deleteAll.setOnClickListener(this);
        textView_loop = mMenuView.findViewById(R.id.textView_loop);
        textView_loop.setOnClickListener(this);
        textView_like = mMenuView.findViewById(R.id.textView_like);
        textView_like.setOnClickListener(this);
        imageView_loop = mMenuView.findViewById(R.id.imageView_loop);
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
            case R.id.textView_loop:
                //循环方式
                Toast.makeText(mContext,"循环方式",Toast.LENGTH_SHORT).show();
                //当前循环
                Log.e(TAG, "onClick: "+Common.loopType[Common.loopType_playing]);
                //更改循环
                Common.loopType_playing = Common.loopType_playing + 1 > 2?0:Common.loopType_playing+1;
                Log.e(TAG, "onClick: "+Common.loopType[Common.loopType_playing]);
                //更新UI
                textView_loop.setText(Common.loopType[Common.loopType_playing]);
                switch (Common.loopType_playing){
                    case 0: imageView_loop.setImageResource(R.drawable.logo_white_loop_random);break;
                    case 1: imageView_loop.setImageResource(R.drawable.logo_white_loop_single);break;
                    case 2: imageView_loop.setImageResource(R.drawable.logo_white_loop);break;
                }
                //发送广播
                SendLocalBroadcast.refreshLoop(mContext);
                break;
            case R.id.textView_like:
                //收藏全部
                Toast.makeText(mContext,"收藏全部",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageView_deleteAll:
                //删除歌单全部歌曲
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("提示");
                builder.setMessage("移除歌单所有歌曲？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        songList.removeAll(songList);
                        songListAdapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("取消",null);
                builder.show();
                break;
        }
    }

}