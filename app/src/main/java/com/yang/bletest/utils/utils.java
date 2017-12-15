package com.yang.bletest.utils;

/**
 * Created by Yang_Mstarc on 2017/8/24.
 */

public class utils {

    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static byte[] shortToByte(short number){
        int temp = number;
        byte[] b = new byte[2];
        for(int i =0; i < b.length; i++){
            b[i]=new Integer(temp &0xff).byteValue();
            temp = temp >>8;
        }
        return b;
    }

    public static short byteToShort(byte[] b){
        short s =0;
        short s0 =(short)(b[0]&0xff);
        short s1 =(short)(b[1]&0xff);
        s1 <<=8;
        s =(short)(s0 | s1);
        return s;
    }

    public static byte[] intToByte(int number){
        int temp = number;
        byte[] b =new byte[4];
        for(int i =0; i < b.length; i++){
            b[i]=new Integer(temp &0xff).byteValue();
            temp = temp >>8;
        }
        return b;
    }

    public static int byteToInt(byte[] b){
        int s =0;
        int s0 = b[0]&0xff;
        int s1 = b[1]&0xff;
        int s2 = b[2]&0xff;
        int s3 = b[3]&0xff;
        s3 <<=24;
        s2 <<=16;
        s1 <<=8;
        s = s0 | s1 | s2 | s3;
        return s;
    }



}
