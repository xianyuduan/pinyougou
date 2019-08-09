package com.pinyougou.seckill.task;

import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class SeckillTask {

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbSeckillOrderMapper tbSeckillOrderMapper;

    @Autowired
    private TbOrderMapper tbOrderMapper;
    /***
     * 每年双十一启动秒杀
     * 将商品数据全部跟新到索引库
     */
    @Scheduled(cron = "0/10 * * * * ?")  //我们这里测试数据每分钟30秒执行
    public void startSeckill() {
        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
        //商品审核通过
        criteria.andStatusEqualTo("1");
        //库存数量>0
        criteria.andStockCountGreaterThan(0);
        //活动开始时间     <=当前时间<=     活动结束时间
        Date date = new Date();
        criteria.andStartTimeLessThanOrEqualTo(date);       //活动开始时间<=now()
        criteria.andEndTimeGreaterThanOrEqualTo(date);       //活动结束时间>now()

        //批量查询所有缓存数据，增加到Redis缓存中
        List<TbSeckillGoods> goods = seckillGoodsMapper.selectByExample(example);

        //将商品数据加入到缓存中
        for (TbSeckillGoods good : goods) {
        //秒杀商品信息加入缓存
            redisTemplate.boundHashOps(TbSeckillGoods.class.getSimpleName()).put(good.getId(), good);
        }
    }

    //定时清理失效订单
    @Scheduled(cron="0 * * * * *")
    public void timeCleanOrder() {
        //清理普通订单
        long date = new Date().getTime();
        List<TbPayLog> payLog = redisTemplate.boundHashOps("payLog").values();
        for (TbPayLog tbPayLog : payLog) {
            if ("0".equals(tbPayLog.getTradeState())) {
                long time = tbPayLog.getCreateTime().getTime();
                if (date - time >= 60 * 60 * 2) {
                    if (tbPayLog.getTradeState().equals("0")) {
                        TbOrderExample example = new TbOrderExample();
                        TbOrderExample.Criteria criteria = example.createCriteria();
                        criteria.andUserIdEqualTo(tbPayLog.getUserId());
                        List<TbOrder> orders = tbOrderMapper.selectByExample(example);
                        for (TbOrder order : orders) {
                            order.setStatus("6");
                            tbOrderMapper.updateByPrimaryKey(order);
                        }
                    }
                }
            }

        }
        //清理定时秒杀无效订单
        List<TbSeckillOrder> seckillOrder = redisTemplate.boundHashOps(TbSeckillOrder.class.getSimpleName()).values();
        for (TbSeckillOrder tbSeckillOrder : seckillOrder) {
            if ("0".equals(tbSeckillOrder.getStatus())) {
                long time2 = tbSeckillOrder.getCreateTime().getTime();
                if (date - time2 >= 60 * 60 * 1) {
                    if (tbSeckillOrder.getStatus().equals("0")) {
                        TbSeckillOrderExample example = new TbSeckillOrderExample();
                        TbSeckillOrderExample.Criteria criteria = example.createCriteria();
                        criteria.andUserIdEqualTo(tbSeckillOrder.getUserId());
                        List<TbSeckillOrder> tbSeckillOrders = tbSeckillOrderMapper.selectByExample(example);
                        for (TbSeckillOrder order : tbSeckillOrders) {
                            order.setStatus("6");
                            tbSeckillOrderMapper.updateByPrimaryKey(order);
                        }
                    }
                }
            }
        }
    }
}
