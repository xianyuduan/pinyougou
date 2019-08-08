package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.vo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CartServiceImpl implements CartService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbItemMapper itemMapper;

    /**
     * 根据key在Redis中查找购物车
     */
    @Override
    public List<Cart> findCartListByKey(String key) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(key);
        //若是Redis没有,新建一个
        if (cartList == null) {
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    /**
     * 在购物车中按id查找商家
     */
    private Cart findCartBySellerId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if (sellerId.equals(cart.getSellerId())) {
                return cart;
            }
        }
        return null;
    }

    /**
     * 查询改商家商品列表中是否已经有该商品
     */
    private TbOrderItem findOrderItemByItemId(List<TbOrderItem> orderItemList, long id) {
        for (TbOrderItem orderItem : orderItemList) {
            if (id == orderItem.getItemId()) {
                return orderItem;
            }
        }
        return null;
    }

    /**
     *根据商家商品item创建购物车商品orderItem
     */
    private TbOrderItem createOrderItem(TbItem item, int num) {
        if (num <= 0) {
            throw new RuntimeException("数量不合法");
        }
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
        return orderItem;
    }


    /**
     *将货物添加到购物车中
     */
    @Override
    public List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, int num) {
        //先去数据库查询该货物在仓库的库存情况
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        //若不存在该货物,抛出异常
        if (item == null) {
            throw new RuntimeException("货物不存在,请选择其他货物");
        }
        //若货物已下架,抛出异常
        if (!"1".equals(item.getStatus())) {
            throw new RuntimeException("货物已下架,请选择其他货物");
        }
        //从购物车中查询是否已经有该商家商品列表
        Cart cart=findCartBySellerId(cartList, item.getSellerId());
        //购物车中已有该商家商品
        if (cart != null) {
            //查询改商家商品列表中是否已经有该商品
            TbOrderItem orderItem=findOrderItemByItemId(cart.getOrderItemList(), item.getId());
            if (orderItem != null) {
                //已经有该商品,修改数量和总价
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));
                //如果数量减为0,删掉商品
                if (orderItem.getNum() <= 0) {
                    cart.getOrderItemList().remove(orderItem);
                }
                //如果没有该商家商品,删除商家
                if (cart.getOrderItemList().size() <= 0) {
                    cartList.remove(cart);
                }
            } else {//有商家,没该商品
                //根据商家商品创建购物车商品
                orderItem=createOrderItem(item, num);
                cart.getOrderItemList().add(orderItem);
            }
        } else {//购物车中没有该商家商品
            cart = new Cart();
            cart.setSellerId(item.getSellerId());
            cart.setSellerName(item.getSeller());
            List<TbOrderItem> orderItemList = new ArrayList<>();
            orderItemList.add(createOrderItem(item, num));
            cart.setOrderItemList(orderItemList);
            cartList.add(cart);
        }
        return cartList;
    }



    /**
     * 将购物车保存到Redis中
     */
    @Override
    public void saveCartListToRedis(String key, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(key,cartList);
    }

    //合并集合
    @Override
    public List<Cart> mergeList(List<Cart> cartListByUsername, List<Cart> cartListByKey) {
        if (cartListByUsername == null || cartListByUsername.size() <= 0) {
            return cartListByKey;
        }
        for (Cart cart : cartListByKey) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                addItemToCartList(cartListByUsername, orderItem.getItemId(), orderItem.getNum());
            }
        }
        return cartListByUsername;
    }

    //按key删除Redis中数据
    @Override
    public void deleteListByKey(String cartListByKey) {
        redisTemplate.boundHashOps("cartList").delete(cartListByKey);
    }


}
