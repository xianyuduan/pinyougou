package com.pinyougou.sms;

import entity.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.SMSUtils;

@RestController
@RequestMapping("/sms")
public class SmsController {
    @RequestMapping("/sendSms")
    public Result sendSms(String phoneNum, String templateParam,String signName, String templateCode) {
        boolean result = SMSUtils.sendSms(phoneNum, templateParam, signName, templateCode);
        return new Result(result, result ? "信息发送成功" : "信息发送失败");
    }
}
