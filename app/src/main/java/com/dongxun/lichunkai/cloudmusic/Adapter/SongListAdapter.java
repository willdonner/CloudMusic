package com.dongxun.lichunkai.cloudmusic.Adapter;

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

import java.util.List;

/**
 * 搜索适配器
 */
public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder> {

    private List<Song> mList;
    private OnItemClickListener listener;

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView_name;
        TextView textView_artist;
        ImageView imageView_delete;
        LinearLayout LinearLayout_play;


        public ViewHolder( View itemView) {
            super(itemView);
            textView_name = (TextView)itemView.findViewById(R.id.textView_name);
            textView_artist = (TextView)itemView.findViewById(R.id.textView_artist);
            imageView_delete = (ImageView) itemView.findViewById(R.id.imageView_delete);
            LinearLayout_play = (LinearLayout) itemView.findViewById(R.id.LinearLayout_play);
        }
    }

    public SongListAdapter(List<Song> songList) {
        mList = songList;
    }

    //第一步 定义接口（点击播放/菜单）
    public interface OnItemClickListener {
        void onClickPlay(int position);
        void onClickDelete(int position);
    }

    //第二步， 写一个公共的方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_songlist,parent,false);
        ViewHolder holder =  new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        String name = mList.get(position).getName();
        holder.textView_name.setText(name);
        String artist = mList.get(position).getArtist();
        holder.textView_artist.setText(artist);

        //注册点击布局事件
        holder.LinearLayout_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickPlay(position);
                }
            }
        });
        //注册点击布局事件
        holder.imageView_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickDelete(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
