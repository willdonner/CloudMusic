package com.dongxun.lichunkai.cloudmusic.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dongxun.lichunkai.cloudmusic.Bean.Song;
import com.dongxun.lichunkai.cloudmusic.R;
import com.dongxun.lichunkai.cloudmusic.Util.ToolHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 每日推荐RecyclerView适配器
 */
public class DailyRecommendAdapter extends RecyclerView.Adapter<DailyRecommendAdapter.ViewHolder> {

    private List<Song> mList;
    private OnItemClickListener listener;

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView_cover;//封面
        TextView textView_songName;//歌曲名
        TextView textView_fullName;//艺术家+歌曲名
        LinearLayout LinearLayout_song;//点击播放歌曲布局
        LinearLayout LinearLayout_menu;//点击显示菜单布局


        public ViewHolder( View itemView) {
            super(itemView);
            imageView_cover = (ImageView)itemView.findViewById(R.id.imageView_cover);
            textView_songName = (TextView)itemView.findViewById(R.id.textView_songName);
            textView_fullName = (TextView)itemView.findViewById(R.id.textView_fullName);
            LinearLayout_song = (LinearLayout)itemView.findViewById(R.id.LinearLayout_song);
            LinearLayout_menu = (LinearLayout)itemView.findViewById(R.id.LinearLayout_menu);
        }
    }

    public DailyRecommendAdapter(ArrayList<Song> searchHistoryList) {
        mList = searchHistoryList;
    }

    //第一步 定义接口（点击播放/菜单）
    public interface OnItemClickListener {
        void onClickPlay(int position);
        void onClickMenu(int position);
    }

    //第二步， 写一个公共的方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dailyrecommend,parent,false);
        ViewHolder holder =  new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.imageView_cover.setImageResource(R.drawable.img);
        String name = mList.get(position).getName();
        holder.textView_songName.setText(name);
        String artist = mList.get(position).getArtist();
        holder.textView_fullName.setText(artist+" - "+name);

        //注册点击布局事件
        holder.LinearLayout_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickPlay(position);
                }
            }
        });
        holder.imageView_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickPlay(position);
                }
            }
        });
        //注册点击布局事件
        holder.LinearLayout_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickMenu(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
