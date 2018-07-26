package com.example.liang.mobilesafe74.engine;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Xml;
import android.widget.ProgressBar;

import org.xmlpull.v1.XmlSerializer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SmsBackUp {
    private static int index=0;

    //备份短信方法,传一个接口参数，
    public static void backup(Activity activity,Context context, String path, CallBack callBack) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }else {
            FileOutputStream fos=null;
            Cursor cursor=null;
            try {
                //需要用到的对象，上下文环境，备份文件夹的路径，进度条所在的对话框对象，用于备份过程中进度的更新
                //1.获取备份短信写入的文件
                File file = new File(path);
                //2.获取内容解析器,获取短信数据库中数据
                cursor = context.getContentResolver().query(Uri.parse("content://sms/"),
                        new String[]{"address", "date", "type", "body"}, null, null, null);
                //3.文件相应的输出流
                fos = new FileOutputStream(file);
                //4.序列化数据库中读取的数据，放置到xml中
                XmlSerializer xmlSerializer = Xml.newSerializer();
                //5.给此xml做相应的设置
                xmlSerializer.setOutput(fos,"utf-8");
                xmlSerializer.startDocument("utf-8",true);
                xmlSerializer.startTag(null,"smss");
                //备份短信总数的指定
               // pd.setMax(cursor.getCount());
                if (callBack!=null){
                    callBack.setMax(cursor.getCount());
                }
                //6.读取数据库中的每一行的数据写入到xml中
                while (cursor.moveToNext()){
                    xmlSerializer.startTag(null,"sms");

                    xmlSerializer.startTag(null,"address");
                    xmlSerializer.text(cursor.getString(0));
                    xmlSerializer.endTag(null,"address");

                    xmlSerializer.startTag(null,"date");
                    xmlSerializer.text(cursor.getString(1));
                    xmlSerializer.endTag(null,"date");

                    xmlSerializer.startTag(null,"type");
                    xmlSerializer.text(cursor.getString(2));
                    xmlSerializer.endTag(null,"type");

                    xmlSerializer.startTag(null,"body");
                    xmlSerializer.text(cursor.getString(3));
                    xmlSerializer.endTag(null,"body");

                    xmlSerializer.endTag(null,"sms");
                    //每循环一次就需要让进度条叠加
                    index++;
                    Thread.sleep(500);
                    //ProgressDialog可以在子线程更新相应的UI
                    //pd.setProgress(index);
                    if (callBack!=null){
                        callBack.setProgress(index);
                    }
                }
                xmlSerializer.endTag(null,"smss");
                xmlSerializer.endDocument();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    if (cursor!=null&&fos!=null){
                        cursor.close();
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public interface CallBack{
        //短信总数未实现方法(由自己决定是用，对话框.setMax(max)，还是用 进度条.setMax(max))
        public void setMax(int max);
        //备份过程中短信百分比更新（由自己决定是用，对话框.setMax(max)，还是用 进度条.setMax(max)）
        public void setProgress(int index);
    }

}
