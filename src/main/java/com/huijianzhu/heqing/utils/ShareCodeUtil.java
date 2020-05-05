package com.huijianzhu.heqing.utils;

import cn.hutool.core.util.IdUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ================================================================
 * 说明：当前类说说明
 * <p>
 * 作者          时间                    注释
 * 刘梓江	2020/5/2  15:03            创建
 * =================================================================
 **/
public class ShareCodeUtil {

    /** 自定义进制(0,1没有加入,容易与o,l混淆) */
    private static final char[] r=new char[]{'q', 'w', 'e', '8', 'a', 's', '2', 'd', 'z', 'x', '9', 'c', '7', 'p', '5', 'i', 'k', '3', 'm', 'j', 'u', 'f', 'r', '4', 'v', 'y', 'l', 't', 'n', '6', 'b', 'g', 'h'};

    /** (不能与自定义进制有重复) */
    private static final char b='o';

    /** 进制长度 */
    private static final int binLen=r.length;

    /** 序列最小长度 */
    private static final int s=6;

    /**
     * 根据ID生成八位随机码
     * @param id ID
     * @return 随机码
     */
    public static String toSerialCode(long id) {
        char[] buf=new char[32];
        int charPos=32;

        while((id / binLen) > 0) {
            int ind=(int)(id % binLen);
            // System.out.println(num + "-->" + ind);
            buf[--charPos]=r[ind];
            id /= binLen;
        }
        buf[--charPos]=r[(int)(id % binLen)];
        // System.out.println(num + "-->" + num % binLen);
        String str=new String(buf, charPos, (32 - charPos));
        // 不够长度的自动随机补全
        if(str.length() < s) {
            StringBuilder sb=new StringBuilder();
            sb.append(b);
            Random rnd=new Random();
            for(int i=1; i < s - str.length(); i++) {
                sb.append(r[rnd.nextInt(binLen)]);
            }
            str+=sb.toString();
        }
        return str;
    }

    //使用邀请码转换成对应的用户id
    public static long codeToId(String code) {
        char chs[]=code.toCharArray();
        long res=0L;
        for(int i=0; i < chs.length; i++) {
            int ind=0;
            for(int j=0; j < binLen; j++) {
                if(chs[i] == r[j]) {
                    ind=j;
                    break;
                }
            }
            if(chs[i] == b) {
                break;
            }
            if(i > 0) {
                res=res * binLen + ind;
            } else {
                res=ind;
            }
            // System.out.println(ind + "-->" + res);
        }
        return res;
    }


    /**
     * 获取唯一登陆标识
     * @return
     */
    public static String getUserToken(){
        //创建一个用户唯一登陆标识
        String LOGIN_TOKEN= IdUtil.simpleUUID();
        return LOGIN_TOKEN;
    }

    /**
     * 返回长度为【strLength】的随机数，在前面补0
     * @param strLength 长度
     * @return 随机数
     */
    public static String getFixLengthString(int strLength) {
        Random rm = new Random();
        // 获得随机数
        double pross = (1 + rm.nextDouble()) * Math.pow(10, strLength);
        // 将获得的获得随机数转化为字符串
        String fixLengthString = String.valueOf(pross);
        // 返回固定的长度的随机数
        return fixLengthString.substring(1, strLength + 1);
    }

    //生成随机用户名，数字和字母组成,
    public static  String getStringRandom(int length) {

        String val = "";
        Random random = new Random();

        //参数length，表示生成几位随机数
        for(int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if( "char".equalsIgnoreCase(charOrNum) ) {
                //输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char)(random.nextInt(26) + temp);
            } else if( "num".equalsIgnoreCase(charOrNum) ) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }



    public static void main(String[] args) {
        List<String> account=new ArrayList<>();
        account.add("157170075490");
        account.add("157170075490");
        account.add("464562342341");
        account.add("565780986543");
        account.add("213458675432");
        account.add("563424564353");
        account.add("875643256543");
        account.add("324532156435");
        account.add("324535643245");
        account.add("195234345656");

        account.forEach(e->{
            System.out.print((Long.parseLong(e)));
            System.out.print("========"+codeToId(toSerialCode(Long.parseLong(e))));
            System.out.print("========"+toSerialCode(Long.parseLong(e)));
            System.out.println();
        });
    }
}
    
    
    