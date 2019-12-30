package com.dongxun.lichunkai.cloudmusic.Class;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动管理
 */
public class ActivityCollector {

    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity){
        activities.add(activity);
    }

    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }

    public static void finishAll(){
        for (Activity activity:activities){
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
    }

    /**
     * 移除除当前Activity之外的Activity(主要用于登陆成功，移除启动页和登录页)
     * @param mActivity
     */
    public static void removeOther(Activity mActivity) {
        for (Activity activity:activities){
            if (!activity.isFinishing() && activity != mActivity){
                activity.finish();
            }
        }
    }
}
