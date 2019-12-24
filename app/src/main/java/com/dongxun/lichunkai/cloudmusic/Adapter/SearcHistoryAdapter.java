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
 * 搜索历史适配器
 */
public class SearcHistoryAdapter extends RecyclerView.Adapter<SearcHistoryAdapter.ViewHolder> {

    private List<String> mList;
    private OnItemClickListener listener;

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;


        public ViewHolder( View itemView) {
            super(itemView);
            textView = (TextView)itemView.findViewById(R.id.textView);
        }
    }

    public SearcHistoryAdapter(List<String> searchHistoryList) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searchistory,parent,false);
        ViewHolder holder =  new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        String city = mList.get(position);
        holder.textView.setText(city);

        holder.textView.setOnClickListener(new View.OnClickListener() {
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