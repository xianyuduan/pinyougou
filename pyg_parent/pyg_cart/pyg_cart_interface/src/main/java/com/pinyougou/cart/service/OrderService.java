package com.pinyougou.cart.service;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.vo.Cart;

import java.util.List;

public interface OrderService {

    void save(TbOrder order);

    TbPayLog findPayLogByUsername(String username);

    void updatePayStataus(String outTradeNo, String transactionId);

    void add(TbOrder order, List<Cart> cartList);
}
