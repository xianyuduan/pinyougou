package com.pinyougou.seckill.service;

import com.pinyougou.pojo.TbSeckillGoods;

import java.util.List;

public interface SeckillService {
    List<TbSeckillGoods> list();
    TbSeckillGoods getOneFromRedis(Long id);
}
