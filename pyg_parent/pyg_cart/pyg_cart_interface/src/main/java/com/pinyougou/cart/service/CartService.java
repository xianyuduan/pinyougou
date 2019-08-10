package com.pinyougou.cart.service;

import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.vo.Cart;

import java.util.List;

public interface CartService {
    List<Cart> findCartListByKey(String key);

    List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, int num);

    void saveCartListToRedis(String key, List<Cart> cartList);

    List<Cart> mergeList(List<Cart> cartListByUsername, List<Cart> cartListByKey);

    void deleteListByKey(String cartListByKey);

    void saveupOrderItem(List<TbOrderItem> cartListUp, String sessionId);

    List<TbOrderItem> findUpdataCartList(String session);
}
