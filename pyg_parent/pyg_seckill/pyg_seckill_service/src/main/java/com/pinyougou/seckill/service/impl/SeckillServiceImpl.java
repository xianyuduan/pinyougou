package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@Transactional
public class SeckillServiceImpl implements SeckillService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public List<TbSeckillGoods> list() {
        return (List<TbSeckillGoods>) redisTemplate.boundHashOps(TbSeckillGoods.class.getSimpleName()).values();
    }

    @Override
    public TbSeckillGoods getOneFromRedis(Long id) {
        return (TbSeckillGoods) redisTemplate.boundHashOps(TbSeckillGoods.class.getSimpleName()).get(id);
    }

}
