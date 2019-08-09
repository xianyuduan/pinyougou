package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.OrderService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.pojo.*;
import com.pinyougou.vo.Cart;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
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
    @Autowired
    private TbItemMapper itemMapper;

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

    @Override
    public void add(TbOrder order, List<Cart> upCartList) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());
        //订单日志属性值初始化
        List<String> orderList=new ArrayList<>();
        double totalFee=0;
        //循环购物车
        for (Cart cart : upCartList) {
            //每个商家储存一个订单 ，order
            long orderId = idWorker.nextId();
            TbOrder tbOrder = new TbOrder();
            tbOrder.setOrderId(orderId);//订单id
            tbOrder.setUserId(order.getUserId());//用户名
            tbOrder.setPaymentType(order.getPaymentType());//支付类型
            tbOrder.setStatus("1");//1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭,7、待评价
            tbOrder.setCreateTime(new Date());//订单创建时间
            tbOrder.setUpdateTime(new Date());//订单更新时间
            tbOrder.setReceiverAreaName(order.getReceiverAreaName());//地址
            tbOrder.setReceiverMobile(order.getReceiverMobile());//手机号
            tbOrder.setReceiver(order.getReceiver());//收货人
            tbOrder.setSourceType(order.getSourceType());//订单来源
            tbOrder.setSellerId(cart.getSellerId());
            double money=0;
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                orderItem.setId(idWorker.nextId());
                orderItem.setOrderId(orderId);
                orderItem.setSellerId(cart.getSellerId());
                money=money+orderItem.getTotalFee().doubleValue();
                orderItemMapper.insert(orderItem);
            }
            tbOrder.setPayment(new BigDecimal(money));
            orderMapper.insert(tbOrder);
            //订单日志属性赋值
            orderList.add(orderId+"");
            totalFee=totalFee+money;
        }
        //修改当前用户购物车
        //redisTemplate.boundHashOps("cartList").delete(order.getUserId());
        //A-B做删除
        List<Cart> newCartList=deleteCartlist(cartList,upCartList);
        redisTemplate.boundHashOps("cartList").put(order.getUserId(),newCartList);
        if (order.getPaymentType().equals("1")){
            TbPayLog payLog=new TbPayLog();
            String outTradeNo=  idWorker.nextId()+"";//支付订单号
            payLog.setOutTradeNo(outTradeNo);//支付订单号
            payLog.setCreateTime(new Date());//创建时间
            //订单号列表，逗号分隔
            String ids= StringUtils.join(orderList,",");
            payLog.setOrderList(ids);//订单号列表，逗号分隔
            payLog.setPayType("1");//支付类型
            payLog.setTotalFee( (long)(totalFee*100 ) );//总金额(分)
            payLog.setTradeState("0");//支付状态
            payLog.setUserId(order.getUserId());//用户ID
            payLogMapper.insert(payLog);//插入到支付日志表
            redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);//放入缓存
        }
    }
    //A-B做删除
    private List<Cart> deleteCartlist(List<Cart> cartList, List<Cart> upCartList) {
        if(cartList.size()<=0 || upCartList.size()<=0){
            throw new RuntimeException("增加失败");
        }
        for (Cart cart : upCartList) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                Long itemId = orderItem.getItemId();
                Integer num = orderItem.getNum()*-1;
                cartList= addItemToCartList(cartList,itemId,num);
            }
        }
        return cartList;
    }
    //在集合中添加新的商品

    private List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //核对商品属性是否正确
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if(item==null){
            throw new RuntimeException("商品不存在");
        }
        if(!item.getStatus().equals("1")){
            throw new RuntimeException("商品不存在");
        }
        if(num==0){
            return cartList;
        }

        //将商品加入购物车集合
        //判断购物车中是否有该店铺
        Boolean a=false;
        if(cartList.size()==0){
            a=false;
        }else {
            for (Cart cart1 : cartList) {
                if(cart1.getSellerId().equals(item.getSellerId())){
                    a=true;
                }
            }
        }
        //1无该店铺
        if(a==false){
            //根据商品属性，创建商品对象
            Cart cart = new Cart();
            cart.setSellerId(item.getSellerId());
            cart.setSellerName(item.getSeller());
            TbOrderItem orderItem=creatOrderItem(item,num);
            List<TbOrderItem> list=new ArrayList<>();
            list.add(orderItem);
            cart.setOrderItemList(list);
            cartList.add(cart);
        }
        //2有该店铺
        if(a==true){
            Boolean cc=false;
            //若店铺中有此商品，则更改商品数量
            for (Cart cart : cartList) {
                if(cart.getSellerId().equals(item.getSellerId())){
                    if(cart.getOrderItemList().size()>0){
                        for (TbOrderItem orderItem : cart.getOrderItemList()) {
                            if(orderItem.getItemId().equals(item.getId())){
                                cc=true;
                                orderItem.setNum(orderItem.getNum()+num);
                                orderItem.setTotalFee(new BigDecimal(orderItem.getNum()*orderItem.getPrice().doubleValue()));
                                if(orderItem.getNum()<=0){
                                    cart.getOrderItemList().remove(orderItem);
                                }
                                if (cart.getOrderItemList().size()<=0){
                                    cartList.remove(cart);
                                }
                                if(ObjectUtils.isEmpty(cartList)){
                                    return new ArrayList<Cart>();
                                }
                                return cartList;
                            }
                        }
                    }
                }
            }
            //若店铺中无此商品，则店铺中添加此商品
            if(cc==false){
                for (Cart cart : cartList) {
                    if(cart.getSellerId().equals(item.getSellerId())){
                        TbOrderItem orderitm=creatOrderItem(item,num);
                        cart.getOrderItemList().add(orderitm);
                    }
                }

            }
        }

        return cartList;
    }

    //创建TbOrderItem
    private TbOrderItem creatOrderItem(TbItem item, Integer num) {
        if(num<=0){
            throw new RuntimeException("数量非法");
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
}
