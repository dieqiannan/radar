package com.dqn.radar;

import android.util.Log;



/**
 * 日志打印
 */
public class MyLogUtils {

    public static boolean isPrint = true;

    public static void e(String tag, Object s){
        if(isPrint){
            Log.e(tag,s.toString());
        }
    }

}
