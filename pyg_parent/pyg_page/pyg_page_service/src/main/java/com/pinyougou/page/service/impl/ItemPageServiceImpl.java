package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ItemPageServiceImpl implements ItemPageService {
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Value("${PAGE_FTL_FILE}")
    private String PAGE_FTL_FILE;//模板文件

    @Value("${PAGE_STATIC_DIR}")
    private String PAGE_STATIC_DIR;

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbItemCatMapper tbItemCatMapper;
    @Override
    public void geneItemHtml(Long goodsId) throws Exception {
        geneItemHtmlById(goodsId);
    }

    @Override
    public void geneItemHtmls() throws Exception {
        List<TbGoods> tbGoods = goodsMapper.selectByExample(null);
        for (TbGoods tbGood : tbGoods) {
            geneItemHtmlById(tbGood.getId());
        }
    }

    public void geneItemHtmlById(Long goodsId) throws Exception{
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        configuration.setDefaultEncoding("utf-8");
        //加载模板
        Template template = configuration.getTemplate(PAGE_FTL_FILE);
        //查询数据
        TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
        TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
        TbItemExample example=new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1")
                .andGoodsIdEqualTo(goodsId);
        List<TbItem> items = itemMapper.selectByExample(example);
        //查询目录
        TbItemCat itemCat1 = tbItemCatMapper.selectByPrimaryKey(goods.getCategory1Id());
        TbItemCat itemCat2 = tbItemCatMapper.selectByPrimaryKey(goods.getCategory2Id());
        TbItemCat itemCat3 = tbItemCatMapper.selectByPrimaryKey(goods.getCategory3Id());
        //构造map
        Map map = new HashMap<>();
        map.put("goods", goods);
        map.put("goodsDesc", goodsDesc);
        map.put("items", items);
        map.put("itemCat1", itemCat1.getName());
        map.put("itemCat2", itemCat2.getName());
        map.put("itemCat3", itemCat3.getName());
        for (TbItem item : items) {
            map.put("item", item);
            //输出
            Writer out = new FileWriter(new File(PAGE_STATIC_DIR+item.getId()+".html"));
            template.process(map,out);
            out.close();
        }
    }
}
