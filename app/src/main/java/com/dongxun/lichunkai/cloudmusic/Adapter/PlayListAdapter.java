package com.dongxun.lichunkai.cloudmusic.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dongxun.lichunkai.cloudmusic.Activity.UserActivity;
import com.dongxun.lichunkai.cloudmusic.Bean.Song;
import com.dongxun.lichunkai.cloudmusic.Bean.SongSheet;
import com.dongxun.lichunkai.cloudmusic.Class.RoundImageView;
import com.dongxun.lichunkai.cloudmusic.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 主页歌单RecyclerView适配器
 */
public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder> {

    private List<SongSheet> mList;
    private OnItemClickListener listener;

    static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout LinearLayout_playList;
        LinearLayout LinearLayout_creator;
        RoundImageView roundImageView_cover;
        TextView textView_name;
        TextView textView_trackCount;
        TextView textView_creatorName;
        TextView textView_playCount;


        public ViewHolder( View itemView) {
            super(itemView);
            roundImageView_cover = (RoundImageView)itemView.findViewById(R.id.roundImageView_cover);
            LinearLayout_playList = (LinearLayout)itemView.findViewById(R.id.LinearLayout_playList);
            LinearLayout_creator = (LinearLayout)itemView.findViewById(R.id.LinearLayout_creator);
            textView_name = (TextView)itemView.findViewById(R.id.textView_name);
            textView_trackCount = (TextView)itemView.findViewById(R.id.textView_trackCount);
            textView_creatorName = (TextView)itemView.findViewById(R.id.textView_creatorName);
            textView_playCount = (TextView)itemView.findViewById(R.id.textView_playCount);
        }
    }

    public PlayListAdapter(ArrayList<SongSheet> searchHistoryList) {
        mList = searchHistoryList;
    }

    //第一步 定义接口（点击播放/菜单）
    public interface OnItemClickListener {
        void onClick(int position);
    }

    //第二步， 写一个公共的方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist,parent,false);
        ViewHolder holder =  new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        //封面
//        if (mList.get(position).getPic() != null) holder.roundImageView_cover.setImageBitmap(mList.get(position).getPic());
        //名字
        holder.textView_name.setText(mList.get(position).getName());
        //歌曲数量
        holder.textView_trackCount.setText(mList.get(position).getTrackCount());
        //创建人
        if (mList.get(position).getCreator() != null) holder.textView_creatorName.setText(mList.get(position).getCreator().getNickname());
        else holder.LinearLayout_creator.setVisibility(View.GONE);
        //播放数
        String playCount = mList.get(position).getPlayCount();
        holder.textView_playCount.setText((playCount.length()>5)?(int)Integer.parseInt(playCount)/10000+"万":playCount);//超过万改变单位
        holder.textView_playCount.setText((playCount.length()>8)?(int)Integer.parseInt(playCount)/100000000+"亿":(playCount.length()>5)?(int)Integer.parseInt(playCount)/10000+"万":playCount);//超过亿改变单位
        //封面
        if (holder.roundImageView_cover.getDrawable() == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    URL imageurl = null;
                    try {
                        imageurl = new URL(mList.get(position).getPicUrl());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    try {
                        HttpURLConnection conn = (HttpURLConnection)imageurl.openConnection();
                        conn.setDoInput(true);
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        final Bitmap bitmap = BitmapFactory.decodeStream(is);
                        holder.roundImageView_cover.post(new Runnable() {
                            @Override
                            public void run() {
                                holder.roundImageView_cover.setImageBitmap(bitmap);
                            }
                        });
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        //注册点击布局事件
        holder.LinearLayout_playList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
