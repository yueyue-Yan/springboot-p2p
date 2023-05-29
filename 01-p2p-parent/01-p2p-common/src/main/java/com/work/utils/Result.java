package com.work.utils;

import java.util.HashMap;

/**
 * ClassName:Result
 * Package:com.work.cons
 * Description: 描述信息
 *
 * @date:2023/4/23 15:13
 * @author:yueyue
 */
public class Result extends HashMap<String, Object> {
    /**
     * 返回成功的json
     * @return
     */
    public static Result success() {
        Result result = new Result();
        result.put("code", 1);
        result.put("message", "");
        result.put("success", true);
        return result;
    }

    /**
     * 返回成功的json(带验证码)
     * @return
     */
    public static Result codeSuccess(String messageCode) {
        Result result = new Result();
        result.put("code", 1);
        result.put("message", "");
        result.put("messageCode",messageCode);
        result.put("success", true);
        return result;
    }

    /**
     * 返回失败的json
     * @return
     */
    public static Result fail(String msg) {
        Result result = new Result();
        result.put("code", -1);
        result.put("message", msg);
        result.put("success", false);
        return result;
    }


    /**
     * 返回验证码错误的json
     * @return
     */
    public static Result codeFail(String msg) {
        Result result = new Result();
        result.put("code", -2);
        result.put("message", msg);
        result.put("success", false);
        return result;
    }


}
