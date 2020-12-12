package com.example.http;

import com.alibaba.fastjson.JSONObject;
import com.example.http.okhttp.ReqClientUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program:http
 * @author: zhengjunjie
 * @Time: 2020/12/10  18:40
 */
@RestController
public class controller {

    @GetMapping("/hi")
    public Object getww() {
        String s = ReqClientUtil.callGetForString("http://www.baidu.com");
        System.out.println(s);
        return  s;
    }
}
