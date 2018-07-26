package com.example.liang.mobilesafe74;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Trace;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.liang.mobilesafe74.utils.ConstantValue;
import com.example.liang.mobilesafe74.utils.SpUtil;
import com.example.liang.mobilesafe74.utils.StreamUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class SplashActivity extends AppCompatActivity {

    protected static final String tag="SplashActivity";
    //更新新版本状态码
    protected static final int updata_version=100;
    //进入程序的状态码
    protected static final int enter_home=101;

    private RelativeLayout rl_root;
    private String mDownloadUrl;
    private String mVersionDes;
    private TextView tv_version_name;
    private int mLocalVersionCode;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case updata_version:
                    //弹出对话框，提示用户更新
                    showUpdateDialog();
                    break;
                case enter_home:
                    //进入主界面
                    enterHome();
                    break;

            }
        }
    };

    //弹出对话框,提示用户更新
    private void showUpdateDialog() {
        //对话框依赖于activity存在的
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //设置左上角图标
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("版本更新");
        //设置描述内容
        builder.setMessage(mVersionDes);
        //积极按钮，立即更新
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //下载APK,apk链接地址
                downloadApk();
            }
        });
        builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //取消对话框,进入主界面
                enterHome();
            }
        });

        //点击取消的事件监听
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //用户点击取消，也需要让其进入
                    enterHome();
                    dialog.dismiss();
            }
        });
        builder.show();
    }


    private void downloadApk() {
        //链接地址,放置apk的所在路径
        //判断SDK是否可用
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            //获取sd卡的路径
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "mobilesafe74.apk";
            //发送请求，并且放置到指定路径(下载地址,下载应用放的位置)
          HttpUtils httpUtils=new HttpUtils();
          httpUtils.download(mDownloadUrl, path, new RequestCallBack<File>() {
              //下载成功
              public void onSuccess(ResponseInfo<File> responseInfo) {
                  //下载过后放置在sd卡中的apk
                  Log.i(tag,"下载成功");
                  File file = responseInfo.result;
                  //提示用户安装
                  installApk(file);
              }

              //下载失败
              public void onFailure(HttpException e, String s) {
                  Log.i(tag,"下载失败");
              }

              //刚开始下载的方法
              public void onStart() {
                  Log.i(tag,"刚刚开始下载");
                  super.onStart();
              }

              //下载过程中的方法(总大小，当前的下载位置,是否在下载)
              public void onLoading(long total, long current, boolean isUploading) {
                  Log.i(tag,"下载中....");
                  super.onLoading(total, current, isUploading);
              }
          });
        }

    }

    //apk安装的方法
    private void installApk(File file) {
        //系统应用界面,源码,安装apk入口
        Intent intent = new Intent("android.intent.action.VIEW");//action,需要跳转到安装apk的界面
        intent.addCategory("android.intent.category.DEFAULT");
        //文件作为数据源
        intent.setData(Uri.fromFile(file));
        //设置安装类型
        intent.setType("application/vnd.android.package-archive");
//        startActivity(intent);
        startActivityForResult(intent,0);
    }

    //开启activity后，返回结果调用的方法
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }

    //进入程序主界面
    private void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        //在开启一个新的界面后，将导航界面关闭(只可见一次)
        finish();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //初始化UI
        initUI();
        //初始化数据
        initData();
        //初始化动画
        initAnimation();
        //初始化数据库
        initDB();
        //生成快捷方式
        initShortCut();
    }

    //生成快捷方式
    private void initShortCut() {
        //给intent维护图标，名称
        Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        //维护图标
         intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
         //维护名称
         intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,"黑马卫士74");
         //点击快捷方式后跳转到的activity
        //维护要去开启的意图对象
        Intent shortCutIntent = new Intent("android.intent.action.HOME");
        shortCutIntent.addCategory("android.intent.category.DEFAULT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,shortCutIntent);
        //发送广播
        sendBroadcast(intent);

    }

    private void initDB() {
        //1.归属地数据库拷贝
        initAddressDB("address.db");
        //3.拷贝病毒数据库
        initAddressDB("antivirus.db");
    }


    //dbName数据库名称
    private void initAddressDB(String dbName) {
        File files = getFilesDir();//只是文件夹路径
        File file = new File(files, dbName);
        if (file.exists()){
            return;
        }
        //2.读取第三方资产目录下的文件
        InputStream stream=null;
        FileOutputStream fos=null;
        try {
            stream = getAssets().open(dbName);
            //读取内容写入指定文件夹中去
            fos = new FileOutputStream(file);
            //每次的读取内容
            byte[]bs=new byte[1024];
            int temp=-1;
            while ((temp=stream.read(bs))!=-1){
                fos.write(bs,0,temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (stream!=null&&fos!=null){
                    stream.close();
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    //添加淡入淡出的动画效果
    private void initAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(3000);
        rl_root.startAnimation(alphaAnimation);

    }

    /*
    获取数据的方法
     */
    private void initData() {
        //应用版本
        tv_version_name.setText("版本：" + getVersionName());
        //2.检测是否有更新，如果有更新，提示用户下载(本地版本号和服务器版本号对比)
        //获取本地版本号
        mLocalVersionCode = getVersionCode();
        //获取服务器版本号(客户端请求，服务的给相应(json,xml)) 返回200，以流的方式将数据返回
        //json中内容包含：更新版本的版本名称,新版本的描述信息,服务端版本号，apk下载地址
        if (SpUtil.getBoolean(this, ConstantValue.open_update,false)){
            checkVersion();
        }else {
            //直接进入主界面
            //消息机制 发送消息4秒后 处理指定的消息
            mHandler.sendEmptyMessageDelayed(enter_home,4000);
        }
    }

    /*
    检测版本号
     */
    private void checkVersion() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message msg = Message.obtain();
                    long startTime = System.currentTimeMillis();
                    //发送请求获取数据,参数为请求json的链接地址
                    try {
                        //封装地址
                        URL url = new URL("http://192.168.2.4:8080/updata74.json");
                        //开启一个链接
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        //设置请求参数
                        //请求超时
                        connection.setConnectTimeout(2000);
                        //读取超时
                        connection.setReadTimeout(2000);
                        //默认就是GET请求
//                        connection.setRequestMethod("GET");
                        //获取响应码
                       if (connection.getResponseCode()==200){
                           //以流的形式将数据获取
                           InputStream is = connection.getInputStream();
                           //流转字符串
                           String json = StreamUtil.streamToString(is);
                           //打印的log
                           Log.d(tag,json);
                           //json的解析
                           JSONObject jsonObject = new JSONObject(json);
                           String versionName = jsonObject.getString("versionName");
                           Log.d(tag,versionName);
                           mVersionDes = jsonObject.getString("versionDes");
                           Log.d(tag,mVersionDes);
                           String versionCode = jsonObject.getString("versionCode");
                           Log.d(tag,versionCode);
                           mDownloadUrl = jsonObject.getString("downloadUrl");
                           Log.d(tag,mDownloadUrl);

                           //比对版本号
                           if (mLocalVersionCode<Integer.parseInt(versionCode)){
                               //提示用户更新  弹出对话框 消息机制5
                               msg.what=updata_version;
                           }else {
                               //进入应用程序主界面
                               msg.what=enter_home;
                           }
                       }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        //睡眠时间,请求网络时间超过4秒，不做处理
                        //请求网络小于4秒，睡满4秒
                        long endTime = System.currentTimeMillis();
                        if (endTime-startTime<4000){
                            try {
                                Thread.sleep(4000-(endTime-startTime));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        mHandler.sendMessage(msg);
                    }
                }
            }).start();

    }

    /*
    返回版本号
     */
    private int getVersionCode() {
        try {
            //1.获取包的管理者
            PackageManager pm = getPackageManager();
            //2.从对象中获取指定包名的基本信息(版本名称，版本号),传0代表获取基本信息
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            //3.获取版本名称
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /*
    获取版本名称  返回null 代表异常
     */
    private String getVersionName() {
        try {
            //1.获取包的管理者
            PackageManager pm = getPackageManager();
            //2.从对象中获取指定包名的基本信息(版本名称，版本号),传0代表获取基本信息
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            //3.获取版本名称
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    初始化UI的方法
     */
    private void initUI() {
         tv_version_name =(TextView) findViewById(R.id.tv_version_name);
         rl_root =(RelativeLayout) findViewById(R.id.rl_root);

    }
}
