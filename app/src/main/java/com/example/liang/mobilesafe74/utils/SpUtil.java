package com.example.liang.mobilesafe74.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtil {
    //写
    private static SharedPreferences sp;
    public static void putBoolean(Context context,String key,boolean value){
        //存储节点文件名称
        if (sp==null){
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key,value).commit();

    }
    //读
    public static boolean getBoolean(Context context,String key,boolean defvalue){
        //存储节点文件名称
        if (sp==null){
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key,defvalue);

    }

    public static void putString(Context context,String key,String value){
        //存储节点文件名称
        if (sp==null){
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putString(key,value).commit();

    }
    //读
    public static int getInt(Context context,String key,int defvalue){
        //存储节点文件名称
        if (sp==null){
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getInt(key,defvalue);

    }

    public static void putInt(Context context,String key,int value){
        //存储节点文件名称
        if (sp==null){
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putInt(key,value).commit();

    }
    //读
    public static String getString(Context context,String key,String defvalue){
        //存储节点文件名称
        if (sp==null){
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getString(key,defvalue);

    }

    public static void remove(Context context, String key) {

        if (sp==null){
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().remove(key).commit();
    }
}
