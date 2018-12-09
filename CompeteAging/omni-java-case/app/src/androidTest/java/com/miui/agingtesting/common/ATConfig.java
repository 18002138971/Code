package com.miui.agingtesting.common;

import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by Dell on 2017/5/18.
 */

public class ATConfig {
    public final static List<String> XIAOMI = Arrays.asList("whyred","ugglite","riva","nikel","tiffany","sagit","mido","jason","chiron","rosy","vince","dipper","polaris","SKR-A0","clover");
    public final static List<String> OPPO = Arrays.asList("R9sk","R11","R9s","R11st");
    public final static List<String> HUAWEI = Arrays.asList("MHA-AL00","SLA-AL00","ALP-AL00","KNT-AL10","BKL-AL20");
    public final static List<String> VIVO = Arrays.asList("PD1708");
    public static final int CAMERA_LOOP = 20;
    public static final int BROWSER_LOOP = 5;
    public static final int GALLERY_LOOP = 20;
    public static final int MARKET_LOOP = 5;
    public static final int NOTES_LOOP = 5;
    public static final int MESSAGE_LOOP = 25;
    public static final int CONTACT_LOOP=30;
    public static final int RECORDER_LOOP=5;
    public static final int MUSIC_LOOP=5;
    public static final int LAUNCH_TIME = 20000;
    public static final int SIMPLE_OPERATION_TIME = 5000;
    public static final int INSTALL_TIME =40000 ;
    public static final int RECORDER_TIME = 5000;
    public static final int MUSIC_TIME =30000 ;
    public static final String PHONENUMBER[] ={"10086","10010"} ;
    public static final int MESSAGELENGTH =20;
    public static final int CONTACTNAMELENGTH =5;
    public static final boolean state[]={true,false};//true为收，false为发
    public static final int MESSAGE_COUNT = 2000; //预置短信条数
    public static final int CONTACT_COUNT = 500; //预置手机联系人数


    public static final int DESKTOP_LOOP = 10;
    public static final int CONTACTSEARCH_LOOP = 10;
    public static final int DIALING_LOOP = 10;
    public static final int VIEWMESSAGE_LOOP = 10;
    public static final int DUOKAN_LOOP = 5;
    public static final int TAOBAO_LOOP = 5;
    public static final int BROWSERVIEW_LOOP = 5;
    public static final int MUSICPLAY_LOOP = 5;
    public static final int WEATHER_LOOP = 10;
    public static final int APPMARKET_LOOP = 5;
    public static final int CAMERAUSE_LOOP = 5;
    public static final int KINGHONER_LOOP = 5;
    public static final int WECHAT_LOOP = 10;
    public static final int ALIPAY_LOOP = 5;
    public static final int SINA_LOOP = 5;
    public static final int ANAP_LOOP = 5;
    public static final int APP_INSTALL_NUM = 2; //这个数量为平均分配到每次循环中的安装个数
    public static final int APP_UNINSTALL_INTERVAL = 1*60*60*1000; //删除最近1个小时安装的应用



}
