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
import com.dongxun.lichunkai.cloudmusic.Adapter.PersonalizedAdapter;
import com.dongxun.lichunkai.cloudmusic.Bean.Song;
import com.dongxun.lichunkai.cloudmusic.Bean.Time;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

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
    private static Bitmap bitmap;

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
        Toast toast=Toast.makeText(context,msg,Toast.LENGTH_SHORT);
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

    /**
     * 毫秒转日期
     * @param time
     * @return
     */
    public static String millisecondToDate(Long time) {
        Date d = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        return sdf.format(d);
    }

    /**
     * 获取指定日期到今天的时间间隔(年-月-天)
     * @param millionSeconds 毫秒数
     * @return Time类
     */
    public static Time betweenDate(String millionSeconds){
        Date d = new Date(new Long(millionSeconds));
        int createTime_year = Integer.parseInt(new SimpleDateFormat("yyyy").format(d));//年
        int createTime_month = Integer.parseInt(new SimpleDateFormat("MM").format(d));//月
        int createTime_day = Integer.parseInt(new SimpleDateFormat("dd").format(d));//日
        LocalDate localDate = LocalDate.now();  //当前时间
        LocalDate createDate = LocalDate.of(createTime_year,createTime_month,createTime_day);//要计算的时间
        Period betweenDate = Period.between(createDate, localDate); //计算时间间隔
        Time time = new Time();
        time.setYear(betweenDate.getYears());
        time.setMonth(betweenDate.getMonths());
        time.setDay(betweenDate.getDays());
        return time;
    }
    /**
     * 添加省份
     */
    public static ArrayList<String> addProvinceData(ArrayList<String> options_province) {
        options_province.add("直辖市");//item1
        options_province.add("特别行政区");//item2
        options_province.add("河北省");//item3
        options_province.add("山西省");//item4
        options_province.add("内蒙古自治区");//item5
        options_province.add("辽宁省");//item6
        options_province.add("吉林省");//item7
        options_province.add("黑龙江省");//item8
        options_province.add("江苏省");//item9
        options_province.add("浙江省");//item10
        options_province.add("安徽省");//item11
        options_province.add("福建省");//item12
        options_province.add("江西省");//item13
        options_province.add("山东省");//item14
        options_province.add("河南省");//item15
        options_province.add("湖北省");//item16
        options_province.add("湖南省");//item17
        options_province.add("广东省");//item18
        options_province.add("广西壮族自治区");//item19
        options_province.add("海南省");//item20
        options_province.add("四川省");//item21
        options_province.add("贵州省");//item22
        options_province.add("云南省");//item23
        options_province.add("西藏自治区");//item24
        options_province.add("陕西省");//item25
        options_province.add("甘肃省");//item26
        options_province.add("青海省");//item27
        options_province.add("宁夏回族自治区");//item28
        options_province.add("新疆维吾尔自治区");//item29
        options_province.add("台湾省");//item30
        options_province.add("海外");//item31
        return options_province;
    }

    /**
     * 添加城市
     */
    public static ArrayList<ArrayList<String>> addCityData(ArrayList<ArrayList<String>> options_city) {
        ArrayList<String> item1 = new ArrayList<>();
        item1.add("北京市");
        item1.add("天津市");
        item1.add("上海市");
        item1.add("重庆市");
        options_city.add(item1);
        ArrayList<String> item2 = new ArrayList<>();
        item2.add("香港");
        item2.add("澳门");
        options_city.add(item2);
        ArrayList<String> item3 = new ArrayList<>();
        item3.add("石家庄市");
        item3.add("唐山市");
        item3.add("秦皇岛市");
        item3.add("邯郸市");
        item3.add("邢台市");
        item3.add("保定市");
        item3.add("张家口市");
        item3.add("承德市");
        item3.add("沧州市");
        item3.add("廊坊市");
        item3.add("衡水市");
        options_city.add(item3);
        ArrayList<String> item4 = new ArrayList<>();
        item4.add("太原市");
        item4.add("大同市");
        item4.add("阳泉市");
        item4.add("长治市");
        item4.add("晋城市");
        item4.add("宿州市");
        item4.add("晋中市");
        item4.add("运城市");
        item4.add("忻州市");
        item4.add("临汾市");
        item4.add("吕梁市");
        options_city.add(item4);
        ArrayList<String> item5 = new ArrayList<>();
        item5.add("呼和浩特市");
        item5.add("包头市");
        item5.add("乌海市");
        item5.add("赤峰市");
        item5.add("通辽市");
        item5.add("鄂尔多斯市");
        item5.add("呼伦贝尔市");
        item5.add("巴彦淖尔市");
        item5.add("乌兰察布市");
        item5.add("兴安盟");
        item5.add("锡林郭勒盟");
        item5.add("阿拉善盟");
        options_city.add(item5);
        ArrayList<String> item6 = new ArrayList<>();
        item6.add("沈阳市");
        item6.add("大连市");
        item6.add("鞍山市");
        item6.add("抚顺市");
        item6.add("本溪市");
        item6.add("丹东市");
        item6.add("锦州市");
        item6.add("营口市");
        item6.add("阜新市");
        item6.add("辽阳市");
        item6.add("盘锦市");
        item6.add("铁岭市");
        item6.add("朝阳市");
        item6.add("葫芦岛市");
        options_city.add(item6);
        ArrayList<String> item7 = new ArrayList<>();
        item7.add("长春市");
        item7.add("吉林市");
        item7.add("四平市");
        item7.add("辽源市");
        item7.add("通化市");
        item7.add("白山市");
        item7.add("松原市");
        item7.add("白城市");
        item7.add("延边朝鲜族自治州");
        options_city.add(item7);
        ArrayList<String> item8 = new ArrayList<>();
        item8.add("哈尔滨市");
        item8.add("齐齐哈尔市");
        item8.add("鸡西市");
        item8.add("鹤岗市");
        item8.add("双鸭山市");
        item8.add("大庆市");
        item8.add("伊春市");
        item8.add("佳木斯市");
        item8.add("七台河市");
        item8.add("牡丹江市");
        item8.add("黑河市");
        item8.add("绥化市");
        item8.add("大兴安岭地区");
        options_city.add(item8);
        ArrayList<String> item9 = new ArrayList<>();
        item9.add("南京市");
        item9.add("无锡市");
        item9.add("徐州市");
        item9.add("常州市");
        item9.add("苏州市");
        item9.add("南通市");
        item9.add("连云港市");
        item9.add("淮安市");
        item9.add("盐城市");
        item9.add("扬州市");
        item9.add("镇江市");
        item9.add("泰州市");
        item9.add("宿迁市");
        options_city.add(item9);
        ArrayList<String> item10 = new ArrayList<>();
        item10.add("杭州市");
        item10.add("宁波市");
        item10.add("温州市");
        item10.add("嘉兴市");
        item10.add("湖州市");
        item10.add("绍兴市");
        item10.add("金华市");
        item10.add("衢州市");
        item10.add("舟山市");
        item10.add("台州市");
        item10.add("丽水市");
        options_city.add(item10);
        ArrayList<String> item11 = new ArrayList<>();
        item11.add("合肥市");
        item11.add("芜湖市");
        item11.add("蚌埠市");
        item11.add("淮南市");
        item11.add("马鞍山市");
        item11.add("淮北市");
        item11.add("铜陵市");
        item11.add("安庆市");
        item11.add("黄山市");
        item11.add("滁州市");
        item11.add("阜阳市");
        item11.add("宿州市");
        item11.add("巢湖市");
        item11.add("六安市");
        item11.add("亳州市");
        item11.add("池州市");
        item11.add("宣城市");
        options_city.add(item11);
        ArrayList<String> item12 = new ArrayList<>();
        item12.add("福州市");
        item12.add("厦门市");
        item12.add("莆田市");
        item12.add("三明市");
        item12.add("泉州市");
        item12.add("漳州市");
        item12.add("南平市");
        item12.add("龙岩市");
        item12.add("宁德市");
        options_city.add(item12);
        ArrayList<String> item13 = new ArrayList<>();
        item13.add("南昌市");
        item13.add("景德镇市");
        item13.add("萍乡市");
        item13.add("九江市");
        item13.add("新余市");
        item13.add("鹰潭市");
        item13.add("赣州市");
        item13.add("吉安市");
        item13.add("宜春市");
        item13.add("抚州市");
        item13.add("上饶市");
        options_city.add(item13);
        ArrayList<String> item14 = new ArrayList<>();
        item14.add("南昌市");
        item14.add("济南市");
        item14.add("青岛市");
        item14.add("淄博市");
        item14.add("枣庄市");
        item14.add("东营市");
        item14.add("烟台市");
        item14.add("潍坊市");
        item14.add("济宁市");
        item14.add("泰安市");
        item14.add("威海市");
        item14.add("日照市");
        item14.add("莱芜市");
        item14.add("临沂市");
        item14.add("德州市");
        item14.add("聊城市");
        item14.add("滨州市");
        item14.add("菏泽市");
        options_city.add(item14);
        ArrayList<String> item15 = new ArrayList<>();
        item15.add("郑州市");
        item15.add("开封市");
        item15.add("洛阳市");
        item15.add("平顶山市");
        item15.add("安阳市");
        item15.add("鹤壁市");
        item15.add("新乡市");
        item15.add("焦作市");
        item15.add("濮阳市");
        item15.add("许昌市");
        item15.add("漯河市");
        item15.add("三门峡市");
        item15.add("南阳市");
        item15.add("商丘市");
        item15.add("信阳市");
        item15.add("周口市");
        item15.add("驻马店市");
        item15.add("济源市");
        options_city.add(item15);
        ArrayList<String> item16 = new ArrayList<>();
        item16.add("武汉市");
        item16.add("黄石市");
        item16.add("十堰市");
        item16.add("宜昌市");
        item16.add("襄阳市");
        item16.add("鄂州市");
        item16.add("荆门市");
        item16.add("孝感市");
        item16.add("荆州市");
        item16.add("黄冈市");
        item16.add("咸宁市");
        item16.add("随州市");
        item16.add("恩施土家族苗族自治州");
        item16.add("仙桃市");
        item16.add("潜江市");
        item16.add("天门市");
        item16.add("神农架林区");
        options_city.add(item16);
        ArrayList<String> item17 = new ArrayList<>();
        item17.add("长沙市");
        item17.add("株洲市");
        item17.add("湘潭市");
        item17.add("衡阳市");
        item17.add("邵阳市");
        item17.add("岳阳市");
        item17.add("常德市");
        item17.add("张家界市");
        item17.add("益阳市");
        item17.add("郴州市");
        item17.add("永州市");
        item17.add("怀化市");
        item17.add("娄底市");
        item17.add("湘西土家族苗族自治州");
        options_city.add(item17);
        ArrayList<String> item18 = new ArrayList<>();
        item18.add("广州市");
        item18.add("韶关市");
        item18.add("深圳市");
        item18.add("珠海市");
        item18.add("汕头市");
        item18.add("佛山市");
        item18.add("江门市");
        item18.add("湛江市");
        item18.add("茂名市");
        item18.add("肇庆市");
        item18.add("惠州市");
        item18.add("梅州市");
        item18.add("汕尾市");
        item18.add("河源市");
        item18.add("阳江市");
        item18.add("清远市");
        item18.add("东莞市");
        item18.add("中山市");
        item18.add("潮州市");
        item18.add("揭阳市");
        item18.add("云浮市");
        options_city.add(item18);
        ArrayList<String> item19 = new ArrayList<>();
        item19.add("南宁市");
        item19.add("柳州市");
        item19.add("桂林市");
        item19.add("梧州市");
        item19.add("北海市");
        item19.add("防城港市");
        item19.add("钦州市");
        item19.add("贵港市");
        item19.add("玉林市");
        item19.add("百色市");
        item19.add("贺州市");
        item19.add("河池市");
        item19.add("来宾市");
        item19.add("崇左市");
        options_city.add(item19);
        ArrayList<String> item20 = new ArrayList<>();
        item20.add("海口市");
        item20.add("三亚市");
        item20.add("五指山市");
        item20.add("琼海市");
        item20.add("儋州市");
        item20.add("文昌市");
        item20.add("万宁市");
        item20.add("东方市");
        item20.add("定安市");
        item20.add("屯昌市");
        item20.add("澄迈市");
        item20.add("临高市");
        item20.add("白沙黎族自治县");
        item20.add("昌江黎族自治县");
        item20.add("乐东黎族自治县");
        item20.add("陵水黎族自治县");
        item20.add("保亭黎族自治县");
        item20.add("琼中黎族自治县");
        item20.add("西沙");
        item20.add("南沙");
        item20.add("中沙");
        options_city.add(item20);
        ArrayList<String> item21 = new ArrayList<>();
        item21.add("成都市");
        item21.add("自贡市");
        item21.add("攀枝花市");
        item21.add("泸州市");
        item21.add("德阳市");
        item21.add("绵阳市");
        item21.add("广元市");
        item21.add("遂宁市");
        item21.add("内江市");
        item21.add("乐山市");
        item21.add("南充市");
        item21.add("眉山市");
        item21.add("宜宾市");
        item21.add("广安市");
        item21.add("达州市");
        item21.add("雅安市");
        item21.add("巴中市");
        item21.add("资阳市");
        item21.add("阿坝藏族羌族自治州");
        item21.add("甘孜藏族自治州");
        item21.add("凉山彝族自治州");
        options_city.add(item21);
        ArrayList<String> item22 = new ArrayList<>();
        item22.add("贵阳市");
        item22.add("六盘水市");
        item22.add("遵义市");
        item22.add("安顺市");
        item22.add("铜仁市");
        item22.add("黔西南布依族苗族自治州");
        item22.add("毕节市");
        item22.add("黔东南苗族侗族自治州");
        item22.add("黔南布依族苗族自治州");
        options_city.add(item22);
        ArrayList<String> item23 = new ArrayList<>();
        item23.add("昆明市");
        item23.add("曲靖市");
        item23.add("玉溪市");
        item23.add("保山市");
        item23.add("昭通市");
        item23.add("丽江市");
        item23.add("普洱市");
        item23.add("临沧市");
        item23.add("楚雄彝族自治州");
        item23.add("红河哈尼族彝族自治州");
        item23.add("文山壮族苗族自治州");
        item23.add("西双版纳傣族自治州");
        item23.add("大理白族自治州");
        item23.add("德宏傣族景颇族自治州");
        item23.add("怒江傈僳族自治州");
        item23.add("迪庆藏族自治州");
        options_city.add(item23);
        ArrayList<String> item24 = new ArrayList<>();
        item24.add("拉萨市");
        item24.add("昌都地区");
        item24.add("山南地区");
        item24.add("日喀则市");
        item24.add("那曲地区");
        item24.add("阿里地区");
        item24.add("林芝地区");
        options_city.add(item24);
        ArrayList<String> item25 = new ArrayList<>();
        item25.add("西安市");
        item25.add("铜仁市");
        item25.add("宝鸡市");
        item25.add("咸阳市");
        item25.add("渭南市");
        item25.add("延安市");
        item25.add("汉中市");
        item25.add("榆林市");
        item25.add("安康市");
        item25.add("商洛市");
        options_city.add(item25);
        ArrayList<String> item26 = new ArrayList<>();
        item26.add("兰州市");
        item26.add("嘉峪关市");
        item26.add("金昌市");
        item26.add("白银市");
        item26.add("天水市");
        item26.add("武威市");
        item26.add("张掖市");
        item26.add("平凉市");
        item26.add("酒泉市");
        item26.add("庆阳市");
        item26.add("定西市");
        item26.add("陇南市");
        item26.add("临夏回族自治州");
        item26.add("甘南藏族自治州");
        options_city.add(item26);
        ArrayList<String> item27 = new ArrayList<>();
        item27.add("西宁市");
        item27.add("海东市");
        item27.add("海北藏族自治州");
        item27.add("黄南藏族自治州");
        item27.add("海南藏族自治州");
        item27.add("果洛藏族自治州");
        item27.add("玉树藏族自治州");
        item27.add("海西蒙古族藏族自治州");
        options_city.add(item27);
        ArrayList<String> item28 = new ArrayList<>();
        item28.add("银川市");
        item28.add("石嘴山市");
        item28.add("吴忠市");
        item28.add("固原市");
        item28.add("中卫市");
        options_city.add(item28);
        ArrayList<String> item29 = new ArrayList<>();
        item29.add("乌鲁木齐市");
        item29.add("克拉玛依市");
        item29.add("吐鲁番地区");
        item29.add("哈密地区");
        item29.add("昌吉回族自治州");
        item29.add("博尔塔拉蒙古自治州");
        item29.add("巴音郭楞蒙古自治州");
        item29.add("阿克苏地区");
        item29.add("克孜勒苏柯尔克孜自治州");
        item29.add("喀什地区");
        item29.add("和田地区");
        item29.add("伊犁哈萨克自治州");
        item29.add("塔城地区");
        item29.add("阿勒泰地区");
        item29.add("石河子市");
        item29.add("阿拉尔市");
        item29.add("图木舒克市");
        item29.add("五家渠市");
        options_city.add(item29);
        ArrayList<String> item30 = new ArrayList<>();
        item30.add("台北市");
        item30.add("高雄市");
        item30.add("基隆市");
        item30.add("台中市");
        item30.add("台南市");
        item30.add("新竹市");
        item30.add("嘉义市");
        item30.add("台北县");
        item30.add("宜兰县");
        item30.add("桃园县");
        item30.add("新竹县");
        item30.add("苗栗县");
        item30.add("台中县");
        item30.add("彰化县");
        item30.add("南投县");
        item30.add("云林县");
        item30.add("嘉义县");
        item30.add("台南县");
        item30.add("高雄县");
        item30.add("屏东县");
        item30.add("澎湖县");
        item30.add("台东县");
        item30.add("花莲县");
        options_city.add(item30);
        ArrayList<String> item31 = new ArrayList<>();
        item31.add("阿尔及利亚");
        item31.add("阿根廷");
        item31.add("阿联酋");
        item31.add("埃及");
        item31.add("爱尔兰");
        item31.add("奥地利");
        item31.add("澳大利亚");
        item31.add("巴哈马");
        item31.add("巴基斯坦");
        item31.add("巴西");
        item31.add("白俄罗斯");
        item31.add("比利时");
        item31.add("冰岛");
        item31.add("波兰");
        item31.add("玻利维亚");
        item31.add("伯利兹");
        item31.add("朝鲜");
        item31.add("丹麦");
        item31.add("德国");
        item31.add("俄罗斯");
        item31.add("厄尔多尔");
        item31.add("法国");
        item31.add("菲律宾");
        item31.add("芬兰");
        item31.add("哥伦比亚");
        item31.add("古巴");
        item31.add("关岛");
        item31.add("哈萨克斯坦");
        item31.add("韩国");
        item31.add("荷兰");
        item31.add("加拿大");
        item31.add("加纳");
        item31.add("柬埔寨");
        item31.add("捷克");
        item31.add("卡塔尔");
        item31.add("科威特");
        item31.add("克罗地亚");
        item31.add("肯尼亚");
        item31.add("老挝");
        item31.add("卢森堡");
        item31.add("罗马尼亚");
        item31.add("马尔代夫");
        item31.add("马来西亚");
        item31.add("美国");
        item31.add("蒙古");
        item31.add("孟加拉");
        item31.add("秘鲁");
        item31.add("缅甸");
        item31.add("摩洛哥");
        item31.add("墨西哥");
        item31.add("南非");
        item31.add("尼日利亚");
        item31.add("挪威");
        item31.add("葡萄牙");
        item31.add("日本");
        item31.add("瑞典");
        item31.add("瑞士");
        item31.add("沙特阿拉伯");
        item31.add("斯里兰卡");
        item31.add("苏丹");
        item31.add("泰国");
        item31.add("坦桑尼亚");
        item31.add("土耳其");
        item31.add("委内瑞拉");
        item31.add("乌克兰");
        item31.add("西班牙");
        item31.add("希腊");
        item31.add("新加坡");
        item31.add("新西兰");
        item31.add("匈牙利");
        item31.add("伊拉克");
        item31.add("以色列");
        item31.add("意大利");
        item31.add("印度");
        item31.add("印度尼西亚");
        item31.add("英国");
        item31.add("越南");
        item31.add("智利");
        item31.add("其他");
        options_city.add(item31);
        return options_city;
    }
}
