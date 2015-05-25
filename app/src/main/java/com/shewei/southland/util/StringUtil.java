package com.shewei.southland.util;

/**
 * Created by xiaobing on 15-5-25.
 */
public class StringUtil {
    public static boolean isStrEmpty(String str){
        if (str == null || str.equals("")){
            return true;
        }
        return false;
    }
}
