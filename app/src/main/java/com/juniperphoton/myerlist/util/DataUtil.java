package com.juniperphoton.myerlist.util;


public class DataUtil {
    public static boolean isStringNullOrEmpty(String str) {
        if (str.equals("") || str == null) {
            return true;
        } else return false;
    }

    public static boolean isEmailFormat(String emailStr) {
        return java.util.regex.Pattern.matches("\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*", emailStr);
    }
}
