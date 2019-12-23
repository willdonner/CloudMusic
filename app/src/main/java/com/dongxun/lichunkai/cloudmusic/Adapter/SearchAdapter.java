package com.dongxun.lichunkai.cloudmusic.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dongxun.lichunkai.cloudmusic.Bean.Song;
import com.dongxun.lichunkai.cloudmusic.R;

import java.util.List;

/**
 * 搜索适配器
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private List<Song> mList;
    private OnItemClickListener listener;

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView_songName;//歌曲名
        TextView textView_fullName;//艺术家+歌曲名
        LinearLayout LinearLayout_song;//点击播放歌曲布局
        LinearLayout LinearLayout_menu;//点击显示菜单布局


        public ViewHolder( View itemView) {
            super(itemView);
            textView_songName = (TextView)itemView.findViewById(R.id.textView_songName);
            textView_fullName = (TextView)itemView.findViewById(R.id.textView_fullName);
            LinearLayout_song = (LinearLayout)itemView.findViewById(R.id.LinearLayout_song);
            LinearLayout_menu = (LinearLayout)itemView.findViewById(R.id.LinearLayout_menu);
        }
    }

    public SearchAdapter(List<Song> searchHistoryList) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search,parent,false);
        ViewHolder holder =  new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
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
