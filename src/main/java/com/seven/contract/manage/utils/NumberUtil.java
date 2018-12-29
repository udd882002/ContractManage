package com.seven.contract.manage.utils;

/**
 * Created by apple on 2018/12/15.
 */
public class NumberUtil {

    /**
     * //生成6位随机数
     */
    public static int randomCode() {
        return (int) ((Math.random() * 9 + 1) * 100000);
    }

    public static boolean isNumeric (String str) {
        if (str == null) {
            return false;
        }
        for (int i = str.length(); --i >=0;) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println(isNumeric(null));
    }


}
