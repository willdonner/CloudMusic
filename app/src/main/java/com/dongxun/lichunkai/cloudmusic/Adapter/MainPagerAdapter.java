package com.dongxun.lichunkai.cloudmusic.Adapter;

import android.content.Context;
import android.view.View;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

public class MainPagerAdapter extends PagerAdapter {

    private List<View> views;
    private Context context;

    public MainPagerAdapter(List<View> views, Context context){
        this.views = views;
        this.context = context;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        // TODO Auto-generated method stub
        ((ViewPager) container).removeView(views.get(position));
    }

    @Override
    public Object instantiateItem(View container, int position) {
        // TODO Auto-generated method stub
        ((ViewPager) container).addView(views.get(position));
        return views.get(position);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // TODO Auto-generated method stub
        return arg0 == arg1;
    }

}
