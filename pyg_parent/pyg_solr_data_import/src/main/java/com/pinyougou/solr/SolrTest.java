package com.pinyougou.solr;

import com.pinyougou.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-solr.xml")
public class SolrTest {

    @Resource
    private SolrTemplate solrTemplate;

    @Test
    public void testAdd() {
        TbItem item = new TbItem();
        item.setId(1L);
        item.setCategory("手机");
        item.setTitle("华为 Mate20Pro 8G 256G");
        item.setSeller("华为官方旗舰店");
        item.setBrand("华为");
        item.setPrice(new BigDecimal(10));
        solrTemplate.saveBean(item);
        solrTemplate.commit();
    }

    @Test
    public void testDelete() {
        solrTemplate.deleteById("1");
        solrTemplate.commit();
    }

}
