package com.pinyougou.cart.service;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;

public interface OrderService {

    void save(TbOrder order);

    TbPayLog findPayLogByUsername(String username);

    void updatePayStataus(String outTradeNo, String transactionId);
}
