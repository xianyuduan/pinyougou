package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeiXinPayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import utils.HttpClient;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class WeiXinPayServiceImpl implements WeiXinPayService {
    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String partner;
    @Value("${partnerkey}")
    private String partnerkey;

    //生成二维码
    @Override
    public Map createNative(String outTradeNo, String money) {
        Map<String, String> param = new HashMap<>();
        param.put("appid", appid);//公众号
        param.put("mch_id", partner);//商户号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        param.put("body", "品优购");//商品描述
        param.put("out_trade_no", outTradeNo);//商户订单号
        param.put("total_fee",money);//总金额（分）
        param.put("spbill_create_ip", "127.0.0.1");//IP
        param.put("notify_url", "http://test.itcast.cn");//回调地址(随便写)
        param.put("trade_type", "NATIVE");//交易类型
        try {
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(WXPayUtil.generateSignedXml(param,partnerkey));
            httpClient.post();
            String content = httpClient.getContent();
            return WXPayUtil.xmlToMap(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap();
    }

    //查询支付结果
    @Override
    public Map queryPayResult(String outTradeNo) {
        Map resultMap = new HashMap<>();
        resultMap.put("success", false);
        //1.使用map组装查询参数
        Map<String, String> param = new HashMap<>();
        param.put("appid", appid);//公众账号ID
        param.put("mch_id", partner);//商户号
        param.put("out_trade_no", outTradeNo);//订单号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
        client.setHttps(true);
        int num=0;
        try {
            while (true) {
                //查询100次，还未支付，则失败
                if (num >= 100) {
                    return resultMap;
                }
                num++;
                Thread.sleep(3000);
                client.setXmlParam(WXPayUtil.generateSignedXml(param,partnerkey));
                client.post();
                //后去查询结果
                String content = client.getContent();
                if (content == null) {
                    continue;
                }
                //查询结果转map
                Map<String, String> map = WXPayUtil.xmlToMap(content);
                String tradeState = map.get("trade_state");
                if ("SUCCESS".equals(tradeState)) {
                    resultMap.put("success", true);
                    resultMap.put("transactionId", map.get("transaction_id"));
                    return resultMap;
                } else if ("NOTPAY|USERPAYING".contains(tradeState)) {
                    continue;
                } else {
                    resultMap.put("success", false);
                    return resultMap;
                }
            }
        } catch (Exception e) {
            return resultMap;
        }
    }
}
