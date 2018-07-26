package com.example.liang.mobilesafe74.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AddressDao {
    //1.指定访问数据库的路径
    public static String path="data/data/com.example.liang.mobilesafe74/files/address.db";
    //2.传递一个电话号码，开启数据库连接，进行访问，返回一个归属地(只读的形式打开)
    public static void getAddress(String phone){
        phone = phone.substring(0, 7);
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("data1", new String[]{"outkey"}, "id=?",
                new String[]{phone}, null, null, null);
        //4.查到即可
        if (cursor.moveToNext()){
            String outkey = cursor.getString(0);
            //5.通过data1查询到的结果，作为外键查询data2
            Cursor indexCursor = db.query("data2", new String[]{"location"}, "id=?", new String[]{outkey}, null, null, null);
            if (indexCursor.moveToNext()){
                //获取查询到的电话归属地
                String address = indexCursor.getString(0);

            }
        }
    }
}
