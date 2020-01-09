package com.dongxun.lichunkai.cloudmusic.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dongxun.lichunkai.cloudmusic.Bean.SongSheet;
import com.dongxun.lichunkai.cloudmusic.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * 歌单RecyclerView适配器
 */
public class PersonalizedAdapter extends RecyclerView.Adapter<PersonalizedAdapter.ViewHolder> {

    private List<SongSheet> mList;
    private OnItemClickListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView_name;//歌单名
        TextView textView_playCount;//播放量
        ImageView imageView_cover;//封面图
        RelativeLayout RelativeLayout_personalized;

        public ViewHolder( View itemView) {
            super(itemView);
            textView_name = (TextView)itemView.findViewById(R.id.textView_name);
            textView_playCount = (TextView)itemView.findViewById(R.id.textView_playCount);
            imageView_cover = (ImageView) itemView.findViewById(R.id.imageView_cover);
            RelativeLayout_personalized = (RelativeLayout)itemView.findViewById(R.id.RelativeLayout_personalized);
        }
    }

    public PersonalizedAdapter(List<SongSheet> searchHistoryList) {
        mList = searchHistoryList;
    }

    //第一步 定义接口（点击城市标签）
    public interface OnItemClickListener {
        void onClick(int position);
    }

    //第二步， 写一个公共的方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_personalized,parent,false);
        ViewHolder holder =  new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        String name = mList.get(position).getName();//歌单名
        Bitmap pic = mList.get(position).getPic();//封面图
        String playCount = mList.get(position).getPlayCount();//播放量
        holder.textView_name.setText(name);
        holder.textView_playCount.setText((playCount.length()>5)?(int)Integer.parseInt(playCount)/100000+"万":playCount);//超过十万改变单位

        //获取封面
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
                    //切换主线程更新UI
                    holder.imageView_cover.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.imageView_cover.setImageBitmap(bitmap);
                        }
                    });
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        holder.RelativeLayout_personalized.setOnClickListener(new View.OnClickListener() {
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