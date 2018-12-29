package com.seven.contract.manage.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobileUtils {

	/**
	 * 验证手机号
	 *
	 * @param mobiles 手机号码
	 * @return 有效返回true, 否则返回false
	 */
	public static boolean isMobileNO(String mobiles) {

		Pattern p = Pattern.compile("^((13[0-9])|(15[^4])|(18[0-9])|(17[0-9])|(147))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

}