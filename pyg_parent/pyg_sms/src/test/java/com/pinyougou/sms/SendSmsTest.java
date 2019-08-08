package com.pinyougou.sms;


import org.apache.commons.lang.RandomStringUtils;
import utils.HttpClient;

import java.io.IOException;
import java.text.ParseException;

//测试发送消息
public class SendSmsTest {
    public static void main(String[] args) throws IOException, ParseException {
        HttpClient httpClient = new HttpClient("http://localhost:7788/sms/sendSms");
        httpClient.addParameter("phoneNum","18672556605");
        String random = RandomStringUtils.randomNumeric(4);
        System.out.println(random);
        httpClient.addParameter("templateParam","{'code':'1234'}");
        httpClient.addParameter("signName","品优购");
        httpClient.addParameter("templateCode","SMS_171540858");
        httpClient.post();
        String content = httpClient.getContent();
        System.out.println(content);
    }
}
