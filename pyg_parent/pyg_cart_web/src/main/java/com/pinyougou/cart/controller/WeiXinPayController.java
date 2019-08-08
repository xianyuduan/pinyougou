package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.cart.service.OrderService;
import com.pinyougou.pay.service.WeiXinPayService;
import com.pinyougou.pojo.TbPayLog;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class WeiXinPayController {
    @Reference
    private OrderService orderService;
    @Reference
    private WeiXinPayService weiXinPayService;

    //生成二维码
    @RequestMapping("/createNative")
    public Map createNative() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //查询订单
        TbPayLog payLog=orderService.findPayLogByUsername(username);
        if (payLog != null) {
            return weiXinPayService.createNative(payLog.getOutTradeNo(), "1");
        } else {
            return new HashMap();
        }
    }

    //查询支付结果
    @RequestMapping("/queryPayResult")
    public Map queryPayResult() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //查询订单
        TbPayLog payLog=orderService.findPayLogByUsername(username);
        if (payLog != null) {
            //查询支付结果
            Map map =weiXinPayService.queryPayResult(payLog.getOutTradeNo());
            //更新订单
            orderService.updatePayStataus(payLog.getOutTradeNo(), (String) map.get("transactionId"));
            return map;
        } else {
            return new HashMap();
        }
    }
}
