package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.OrderService;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.pojo.*;
import com.pinyougou.vo.Cart;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import utils.IdWorker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private TbOrderItemMapper orderItemMapper;
    @Autowired
    private TbOrderMapper orderMapper;
    @Autowired
    private TbPayLogMapper payLogMapper;

    //新增订单
    @Override
    public void save(TbOrder order) {
        //1.从redis中根据用户名查询该用户的购物车数据
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());

        double totalFees = 0;//记录所有订单金额的总ong和
        List<Long> orderIds = new ArrayList<>();
        for(Cart cart : cartList){
            //2.循环购物车数据，为每一个购物车对象创建对应的订单对象
            TbOrder tborder = new TbOrder();
            tborder.setOrderId(idWorker.nextId());

            tborder.setUserId(order.getUserId());//用户名
            orderIds.add(tborder.getOrderId());//记录订单号
            tborder.setPaymentType(order.getPaymentType());//支付类型
            tborder.setStatus("1");//状态：未付款
            tborder.setCreateTime(new Date());//订单创建日期
            tborder.setUpdateTime(new Date());//订单更新日期
            tborder.setReceiverAreaName(order.getReceiverAreaName());//地址
            tborder.setReceiverMobile(order.getReceiverMobile());//手机号
            tborder.setReceiver(order.getReceiver());//收货人
            tborder.setSourceType(order.getSourceType());//订单来源
            tborder.setSellerId(cart.getSellerId());//商家ID
            //循环购物车明细
            double money=0;
            for(TbOrderItem orderItem :cart.getOrderItemList()){
                orderItem.setId(idWorker.nextId());
                orderItem.setOrderId( tborder.getOrderId()  );//订单ID
                orderItem.setSellerId(cart.getSellerId());
                money+=orderItem.getTotalFee().doubleValue();//金额累加
                orderItemMapper.insert(orderItem);
            }
            totalFees += money;
            tborder.setPayment(new BigDecimal(money));
            orderMapper.insert(tborder);
        }
        //创建支付日志对象，保存
        if("1".equals(order.getPaymentType())){
            TbPayLog payLog = new TbPayLog();
            payLog.setOutTradeNo(String.valueOf(idWorker.nextId()));
            payLog.setTotalFee((long)(totalFees*100));//总金额是使用分为单位保存
            payLog.setOrderList(StringUtils.join(orderIds, ","));
            payLog.setCreateTime(new Date());//创建时间
            payLog.setPayType("1");//支付类型
            payLog.setTradeState("0");//支付状态
            payLog.setUserId(order.getUserId());//用户ID
            payLogMapper.insert(payLog);//插入到支付日志表
            //在redis中保存支付之日
            redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);
        }

        //4.将redis中该用户的购物车数据删除
        redisTemplate.boundHashOps("cartList").delete(order.getUserId());

    }

    @Override
    public TbPayLog findPayLogByUsername(String username) {
        return (TbPayLog) redisTemplate.boundHashOps("payLog").get(username);
    }

    //更新付款后的订单
    @Override
    public void updatePayStataus(String outTradeNo, String transactionId) {
        TbPayLog payLog = payLogMapper.selectByPrimaryKey(outTradeNo);
        //更新日志
        payLog.setPayTime(new Date());
        payLog.setTradeState("1");
        payLog.setTransactionId(transactionId);
        payLogMapper.updateByPrimaryKey(payLog);
        //跟新订单
        String[] orderIds = payLog.getOrderList().split(",");
        for (String orderId : orderIds) {
            TbOrder tbOrder = orderMapper.selectByPrimaryKey(Long.valueOf(orderId));
            tbOrder.setStatus("2");//支付成功
            orderMapper.updateByPrimaryKey(tbOrder);
        }
        //删除日志缓存
        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
    }
}
