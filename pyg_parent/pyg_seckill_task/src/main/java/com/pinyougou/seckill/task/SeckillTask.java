package com.pinyougou.seckill.task;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
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

    /***
     * 每年双十一启动秒杀
     * 将商品数据全部跟新到索引库
     */
    @Scheduled(cron = "0/10 * * * * ?")  //我们这里测试数据每分钟30秒执行
    public void startSeckill() {
        System.out.println("定时任务开始");
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
}
