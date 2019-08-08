package com.pinyougou.pay.service;

import java.util.Map;

public interface WeiXinPayService {
    Map createNative(String outTradeNo, String s);

    Map queryPayResult(String outTradeNo);
}
