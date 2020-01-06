package com.dongxun.lichunkai.cloudmusic.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dongxun.lichunkai.cloudmusic.Adapter.MainPagerAdapter;
import com.dongxun.lichunkai.cloudmusic.Adapter.PersonalizedAdapter;
import com.dongxun.lichunkai.cloudmusic.Adapter.SearcHistoryAdapter;
import com.dongxun.lichunkai.cloudmusic.Adapter.SearchAdapter;
import com.dongxun.lichunkai.cloudmusic.Bean.Song;
import com.dongxun.lichunkai.cloudmusic.Bean.SongSheet;
import com.dongxun.lichunkai.cloudmusic.Class.ActivityCollector;
import com.dongxun.lichunkai.cloudmusic.Class.BaseActivity;
import com.dongxun.lichunkai.cloudmusic.Class.CircleImageView;
import com.dongxun.lichunkai.cloudmusic.Class.MusicMediaPlayer;
import com.dongxun.lichunkai.cloudmusic.Class.ResizableImageView;
import com.dongxun.lichunkai.cloudmusic.Common.Common;
import com.dongxun.lichunkai.cloudmusic.LocalBroadcast.SendLocalBroadcast;
import com.dongxun.lichunkai.cloudmusic.PopWindow.ListWindow;
import com.dongxun.lichunkai.cloudmusic.PopWindow.SongDetailsWindow;
import com.dongxun.lichunkai.cloudmusic.R;
import com.dongxun.lichunkai.cloudmusic.Util.PermissionUtil;
import com.dongxun.lichunkai.cloudmusic.Util.ToolHelper;
import com.gyf.immersionbar.ImmersionBar;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.dongxun.lichunkai.cloudmusic.Common.Common.songList;
import static com.dongxun.lichunkai.cloudmusic.Util.ToolHelper.openImage;
import static com.dongxun.lichunkai.cloudmusic.Util.ToolHelper.showToast;

