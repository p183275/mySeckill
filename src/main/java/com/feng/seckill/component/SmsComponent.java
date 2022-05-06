package com.feng.seckill.component;

import com.feng.seckill.util.HttpUtils;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : pcf
 * @date : 2022/3/25 17:56
 */
@Data
@Component
public class SmsComponent {

    @Value("${spring.alicloud.sms.host}")
    private String host;
    @Value("${spring.alicloud.sms.path}")
    private String path;
    @Value("${spring.alicloud.sms.method}")
    private String method;
    @Value("${spring.alicloud.sms.appcode}")
    private String appcode;
    @Value("${spring.alicloud.sms.template}")
    private String template;

    public void sendSmsCode(String phoneNumber, String code, String expire){
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();

        bodys.put("content", "code:"+code+",expire_at:" + expire);
        bodys.put("phone_number", phoneNumber);
        bodys.put("template_id", template);

        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
