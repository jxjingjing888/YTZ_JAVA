package com.ytz.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class MobileUtil {

    // 验证手机号
    private static Pattern pattern = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");

    public static boolean isMobile(String str) {
        boolean b = false;
        if (StringUtils.isNotBlank(str)) {
            Matcher m = pattern.matcher(str);
            b = m.matches();
        }
        return b;
    }

    public static String mobileEncrypt(String phone) {
        if (isMobile(phone)) {
            return phone.substring(0, 3) + "****" + phone.substring(7, phone.length());
        }
        return phone;
    }

    public static void main(String[] args) {
        System.out.println(mobileEncrypt("15986755827"));
    }

}
