package com.work.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * ClassName:PayKuaiQianController
 * Package:com.work.controller
 * Description: 接收块钱返回的结果
 *
 * @date:2023/4/30 12:11
 * @author:yueyue
 */
@Controller
public class PayKuaiQianController {
    // 接受快钱的异步通知
    @GetMapping("/kq/notify")
    public void receiveKQNotify(HttpServletResponse response) throws IOException {
        System.out.println("========receiveKQNotify=========");
        PrintWriter out = response.getWriter();
        out.println("<result>1</result><redirecturl>http://www.baidu.com</redirecturl>");
        out.flush();
        out.close();
    }
}
