package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {
    @Reference
    private SeckillService seckillGoodsService;

    /***
     * 秒杀商品列表
     * @return
     */
    @RequestMapping(value = "/list")
    public List<TbSeckillGoods> list(){
        return seckillGoodsService.list();
    }

    /***
     * 根据ID查询商品详情
     * @param id
     * @return
     */
    @RequestMapping(value = "/findOne/{id}")
    public TbSeckillGoods getOne(@PathVariable("id") Long id){
        return  seckillGoodsService.getOneFromRedis(id);
    }


}
