package com.work.utils;



import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

/**
 * ClassName:DateUtil
 * Package:com.work.utils
 * Description: 日期工具类
 * @date:2023/04/29 22:22
 * @author:yueyue
 */
public class DateUtil {

    // 生成订单号
    //注意写成public static静态方法以便直接调用
    public static String generateOrderId() {
        return "KQ" + DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS");
    }

}
