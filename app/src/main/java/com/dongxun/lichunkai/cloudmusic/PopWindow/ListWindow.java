package com.dongxun.lichunkai.cloudmusic.PopWindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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


        Song song = new Song();
        song.setName("趁你还年轻");
        song.setArtist("华晨宇");
        for (int i=0;i<10;i++){
            songList.add(song);
        }

        layoutManager = new LinearLayoutManager(mContext);
        recyclerView_song.setLayoutManager(layoutManager);
        songListAdapter = new SongListAdapter(songList);
        recyclerView_song.setAdapter(songListAdapter);
        songListAdapter.setOnItemClickListener(new SongListAdapter.OnItemClickListener() {
            @Override
            public void onClickPlay(int position) {
                //播放
                Toast.makeText(mContext,"播放",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClickDelete(int position) {
                //删除
                Toast.makeText(mContext,"删除",Toast.LENGTH_SHORT).show();
            }
        });
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
        recyclerView_song = mMenuView.findViewById(R.id.recyclerView_song);
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