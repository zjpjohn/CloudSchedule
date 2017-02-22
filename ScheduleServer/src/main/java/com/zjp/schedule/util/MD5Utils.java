package com.zjp.schedule.util;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

/**
 * Project: AlertSystem
 * Module Desc:com.juntu.alert.utils
 * User: zjprevenge
 * Date: 2016/12/7
 * Time: 16:01
 */
public class MD5Utils {

    /**
     * md5加密
     *
     * @param source 待加密的字符串
     * @return
     */
    public static String md5(String source) {
        return Hashing.md5().newHasher().putString(source, Charsets.UTF_8).hash().toString();
    }

}
