package com.pinyougou.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.cart.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.vo.Cart;
import com.pinyougou.vo.UpOrder;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    //新增订单
    @RequestMapping("/saveOrder")
    public Result saveOrder(@RequestBody UpOrder upOrder) {
        TbOrder order = upOrder.getTbOrder();
        List<Cart> cartList = upOrder.getCartList();
        if(order==null || cartList==null){
            return new Result(true,"增加失败");
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        order.setUserId(username);
        order.setSourceType("2");//订单来源  PC
        try {
            orderService.add(order,cartList);
            return new Result(true,"增加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(true,"增加失败");
        }
    }
}
