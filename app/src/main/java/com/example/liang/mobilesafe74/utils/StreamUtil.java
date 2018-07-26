package com.example.liang.mobilesafe74.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {

    /*
    返回流转换成的字符串
     */
    public static String streamToString(InputStream is){
        //读取过程中，将读取的内容存储值缓存，然后一次性的转换成字符串返回
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //读流，读到没有
        byte [] buffer=new byte[1024];
        int temp=-1;
        try {
            while ((temp=is.read(buffer))!=-1){

                bos.write(buffer,0,temp);
            }
            //返回读取的数据
            return bos.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                bos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
