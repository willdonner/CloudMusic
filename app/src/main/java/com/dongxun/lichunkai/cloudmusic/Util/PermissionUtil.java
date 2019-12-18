package com.dongxun.lichunkai.cloudmusic.Util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * 权限处理
 */
public class PermissionUtil {

    private static PermissionUtil mInstance;

    public static final int REQUEST_PERMISSION_CAMERA = 501;//相机权限申请
    public static final int REQUEST_PERMISSION_SD = 502;//SD卡权限申请
    public static final int REQUEST_PERMISSION_AUDIO = 503;//录音权限申请
    public static final int REQUEST_PERMISSION_LOCATION = 504;//定位权限申请
    public static final int REQUEST_PERMISSION_FILESYSTEMS = 505;//挂载SD卡权限申请
    public static final int REQUEST_PERMISSION_CONTACTS = 506;//获取读取手机通讯录
    public static final int REQUEST_PERMISSION_CALL = 507;//获取拨打电话权限

    private PermissionUtil() {
    }

    public static PermissionUtil getInstance() {
        if (null == mInstance) {
            synchronized (PermissionUtil.class) {
                if (null == mInstance) {
                    mInstance = new PermissionUtil();
                }
            }
        }
        return mInstance;
    }


    /**
     * 判断是否拥有某种权限,第一次没有则弹窗询问,用户拒绝后，
     * 请到对应的Activity中重写onRequestPermissionsResult进行判断requestCode值进行相应提示
     *
     * @param activity
     * @param permission
     * @param requestCode
     * @return
     */
    public boolean requestPermission(Activity activity, String[] permission, int requestCode) {
        boolean isHasPermission = false;//默认没有权限
        if (ContextCompat.checkSelfPermission(activity, permission[0]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, permission, requestCode);
        } else {
            isHasPermission = true;
        }
        return isHasPermission;
    }



    /**
     * 相机权限申请
     *
     * @param activity
     * @return
     */
    public boolean requestCamera(Activity activity) {
        return requestPermission(activity,
                new String[]{Manifest.permission.CAMERA
                }, REQUEST_PERMISSION_CAMERA);
    }

    /**
     * SD卡权限申请
     *
     * @param activity
     * @return
     */
    public boolean requestSD(Activity activity) {
        return requestPermission(activity,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, REQUEST_PERMISSION_SD);
    }

    /**
     * 录音权限申请
     *
     * @param activity
     * @return
     */
    public boolean requestAudio(Activity activity) {
        return requestPermission(activity,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO
                }, REQUEST_PERMISSION_AUDIO);
    }

    /**
     * 定位权限申请
     *
     * @param activity
     * @return
     */
    public boolean requestLocation(Activity activity) {
        return requestPermission(activity,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, REQUEST_PERMISSION_LOCATION);
    }

    /**
     * 联系人权限申请
     *
     * @param activity
     * @return
     */
    public boolean requestContacts(Activity activity) {
        return requestPermission(activity,
                new String[]{Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS
                }, REQUEST_PERMISSION_CONTACTS);
    }

    /**
     * 挂载SD卡权限申请
     *
     * @param activity
     * @return
     */
    public boolean requestFilesystems(Activity activity) {
        return requestPermission(activity,
                new String[]{Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
                }, REQUEST_PERMISSION_FILESYSTEMS);
    }

    /**
     * 获取拨打电话权限
     */
    public boolean requestCallPhone(Activity activity) {
        return requestPermission(activity, new String[]{Manifest.permission.CALL_PHONE},
                REQUEST_PERMISSION_CALL);
    }

    /**
     * 申请提供的所有权限
     *
     * @param activity
     */
    public void requestAll(Activity activity) {
        requestPermission(activity,
                new String[]{Manifest.permission.CAMERA
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                        Manifest.permission.CALL_PHONE},
                REQUEST_PERMISSION_CAMERA);
    }
}
