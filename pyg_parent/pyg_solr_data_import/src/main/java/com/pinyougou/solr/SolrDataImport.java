package com.pinyougou.solr;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrDataImport {

    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbTypeTemplateMapper tbTypeTemplateMapper;
    @Autowired
    private TbItemCatMapper tbItemCatMapper;
    @Autowired
    private TbSpecificationOptionMapper optionMapper;

    //将数导入solr
    public void dataImportFromDb2Solr() {
        TbItemExample example =new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> tbItems = tbItemMapper.selectByExample(example);
        for (TbItem tbItem : tbItems) {
            Map specMap=JSON.parseObject(tbItem.getSpec());
            tbItem.setSpecMap(specMap);
        }
        solrTemplate.saveBeans(tbItems);
        solrTemplate.commit();
    }

    //将brandList和specList导入Redis
    void dataImportFromDb2Redis() {
        //获取数据库数据
        List<TbItemCat> tbItemCats = tbItemCatMapper.selectByExample(null);
        for (TbItemCat tbItemCat : tbItemCats) {
            //获取模板id,根据模板id查询模板表
            TbTypeTemplate tbTypeTemplate = tbTypeTemplateMapper.selectByPrimaryKey(tbItemCat.getTypeId());
            //获取模板中的brandID,字符转map,存入Redis的brandList
            List<Map> brandList = JSON.parseArray(tbTypeTemplate.getBrandIds(), Map.class);
            redisTemplate.boundHashOps("brandList").put(tbItemCat.getName(),brandList);
            //获取模板中的brandID,字符转map
            List<Map> specList = JSON.parseArray(tbTypeTemplate.getSpecIds(), Map.class);
            //specList中不含有sku规格,遍历specList,拿到id,去sku中查询规格,写入specList
            for (Map map : specList) {
                TbSpecificationOptionExample example=new TbSpecificationOptionExample();
                TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
                criteria.andSpecIdEqualTo(Long.valueOf(String.valueOf(map.get("id"))));
                List<TbSpecificationOption> optionList = optionMapper.selectByExample(example);
                map.put("options", optionList);
            }
            //specList存入Redis
            redisTemplate.boundHashOps("specList").put(tbItemCat.getName(),specList);
            //获取模板中的customAttr,字符转map,存入Redis的customAttrList
            List<Map> customAttrList = JSON.parseArray(tbTypeTemplate.getCustomAttributeItems(), Map.class);
            redisTemplate.boundHashOps("customAttrList").put(tbItemCat.getName(),customAttrList);
        }

    }
    public static void main(String[] args) {
        ApplicationContext ac = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrDataImport solrDataImport = (SolrDataImport) ac.getBean("solrDataImport");
        solrDataImport.dataImportFromDb2Solr();
        //solrDataImport.dataImportFromDb2Redis();
    }


}
