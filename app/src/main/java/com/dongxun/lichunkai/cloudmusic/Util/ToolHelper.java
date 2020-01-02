package com.dongxun.lichunkai.cloudmusic.Util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dongxun.lichunkai.cloudmusic.Activity.LoginActivity;
import com.dongxun.lichunkai.cloudmusic.Bean.Song;
import com.dongxun.lichunkai.cloudmusic.Common.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 小工具帮助类
 */
public class ToolHelper {

    private static String TAG = "ToolHelper";

    /**
     * 读取txt文件内容
     * @param inputStream
     * @return
     */
    public static String getString(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "gbk");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    /**
     * 加载本地图片
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取音频文件的总时长大小(毫秒)
     *
     * @param filePath 音频文件路径
     * @return 返回时长大小
     */
    public static int getAudioFileVoiceTime(String filePath) {
        long mediaPlayerDuration = 0L;
        if (filePath == null || filePath.isEmpty()) {
            return 0;
        }
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayerDuration = mediaPlayer.getDuration();
        } catch (IOException ioException) {
            ioException.getMessage();
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
        }
        return (int) mediaPlayerDuration;
    }

    /**
     * 创建文件
     * @param filePath 文件路径（不包括文件名及格式）
     * @param id 歌曲id
     * @return
     * @throws IOException
     */
    public static boolean creatTxtFile(String filePath,String id) throws IOException {
        boolean flag = false;
        File filename = new File(filePath + "/"+ id +".txt");
        if (!filename.exists()) {
            filename.createNewFile();
            flag = true;
        }
        return flag;
    }

    /**
     * 写文件
     * @param filePath 文件路径（不包括文件名及格式）
     * @param id 歌曲id
     * @param newStr 存储内容
     * @return
     * @throws IOException
     */
    public static boolean writeTxtFile(String filePath,String id,String newStr) throws IOException {
        // 先读取原有文件内容，然后进行写入操作
        boolean flag = false;
        String filein = newStr + "\r\n";
        String temp = "";

        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        FileOutputStream fos = null;
        PrintWriter pw = null;
        try {
            // 文件路径
            File file = new File(filePath + "/"+ id +".txt");
            // 将文件读入输入流
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            StringBuffer buf = new StringBuffer();

            // 保存该文件原有的内容
            for (int j = 1; (temp = br.readLine()) != null; j++) {
                buf = buf.append(temp);
                // System.getProperty("line.separator")
                // 行与行之间的分隔符 相当于“\n”
                buf = buf.append(System.getProperty("line.separator"));
            }
            buf.append(filein);

            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);
            pw.write(buf.toString().toCharArray());
            pw.flush();
            flag = true;
        } catch (IOException e1) {
            // TODO 自动生成 catch 块
            throw e1;
        } finally {
            if (pw != null) {
                pw.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (br != null) {
                br.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
        return flag;
    }


    /**
     * 读取文件内容
     * @param file
     * @return
     * @throws IOException
     */
    public static String readTxtFile(String file) throws IOException {
        String sumstr = "";
        FileInputStream fin = null;
        fin = new FileInputStream(file);
        InputStreamReader reader = new InputStreamReader(fin);
        BufferedReader buffReader = new BufferedReader(reader);
        String strTmp = "";
        while((strTmp = buffReader.readLine())!=null){
            if (sumstr.length() == 0) sumstr = strTmp;
            else sumstr = sumstr +"\n"+ strTmp;
        }
        Log.e(TAG, "refreshUI: "+ sumstr );
        buffReader.close();
        return sumstr;
    }

    /**
     * 下载图片
     * @param downloadUrl   下载的文件地址
     * @return
     */
    public static void downloadPicture(String filePath, String songID, final String downloadUrl) {
        try {
            //设置下载位置和名称
            final String fileName = filePath+ "/" + songID + ".jpg";
            File file1 = new File(fileName);
            if (file1.exists()) {
                //文件存在
                Log.e("DOWLOAD", "jpg文件已存在！");
            }else {
                URL url = new URL(downloadUrl);
                //打开连接
                URLConnection conn = url.openConnection();
                //打开输入流
                InputStream is = conn.getInputStream();
                //获得长度
                int contentLength = conn.getContentLength();
                Log.e("DOWLOAD", "jpg文件长度 = " + contentLength);
                //创建字节流
                byte[] bs = new byte[1024];
                int len;
                OutputStream os = new FileOutputStream(fileName);
                //写数据
                while ((len = is.read(bs)) != -1) {
                    os.write(bs, 0, len);
                }
                //完成后关闭流
                Log.e("DOWLOAD", "jpg文件不存在,下载成功！");
                os.close();
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存bitmap到本地
     *
     * @param bitmap Bitmap
     */
    public static void saveBitmap(Bitmap bitmap,String path) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            savePath = path;
        } else {
            Log.e("tag", "saveBitmap failure : sdcard not mounted");
            return;
        }
        try {
            filePic = new File(savePath);
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.e("tag", "saveBitmap: " + e.getMessage());
            return;
        }
        Log.i("tag", "saveBitmap success: " + filePic.getAbsolutePath());
    }

    /**
     * 将本地图片转成Bitmap
     * @param path 已有图片的路径
     * @return
     */
    public static Bitmap openImage(String path){
        Bitmap bitmap = null;
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
            bitmap = BitmapFactory.decodeStream(bis);
            bis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 获取当前歌曲位置
     * @return
     */
    public static int getSongListPosition() {
        int nowPosition = 0;
        for (Song song:Common.songList){
            if (song.getId().equals(Common.song_playing.getId())) nowPosition = Common.songList.indexOf(song);
        }
        return nowPosition;
    }

    /**
     * 提示信息
     * @param context
     * @param msg
     */
    public static void showToast(Context context,String msg){
        Toast toast=Toast.makeText(context,msg,Toast.LENGTH_LONG);
        toast.setText(msg);
        toast.show();
    }

    /**
     * 保存登录账号
     * @param account
     */
    public static void saveAccount(Context context,String account) {
        SharedPreferences.Editor editor = context.getSharedPreferences("data",Context.MODE_PRIVATE).edit();
        editor.putString("account",account);
        editor.apply();
    }

    /**
     * 读取登录账号
     * @param context
     * @return
     */
    public static String getAccount(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("data",Context.MODE_PRIVATE);
        String account = preferences.getString("account","");
        return account;
    }
}
