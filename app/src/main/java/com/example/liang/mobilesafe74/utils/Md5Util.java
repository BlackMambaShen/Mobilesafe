package com.example.liang.mobilesafe74.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {


    //psd需要加密的密码
    public static String encoder(String psd) {
        //指定加密的算法类型
        try {
            //加盐
            psd=psd+"mobilesafe";
            MessageDigest digest = MessageDigest.getInstance("MD5");
            //将需要加密的字符串转换成byte类型的数组，然后进行随机哈希过程
//            System.out.println(psd.getBytes().length+"");
            //16字节
            byte[] bs = digest.digest(psd.getBytes());
            System.out.println(bs.length);
            //循环遍历bs,然后让其生成32位字符串，固定写法
            //拼接字符串过程
            StringBuffer stringBuffer=new StringBuffer();
            for (byte b:bs){
                int i=b&0xff;
                String hexString = Integer.toHexString(i);
                System.out.println(hexString);
                if (hexString.length()<2){
                    hexString="0"+hexString;
                }
                stringBuffer.append(hexString);
            }
            //打印测试
            System.out.println(stringBuffer.toString());
            return stringBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