/**
 * 播放器主页
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, OnBannerListener {

    private String TAG = "MainActivity";
    private ImageView imageView_search;
    private Activity Context;

    //其他
    private LocalBroadcastManager localBroadcastManager;
    private MusicReceiver musicReceiver;
    private IntentFilter intentFilter;
    //自定义播放器
    private MusicMediaPlayer musicMediaPlayer;

    //底部布局组件
    private LinearLayout LinearLayout_playing;
    private TextView textView_name;
    private ImageView imageView_playOrPause;
    private ImageView imageView_list;
    private ImageView imageView_head;

    //viewpager相关
    private View view_my, view_find, view_video;
    private ViewPager viewPager;  //对应的viewPager
    private List<View> viewList;//view数组

    //顶部标题
    private TextView textView_my;
    private TextView textView_find;
    private TextView textView_county;

    //viewpager_my组件
    private LinearLayout LinearLayout_myInfo;//我的信息
    private CircleImageView CircleImageView_head;//头像
    private TextView textView_nickName;//昵称
    private LinearLayout LinearLayout_local;//本地音乐
    private LinearLayout LinearLayout_diantai;//我的电台
    private LinearLayout LinearLayout_like;//收藏
    private LinearLayout LinearLayout_new;//关注新歌
    private RelativeLayout RelativeLayout_like;//我喜欢的音乐
    private RelativeLayout RelativeLayout_personalFM;//私人FM
    private ResizableImageView ResizableImageView_background;

    //view_find组件
    private Banner banner;
    private List banner_imgs = new ArrayList<>();//banner图
    private List banner_titles = new ArrayList<>();//banner标题
    private List banner_url = new ArrayList<>();//banner URL
    private List banner_targetType = new ArrayList<>();//banner内容类型
    private ArrayList<Song> banner_song = new ArrayList<>();//banner对应歌曲
    private LinearLayout LinearLayout_dailyRecommend;//每日推荐
    private LinearLayout LinearLayout_songList;//歌单
    private LinearLayout LinearLayout_rankingList;//排行榜
    private LinearLayout LinearLayout_radioStation;//电台
    private RecyclerView recyclerView_personalized;//推荐歌单recyclerView
    private ArrayList<SongSheet> songSheets = new ArrayList<>();//推荐歌单
    private PersonalizedAdapter personalizedAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager_personalized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        ActivityCollector.removeOther(this);
        initStateBar();
        initView();
        setViewpager();
        initReceiver();

        getPermission();
        updateUI();
        getBanner();
        getPersonalized();
        setPersonalizedAdapter();
    }

    /**
     * 设置Personalized适配器（瀑布流布局）
     */
    private void setPersonalizedAdapter() {
        staggeredGridLayoutManager_personalized = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        recyclerView_personalized.setLayoutManager(staggeredGridLayoutManager_personalized);
        personalizedAdapter = new PersonalizedAdapter(songSheets);
        recyclerView_personalized.setAdapter(personalizedAdapter);
        personalizedAdapter.setOnItemClickListener(new PersonalizedAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                //点击歌单
                showToast(MainActivity.this,songSheets.get(position).getName());
            }
        });
    }

    /**
     * 获取推荐歌单
     */
    private void getPersonalized() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                    Request request = new Request.Builder()
                            .url("http://www.willdonner.top:3000/personalized?limit=6")
                            .build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String responseData = response.body().string();//处理返回的数据
                            Log.e(TAG, "onResponse: "+responseData);
                            //处理JSON
                            try {
                                JSONObject newRes = new JSONObject(responseData);
                                String code = newRes.getString("code");
                                if (code.equals("200")){
                                    Log.e(TAG, "推荐歌单获取成功");
                                    JSONArray result = newRes.getJSONArray("result");
                                    for (int i=0;i<result.length();i++){
                                        String id = result.getJSONObject(i).getString("id");//歌单id
                                        String name = result.getJSONObject(i).getString("name");//歌单名
                                        String copywriter = result.getJSONObject(i).getString("copywriter");//文案
                                        String picUrl = result.getJSONObject(i).getString("picUrl");//封面图
                                        String playCount = result.getJSONObject(i).getString("playCount");//播放量

                                        SongSheet songSheet = new SongSheet();
                                        songSheet.setId(id);
                                        songSheet.setName(name);
                                        songSheet.setCopywriter(copywriter);
                                        songSheet.setPicUrl(picUrl);
                                        songSheet.setPlayCount(playCount);
                                        songSheets.add(songSheet);
                                    }
                                    //刷新界面
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            personalizedAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }else {
                                    Log.e(TAG, "推荐歌单获取失败");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 获取banner
     */
    private void getBanner() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                    Request request = new Request.Builder()
                            .url("http://www.willdonner.top:3000/banner?type=1")
                            .build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String responseData = response.body().string();//处理返回的数据
                            Log.e(TAG, "onResponse: "+responseData);
                            //处理JSON
                            try {
                                JSONObject newRes = new JSONObject(responseData);
                                String code = newRes.getString("code");
                                if (code.equals("200")){
                                    Log.e(TAG, "banner获取成功");
                                    JSONArray banners = newRes.getJSONArray("banners");
                                    for (int i=0;i<banners.length();i++) {
                                        String pic = banners.getJSONObject(i).getString("pic");//banner图地址
                                        String typeTitle = banners.getJSONObject(i).getString("typeTitle");//banner 标题
                                        String url = banners.getJSONObject(i).getString("url");//banner URL
                                        String targetType = banners.getJSONObject(i).getString("targetType");//banner内容类型
                                        Log.e(TAG, "onResponse: "+pic);
                                        Log.e(TAG, "onResponse: "+typeTitle);
                                        Log.e(TAG, "onResponse: "+url);
                                        banner_titles.add(typeTitle);
                                        banner_url.add(url);
                                        banner_imgs.add(pic);
                                        banner_targetType.add(targetType);
                                        //新歌首发/独家（播放音频）
                                        if (targetType.equals("1")){
                                            String id = banners.getJSONObject(i).getJSONObject("song").getString("id");//歌曲id
                                            String name = banners.getJSONObject(i).getJSONObject("song").getString("name");//歌名
                                            String artis = banners.getJSONObject(i).getJSONObject("song").getJSONArray("ar").getJSONObject(0).getString("name");//艺术家

                                            Song song = new Song();
                                            song.setId(id);
                                            song.setName(name);
                                            song.setArtist(artis);

                                            banner_song.add(song);
                                        }else {
                                            banner_song.add(null);
                                        }
                                    }
                                    SendLocalBroadcast.refreshBanner(MainActivity.this);
                                }else {
                                    Log.e(TAG, "banner获取失败");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 设置Banner
     */
    private void setBanner() {
        //设置内置样式，共有六种可以点入方法内逐一体验使用。
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器，图片加载器在下方
        banner.setImageLoader(new MyLoader());
        //设置图片网址或地址的集合
        banner.setImages(banner_imgs);
        //设置轮播的动画效果，内含多种特效，可点入方法内查找后内逐一体验
        banner.setBannerAnimation(Transformer.Default);
        //设置轮播图的标题集合
        banner.setBannerTitles(banner_titles);
        //设置轮播间隔时间
        banner.setDelayTime(3000);
        //设置是否为自动轮播，默认是“是”。
        banner.isAutoPlay(true);
        //设置指示器的位置，小点点，左中右。
        banner.setIndicatorGravity(BannerConfig.CENTER).setOnBannerListener(this).start();
    }

    //自定义的图片加载器

    private class MyLoader extends ImageLoader {

        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {

            Glide.with(context).load((String) path).into(imageView);
        }

    }

    /**
     * banner监听
     * @param position 当前轮播图片位置
     */
    @Override
    public void OnBannerClick(int position) {
        Log.i("tag", "你点了第"+position+"张轮播图");
        //根据banner信息进行具体动作
        String targetType = banner_targetType.get(position).toString();//banner内容类型
        String title = banner_titles.get(position).toString();//banner 标题
        switch (targetType){
            case "1004":
                //MV首发(播放MV)
                showToast(this,title);
                break;
            case "1":
                //新歌首发/独家（播放音频）
                showToast(this,title);
                //更改公共变量
                Common.song_playing = banner_song.get(position);
                //发送本地广播播放
                SendLocalBroadcast.playNew(this);
                //跳转(带参数，说明是播放新歌曲)
                Intent intent = new Intent(this,PlayActivity.class);
                startActivity(intent);
                break;
            case "1009":
                //独家电台
                showToast(this,title);
                break;
            case "3000":
                //独家策划（打开URL）
                showToast(this,title);
                //跳转(带参数，说明是播放新歌曲)
                Intent intent_web = new Intent(this,WebActivity.class);
                intent_web.putExtra("WEBURL",banner_url.get(position).toString());
                startActivity(intent_web);
                break;
        }
    }

    /**
     * 更新用户数据
     */
    private void updateUI() {
        textView_nickName.setText(Common.user.getNickname());
        if (Common.user.getAvatarUrl_bitmap() == null){
            //下载头像
            downloadImg(Common.user.getUserId(),Common.user.getAvatarUrl(),"Avatar");
        }else {
            CircleImageView_head.setImageBitmap(Common.user.getAvatarUrl_bitmap());
        }
        if (Common.user.getBackgroundUrl_bitmap() == null){
            //下载背景
            downloadImg(Common.user.getUserId(),Common.user.getBackgroundUrl(),"Background");
        }else {
            ResizableImageView_background.setImageBitmap(Common.user.getBackgroundUrl_bitmap());
        }
    }

    /**
     * 下载具体操作
     * @param downloadUrl   下载的文件地址
     * @return
     */
    private void downloadImg(final String songID, final String downloadUrl, final String type) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //创建文件夹
                    String dirName = "";
                    //下载后的文件名
                    //设置下载位置和名称
                    dirName = Environment.getExternalStorageDirectory() + "/CloudMusic/user/";
                    final String fileName = type.equals("Avatar")?dirName + songID + "Avatar.jpg":dirName + songID + "Background.jpg";

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
                    //更改Common.user
                    if (type.equals("Avatar")){
                        //头像
                        Common.user.setAvatarUrl_bitmap(openImage(fileName));
                        CircleImageView_head.setImageBitmap(Common.user.getAvatarUrl_bitmap());
                    }else {
                        //背景
                        Common.user.setBackgroundUrl_bitmap(openImage(fileName));
                        ResizableImageView_background.setImageBitmap(Common.user.getBackgroundUrl_bitmap());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }

    /**
     * 设置Viewpager
     */
    private void setViewpager() {
        viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
        viewList.add(view_my);
        viewList.add(view_find);
        viewList.add(view_video);

        //设置PagerAdapter
        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(viewList,this);
        viewPager.setAdapter(mainPagerAdapter);
        //监听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             * 页面滑动状态停止前一直调用
             *
             * @param position 当前点击滑动页面的位置
             * @param positionOffset 当前页面偏移的百分比
             * @param positionOffsetPixels 当前页面偏移的像素位置
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.e("vp", "滑动中=====position:" + position + "   positionOffset:" + positionOffset + "   positionOffsetPixels:" + positionOffsetPixels);
            }

            /**
             * 滑动后显示的页面和滑动前不同，调用
             *
             * @param position 选中显示页面的位置
             */
            @Override
            public void onPageSelected(int position) {
                Log.e(TAG, "显示页改变=====postion:" + position);
                //设置标题样式
                switch (position){
                    case 0:
                        textView_county.setTextColor(Color.parseColor("#dfdfdf"));
                        textView_county.setTextSize(16f);
                        textView_find.setTextColor(Color.parseColor("#dfdfdf"));
                        textView_find.setTextSize(16f);

                        textView_my.setTextColor(Color.parseColor("#ffffff"));
                        textView_my.setTextSize(20f);
                        //切换搜索图标
                        imageView_search.setImageResource(R.drawable.logo_white_search);
                        //轮播图
                        banner.stopAutoPlay();
                        break;
                    case 1:
                        textView_my.setTextColor(Color.parseColor("#adadad"));
                        textView_my.setTextSize(16f);
                        imageView_search.setImageResource(R.drawable.logo_search);
                        textView_county.setTextColor(Color.parseColor("#adadad"));
                        textView_county.setTextSize(16f);

                        textView_find.setTextColor(Color.parseColor("#000000"));
                        textView_find.setTextSize(20f);
                        //轮播图
                        banner.startAutoPlay();
                        break;
                    case 2:
                        textView_find.setTextColor(Color.parseColor("#adadad"));
                        textView_find.setTextSize(16f);

                        textView_county.setTextColor(Color.parseColor("#000000"));
                        textView_county.setTextSize(20f);
                        //轮播图
                        banner.stopAutoPlay();
                        break;
                }
            }

            /**
             * 页面状态改变时调用
             * @param state 当前页面的状态
             *              SCROLL_STATE_IDLE：空闲状态
             *              SCROLL_STATE_DRAGGING：滑动状态
             *              SCROLL_STATE_SETTLING：滑动后滑翔的状态
             */
            @Override
            public void onPageScrollStateChanged(int state) {
//                switch (state) {
//                    case ViewPager.SCROLL_STATE_IDLE:
//                        Log.e("vp", "状态改变=====SCROLL_STATE_IDLE====静止状态");
//                        break;
//                    case ViewPager.SCROLL_STATE_DRAGGING:
//                        Log.e("vp", "状态改变=====SCROLL_STATE_DRAGGING==滑动状态");
//                        break;
//                    case ViewPager.SCROLL_STATE_SETTLING:
//                        Log.e("vp", "状态改变=====SCROLL_STATE_SETTLING==滑翔状态");
//                        break;
//                }
            }
        });
    }

    /**
     * 创建文件夹（根目录下新建文件夹CloudMusic及其子文件夹mp3及jpg）
     */
    private void createDirectory() {
        File sd=Environment.getExternalStorageDirectory();
        //创建总文件夹
        String path_sum=sd.getPath()+"/CloudMusic";
        File file_sum=new File(path_sum);
        if(!file_sum.exists())
            file_sum.mkdir();
        //创建歌曲文件夹
        String path_song=sd.getPath()+"/CloudMusic/mp3";
        File file_song=new File(path_song);
        if(!file_song.exists())
            file_song.mkdir();
        //创建封面文件夹
        String path_cover=sd.getPath()+"/CloudMusic/cover";
        File file_cover=new File(path_cover);
        if(!file_cover.exists())
            file_cover.mkdir();
        //创建歌词文件夹
        String path_lyric=sd.getPath()+"/CloudMusic/lyric";
        File file_lyric=new File(path_lyric);
        if(!file_lyric.exists())
            file_lyric.mkdir();
        //创建歌曲详情文件夹
        String path_details=sd.getPath()+"/CloudMusic/details";
        File file_details=new File(path_details);
        if(!file_details.exists())
            file_details.mkdir();
        //创建用户文件夹
        String path_user=sd.getPath()+"/CloudMusic/user";
        File file_user=new File(path_user);
        if(!file_user.exists())
            file_user.mkdir();
    }


    /**
     * 获取读写权限
     */
    private void getPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            PermissionUtil.getInstance().requestSD(this);
        }else {
            createDirectory();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 502:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    createDirectory();
                }else {
                    Toast.makeText(this,"用户拒绝了权限申请",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 初始化组件
     */
    private void initView() {
        musicMediaPlayer = new MusicMediaPlayer(this);
        imageView_search = findViewById(R.id.imageView_search);
        imageView_search.setOnClickListener(this);

        //底部组件
        LinearLayout_playing = findViewById(R.id.LinearLayout_playing);
        LinearLayout_playing.setOnClickListener(this);
        imageView_head = findViewById(R.id.imageView_head);
        textView_name = findViewById(R.id.textView_name);
        imageView_playOrPause = findViewById(R.id.imageView_playOrPause);
        imageView_playOrPause.setOnClickListener(this);
        imageView_list = findViewById(R.id.imageView_list);
        imageView_list.setOnClickListener(this);
        //顶部标题
        textView_my = findViewById(R.id.textView_my);
        textView_my.setOnClickListener(this);
        textView_find = findViewById(R.id.textView_find);
        textView_find.setOnClickListener(this);
        textView_county = findViewById(R.id.textView_county);
        textView_county.setOnClickListener(this);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        //实例化view
        LayoutInflater inflater=getLayoutInflater();
        view_my = inflater.inflate(R.layout.viewpager_my, null);
        view_find = inflater.inflate(R.layout.viewpager_find,null);
        view_video = inflater.inflate(R.layout.viewpager_video, null);

        //viewpager_my组件
        LinearLayout_myInfo = view_my.findViewById(R.id.LinearLayout_myInfo);//我的信息
        LinearLayout_myInfo.setOnClickListener(this);
        CircleImageView_head = view_my.findViewById(R.id.CircleImageView_head);//头像
        textView_nickName = view_my.findViewById(R.id.textView_nickName);//昵称
        LinearLayout_local = view_my.findViewById(R.id.LinearLayout_local);//本地音乐
        LinearLayout_local.setOnClickListener(this);
        LinearLayout_diantai = view_my.findViewById(R.id.LinearLayout_diantai);//我的电台
        LinearLayout_diantai.setOnClickListener(this);
        LinearLayout_like = view_my.findViewById(R.id.LinearLayout_like);//我的收藏
        LinearLayout_like.setOnClickListener(this);
        LinearLayout_new = view_my.findViewById(R.id.LinearLayout_new);//关注新歌
        LinearLayout_new.setOnClickListener(this);
        RelativeLayout_like = view_my.findViewById(R.id.RelativeLayout_like);//我喜欢的音乐
        RelativeLayout_like.setOnClickListener(this);
        RelativeLayout_personalFM = view_my.findViewById(R.id.RelativeLayout_personalFM);//私人FM
        RelativeLayout_personalFM.setOnClickListener(this);
        ResizableImageView_background = view_my.findViewById(R.id.ResizableImageView_background);//背景图

        //view_find组件
        banner = view_find.findViewById(R.id.banner);
        LinearLayout_dailyRecommend = view_find.findViewById(R.id.LinearLayout_dailyRecommend);//每日推荐
        LinearLayout_dailyRecommend.setOnClickListener(this);
        LinearLayout_songList = view_find.findViewById(R.id.LinearLayout_songList);//歌单
        LinearLayout_songList.setOnClickListener(this);
        LinearLayout_rankingList = view_find.findViewById(R.id.LinearLayout_rankingList);//排行榜
        LinearLayout_rankingList.setOnClickListener(this);
        LinearLayout_radioStation = view_find.findViewById(R.id.LinearLayout_radioStation);//电台
        LinearLayout_radioStation.setOnClickListener(this);
        recyclerView_personalized = view_find.findViewById(R.id.recyclerView_personalized);//推荐歌单recyclerView
    }

    /**
     * 初始化本地广播接收器
     */
    private void initReceiver() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.dongxun.lichunkai.cloudmusic.MUSIC_BROADCAST");
        musicReceiver = new MusicReceiver();
        localBroadcastManager.registerReceiver(musicReceiver,intentFilter);
    }

    /**
     * 初始化状态栏
     */
    private void initStateBar() {
        ImmersionBar.with(this).init();
        getSupportActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }


    /**
     * 点击事件
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageView_search:
                Intent intent_search = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent_search);
                break;
            case R.id.LinearLayout_playing:
                Intent intent_play = new Intent(MainActivity.this,PlayActivity.class);
                startActivity(intent_play);
                break;
            case R.id.imageView_playOrPause:
                if (Common.song_playing.getId() != null) {
                    //发送本地广播播放
                    SendLocalBroadcast.playOrPause(this);
                }else {
                    Toast.makeText(this,"当前暂无歌曲，快去选一首吧",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.imageView_list:
                Toast.makeText(this,"歌单",Toast.LENGTH_SHORT).show();
                //弹出歌曲详情窗口
                ListWindow listWindow = new ListWindow(this);
                listWindow.show();
                break;

            case R.id.textView_my:
                //切换viewPager
                viewPager.setCurrentItem(0);
                break;
            case R.id.textView_find:
                //切换viewPager
                viewPager.setCurrentItem(1);
                break;
            case R.id.textView_county:
                //切换viewPager
                viewPager.setCurrentItem(2);
                break;

            case R.id.LinearLayout_myInfo:
                showToast(this,"我的信息");
                //弹出个人信息
                break;
            case R.id.LinearLayout_local:
                showToast(this,"本地音乐");
                break;
            case R.id.LinearLayout_diantai:
                showToast(this,"我的电台");
                break;
            case R.id.LinearLayout_like:
                showToast(this,"我的收藏");
                break;
            case R.id.LinearLayout_new:
                showToast(this,"关注新歌");
                break;
            case R.id.RelativeLayout_like:
                showToast(this,"我喜欢的音乐");
                break;
            case R.id.RelativeLayout_personalFM:
                showToast(this,"私人FM");
                break;

            case R.id.LinearLayout_dailyRecommend:
                showToast(this,"每日推荐");
                break;
            case R.id.LinearLayout_songList:
                showToast(this,"歌单");
                break;
            case R.id.LinearLayout_rankingList:
                showToast(this,"排行榜");
                break;
            case R.id.LinearLayout_radioStation:
                showToast(this,"电台");
                break;
        }

    }

    /**
     * 获取歌曲URL
     */
    public void getSongUrl(){
        WebView webView = new WebView(this);
        webView.loadUrl("https://music.163.com/song/media/outer/url?id="+Common.song_playing.getId());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            //页面加载开始
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
            //页面加载完成
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String realUrl = url;
//                下载歌曲
                if (realUrl.equals("")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,"这首歌需要会员，暂时无法收听...",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    //这个realUrl即为重定向之后的地址
                  download(Common.song_playing.getId(),realUrl);
                }

            }
        });

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try{
//                    OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
//                    //和风请求方式
//                    Request request = new Request.Builder()
//                            .url("http://45.32.132.229:3000/song/url?id="+ Common.song_playing.getId())
//                            .build();//创建一个Request对象
//                    //第三步构建Call对象
//                    Call call = client.newCall(request);
//                    //第四步:异步get请求
//                    call.enqueue(new Callback() {
//                        @Override
//                        public void onFailure(Call call, IOException e) {
//                        }
//                        @Override
//                        public void onResponse(Call call, Response response) throws IOException {
//                            final String responseData = response.body().string();//处理返回的数据
//                            try {
//                                //解析JSON
//                                JSONObject object = new JSONObject(responseData);
//                                String code = object.getString("code");
//                                if (code.equals("200")){
//                                    JSONArray data = object.getJSONArray("data");
//                                    String downloadUrl = data.getJSONObject(0).getString("url");
//                                    //下载歌曲
//                                    if (downloadUrl.equals("")){
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                Toast.makeText(MainActivity.this,"这首歌需要会员，暂时无法收听...",Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                                    }else {
//                                        download(Common.song_playing.getId(),downloadUrl);
//                                    }
//                                }else {
//                                    //错误
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }

    /**
     * 下载具体操作
     * @param downloadUrl   下载的文件地址
     * @return
     */
    private void download(final String songID, final String downloadUrl) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    //创建文件夹
                    String dirName = "";
                    //下载后的文件名
                    //设置下载位置和名称
                    dirName = Environment.getExternalStorageDirectory() + "/CloudMusic/mp3/";
                    final String fileName = dirName + songID + ".mp3";

                    File file1 = new File(fileName);

                    if (file1.exists()) {
                        //文件存在
                        Log.e("DOWLOAD", "mp3文件已存在！");
                    }else {
                        URL url = new URL(downloadUrl);
                        //打开连接
                        URLConnection conn = url.openConnection();
                        //打开输入流
                        InputStream is = conn.getInputStream();
                        //获得长度
                        int contentLength = conn.getContentLength();
                        Log.e("DOWLOAD", "mp3文件长度 = " + contentLength);
                        //创建字节流
                        byte[] bs = new byte[1024];
                        int len;
                        OutputStream os = new FileOutputStream(fileName);
                        //写数据
                        while ((len = is.read(bs)) != -1) {
                            os.write(bs, 0, len);
                        }
                        //完成后关闭流
                        Log.e("DOWLOAD", "mp3文件不存在,下载成功！");
                        os.close();
                        is.close();
                    }
                    //显示时间
                    Common.song_playing.setSunTime(ToolHelper.getAudioFileVoiceTime(fileName));
                    //播放
                    musicMediaPlayer.initMediaPlayer(Context);
                    musicMediaPlayer.startOption();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }


    @Override
    protected void onResume() {
        //更新封面
        if (Common.song_playing.getCover() != null) imageView_head.setImageBitmap(Common.song_playing.getCover());
        //更新歌曲显示
        textView_name.setText(Common.song_playing.getName() == null?"暂无歌曲":Common.song_playing.getName());
        super.onResume();
    }

    /**
     * 本地广播接收器（音乐控制）
     */
    public class MusicReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("ACTION");
            Toast.makeText(context,"收到广播：" + action,Toast.LENGTH_SHORT).show();
            switch (action){
                case "PLAYNEW":
                    //停止当前播放
                    musicMediaPlayer.stopOption();
                    Common.lyricPosition_playing = 0;
                    //获取歌曲信息并下载播放
                    getSongUrl();
                    //更新UI
                    imageView_playOrPause.setImageResource(R.drawable.logo_pause2);
                    break;
                case "PLAY_PAUSE":
                    //暂停/播放
                    if (musicMediaPlayer.isPlayingOption()){
                        //暂停
                        musicMediaPlayer.pauseOption();
                        imageView_playOrPause.setImageResource(R.drawable.logo_play2);
                        Common.state_playing = false;
                    }else {
                        //播放
                        musicMediaPlayer.startOption();
                        imageView_playOrPause.setImageResource(R.drawable.logo_pause2);
                        Common.state_playing = true;
                    }
                    break;
                case "LAST":
                    //上一曲
                    Toast.makeText(context,"上一曲",Toast.LENGTH_SHORT).show();
                    for (Song song: songList){
                        Log.e(TAG, "当前歌单歌曲: "+song.getName());
                    }
                    //更改公共变量
                    Common.song_playing = songList.get((ToolHelper.getSongListPosition()-1 < 0)? songList.size()-1:ToolHelper.getSongListPosition()-1);
                    //发送本地广播播放
                    SendLocalBroadcast.playNew(context);
                    SendLocalBroadcast.refreshCover(context);
                    break;
                case "NEXT":
                    //下一曲
                    Toast.makeText(context,"下一曲",Toast.LENGTH_SHORT).show();
                    for (Song song: songList){
                        Log.e(TAG, "当前歌单歌曲: "+song.getName());
                    }
                    //更改公共变量
                    Common.song_playing = songList.get((ToolHelper.getSongListPosition()+1> songList.size()-1)?0:ToolHelper.getSongListPosition()+1);
                    //发送本地广播播放
                    SendLocalBroadcast.playNew(context);
                    SendLocalBroadcast.refreshCover(context);
                    break;
                case "CHANGEPROGRESS":
                    //更改歌曲进度
                    Toast.makeText(context,"更改歌曲进度："+ Common.changeProgress,Toast.LENGTH_SHORT).show();
                    musicMediaPlayer.seekToOption();
                    break;
                case "COMPLETE":
                    //更改UI
                    imageView_playOrPause.setImageResource(R.drawable.logo_play2);
                    //播放完当前音频
                    Toast.makeText(context,"播放完当前音频",Toast.LENGTH_SHORT).show();
                    musicMediaPlayer.pauseOption();

                    Common.changeProgress = 0;
                    musicMediaPlayer.seekToOption();
                    Common.state_playing = false;
                    Common.lyricPosition_playing = 0;
                    break;
                case "BANNER":
                    setBanner();
                    break;
            }
        }
    }

    /**
     * 退出应用停止播放
     */
    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy: 退出应用");
        //退出播放器
        musicMediaPlayer.exitOption();
        super.onDestroy();
    }

    /**
     * 最小化
     */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}