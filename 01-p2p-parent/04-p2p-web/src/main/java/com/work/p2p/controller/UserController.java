package com.work.p2p.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.work.cons.Constants;
import com.work.p2p.beans.user.FinanceAccount;
import com.work.p2p.beans.user.User;
import com.work.p2p.services.user.FinanceAccountService;
import com.work.p2p.services.user.RedisService;
import com.work.p2p.services.user.UserService;
import com.work.utils.Result;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * ClassName:UserController
 * Package:com.work.p2p.controller
 * Description: 描述信息
 *
 * @date:2023/4/18 14:08
 * @author:yueyue
 */
@Controller
public class UserController {

    @Reference(interfaceClass = UserService.class,version = "1.0.0",check = false,timeout = 15000)
    UserService userService;

    @Reference(interfaceClass = RedisService.class,version = "1.0.0",check = false,timeout = 15000)
    RedisService redisService;

    @Reference(interfaceClass = FinanceAccountService.class,version = "1.0.0",check=false,timeout = 15000)
    FinanceAccountService financeAccountService;

    /**跳转到注册界面*/
    @RequestMapping("/loan/page/register")
    public String toRegister() {
        return "/register";
    }
    /**查看数据库检测手机号是否已被注册*/
    @RequestMapping("/loan/checkPhone")
    @ResponseBody
    public Result checkPhone(@RequestParam("phone") String phone){
        // 根据手机号查询数据库 返回值类型===>User
        // 判断返回的User对象是否为空
        User user = userService.queryUserByPhone(phone);
        // 如果为空，则说明手机号可用
        if(!ObjectUtils.allNotNull(user)){
          return Result.success();
        }
        // 如果不为空，则说明手机号不可用
        else {
            return Result.fail("手机号: "+phone+"已经被注册！");
        }
    }
    /**注册*/
    @RequestMapping("/loan/register")
    @ResponseBody
    public Result register(HttpServletRequest request,
                           @RequestParam("phone") String phone,
                           @RequestParam("loginPassword") String loginPassword,
                           @RequestParam("messageCode") String messageCode
                            ){
        // 1. 往数据库中添加一条User信息，和账户信息
        // 2. 用户注册后，将User对象保存到Session中，相当于直接登录
        try{
            // 判断验证码是否正确
            String redisCode = redisService.get(phone);
            if (!StringUtils.equals(redisCode,messageCode)) {
                return Result.codeFail("验证码错误");
            }
            // 往数据库中添加一条User信息，和账户信息
            User user = userService.register(phone, loginPassword);
//            // 用户注册后，将User对象保存到Session中，相当于直接登录
            request.getSession().setAttribute(Constants.SESSION_USER, user);

        }catch (Exception e){
            return Result.fail("注册失败！");
        }
        return Result.success();
    }
    //工具类：生成n位随机整数
    private String getRandNum(int n) {
        Random random= new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < n; i++) {
            int radnNum = random.nextInt(9);
            sb.append(radnNum);
        }
        return sb.toString();
    }
    /**通过第三方接口发送短信验证码*/
    @RequestMapping("/user/messageCode")
    @ResponseBody
    public Result sendMessageCode(@RequestParam("phone")String phone) throws DocumentException {
        // 通过第三方接口发送短信验证码
        String randomNum = getRandNum(6);
        String url = "https://way.jd.com/kaixintong/kaixintong";
        Map<String, String> params = new HashMap<String, String>();
        params.put("appkey","你的appkey");
        params.put("mobile",phone);
        params.put("content","【凯信通】您的验证码是：" + randomNum);
        //String jsonStr = HttpClientUtils.doGet(url,params);

        //先用假数据测试一下
        String jsonStr = "{\n" +
                "\"code\": \"10000\",\n" +
                "\"charge\": false,\n" +
                "\"remain\": 0,\n" +
                "\"msg\": \"查询成功\",\n" +
                "\"result\": \"<?xml version=\\\"1.0\\\" encoding=\\\"utf-8\\\" ?><returnsms>\\n <returnstatus>Success</returnstatus>\\n <message>ok</message>\\n <remainpoint>-6850193</remainpoint>\\n <taskID>162317289</taskID>\\n <successCounts>1</successCounts></returnsms>\"\n" +
                "}";
        //解析json数据 JSONObject.parseObject
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        String code = jsonObject.getString("code");
        if (!StringUtils.equals(code,"10000")) {
            return Result.fail("验证码发送错误");
        }

        String resultXml = jsonObject.getString("result");
        Document document = DocumentHelper.parseText(resultXml);
        Node node = document.selectSingleNode("/returnsms/returnstatus");
        String returnstatus = node.getText();
        if (!StringUtils.equals(returnstatus,"Success")) {
            return Result.fail("验证码发送失败");
        }

        // 验证发送成功,保存验证码(将验证码保存到Redis中)
        redisService.put(phone, randomNum);

        return Result.codeSuccess(randomNum);
    }
    /**跳转到实名认证界面*/
    @RequestMapping("/loan/page/realName")
    public String toRealName(){
        //跳转到实名认证界面
        return "/realName";
    }

    /**实名认证+第三方发送验证码*/
    @RequestMapping("/loan/realName")
    @ResponseBody
    public Result realName(HttpServletRequest request,
                           @RequestParam("phone") String phone,
                           @RequestParam("realName") String realName,
                           @RequestParam("idCard") String idCard,
                           @RequestParam("messageCode") String messageCode
                           ) {
        try {
            String redisCode = redisService.get(phone);
            if(!StringUtils.equals(redisCode,messageCode)){
                return Result.codeFail("验证码错误");
            }
            String url = "https://way.jd.com/youhuoBeijing/test";
            Map<String, String> params = new HashMap<String, String>();
            params.put("appkey","你的appkey");
            params.put("cardNo",idCard);
            params.put("realName",realName);
            //String jsonStr = HttpClientUtils.doGet(url,params);
            //先用假数据测试一下
            String jsonStr = "{\n" +
                    "    \"code\": \"10000\",\n" +
                    "    \"charge\": false,\n" +
                    "    \"remain\": 1305,\n" +
                    "    \"msg\": \"查询成功\",\n" +
                    "    \"result\": {\n" +
                    "        \"error_code\": 0,\n" +
                    "        \"reason\": \"成功\",\n" +
                    "        \"result\": {\n" +
                    "            \"realname\": \"乐天磊\",\n" +
                    "            \"idcard\": \"350721197702134399\",\n" +
                    "            \"isok\": true\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";
            //解析json数据 JSONObject.parseObject
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            String code = jsonObject.getString("code");
            if(!StringUtils.equals(code,"10000")){
                return Result.fail("实名认证失败");
            }

            String outerResult = jsonObject.getString("result");
            JSONObject jsonObject1 = JSONObject.parseObject(outerResult);
            /**
             * 由jsonStr知jsonObject1内容：
             *  result:{
             *         error_code: 0,
             *         reason: "成功",
             *         result: {
             *             realname: "乐天磊",
             *             idcard: "350721197702134399"
             *             ,
             *             isok: true
             *         }
             * */
            String innerResult = jsonObject1.getString("result");
            JSONObject jsonObject2 = JSONObject.parseObject(innerResult);
            Boolean isok = jsonObject2.getBoolean("isok");
            if(!isok){
                return Result.fail("实名认证失败");
            }
            //实名认证成功,更新user表
            User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
            user.setName(realName);
            user.setIdCard(idCard);
            int rows = userService.modifyUser(user);
            if(rows == 0){
                throw new Exception("实名认证失败");
            }
        }catch (Exception e){
            return Result.fail("实名认证失败");
        }

        return  Result.success();
    }
    /**根据uid 获取账户余额*/
    @RequestMapping("/loan/myFinanceAccount")
    @ResponseBody
    public Double getFinanceAccountByUid(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(user.getId());
        return financeAccount.getAvailableMoney();
    }

    /**退出登录*/
    @RequestMapping("/loan/logout")
    public String logout(HttpServletRequest request){
        //退出登录:删除session数据
        request.getSession().removeAttribute(Constants.SESSION_USER);
        //重定向到首页
        return "redirect:/index";
    }

    /**跳转到登录页面*/
    @RequestMapping("/loan/page/login")
    public String login(){
        return "/login";
    }
    /**登录认证*/
    @RequestMapping("/loan/login")
    @ResponseBody
    public Result toLogin(HttpServletRequest request,
                          @RequestParam("phone") String phone,
                          @RequestParam("loginPassword") String loginPassword,
                          @RequestParam("messageCode") String messageCode
                          ){
        try {
            String redisCode = redisService.get(phone);
            if(!StringUtils.equals(redisCode,messageCode)){
                return Result.codeFail("验证码有误");
            }
            User user = userService.queryUserByPhoneAndPwd(phone, loginPassword);

            if(ObjectUtils.allNotNull(user)){
                request.getSession().setAttribute(Constants.SESSION_USER,user);
            }
            else {
                return Result.fail("用户名或密码错误");
            }

        }catch (Exception e){
            e.printStackTrace();
            return Result.fail("未知异常，登陆失败！");
        }
        return Result.success();

    }
    /**跳转到个人中心界面*/
    @RequestMapping("/loan/myCenter")
    public String toMyCenter(HttpServletRequest request, Model model){
        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(user.getId());
        model.addAttribute("availableMoney",financeAccount.getAvailableMoney());
        return "/myCenter";
    }





}
