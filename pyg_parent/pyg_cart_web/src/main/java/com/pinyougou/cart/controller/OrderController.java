package com.pinyougou.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.cart.service.OrderService;
import com.pinyougou.pojo.TbOrder;
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
    public Result saveOrder(@RequestBody TbOrder order) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            order.setUserId(username);
            order.setSourceType("1");//设置订单来源,1=pc
            orderService.save(order);
            return new Result(true, "保存成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, "保存失败");
    }
}
