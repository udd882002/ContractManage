package com.seven.contract.manage.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;

/**
 * 图片base64互转
 * Created by yangyan on 2015/8/11.
 */
public class Base64ToImageUtils {


    public static void main(String[] args) {
        try {
            String base64Str = FileUtils.readFile("/Users/apple/Desktop/Echonessy.txt");
            System.out.println(base64Str);
            String str = base64Str.substring(base64Str.indexOf(",")+1);
            System.out.println(str);
            Base64ToImageUtils.GenerateImage(str, "/Users/apple/Desktop/Echonessy.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

//        String base64 = Base64ToImageUtils.GetImageStr("/Users/apple/Desktop/最新合同文本_1.jpg");
//        System.out.println(base64);
    }
    //图片转化成base64字符串
    public static String GetImageStr(String imgFile) {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        //对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);//返回Base64编码过的字节数组字符串
    }

    //base64字符串转化成图片
    public static boolean GenerateImage(String base64Str, String imgFilePath) {   //对字节数组字符串进行Base64解码并生成图片
        if (base64Str == null) {
            //图像数据为空
            return false;
        }


        if (imgFilePath == null || imgFilePath.equals("")) {
            //图片地址
            return false;
        }

        String suffix = imgFilePath.substring(imgFilePath.lastIndexOf("."));
        if (!suffix.equals(".jpg") && !suffix.equals(".jpeg")) {
            return false;
        }

        BASE64Decoder decoder = new BASE64Decoder();
        try {
            //Base64解码
            byte[] b = decoder.decodeBuffer(base64Str);
            for(int i=0;i<b.length;++i) {
                if(b[i]<0) {//调整异常数据
                    b[i]+=256;
                }
            }
            //生成jpeg图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}