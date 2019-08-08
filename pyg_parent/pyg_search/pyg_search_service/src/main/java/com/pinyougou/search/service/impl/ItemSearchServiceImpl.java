package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import com.pinyougou.vo.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    //按条件solr查询
    @Override
    public Map<String, Object> search(Map searchEntity) {
        HighlightQuery query = new SimpleHighlightQuery();
        Criteria criteria = new Criteria("item_keywords").is(searchEntity.get("keywords"));
        query.addCriteria(criteria);


        //查出所有分类,写入categoryList
        SimpleQuery groupQuery = new SimpleQuery();
        groupQuery.addCriteria(criteria);//与高亮同一条件
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");//设置域名
        groupQuery.setGroupOptions(groupOptions);
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(groupQuery, TbItem.class);//从solr中查分组
        GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");//拿到数据中的item_category
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //将分组目录封装进categoryList,由map传到前端
        List<String> categoryList = new ArrayList<>();
        for (GroupEntry<TbItem> groupEntry : groupEntries) {
            categoryList.add(groupEntry.getGroupValue());
        }

        //从Redis中查出brandList和specList
        List<String> brandList = new ArrayList<>();
        List<String> specList = new ArrayList<>();
        if (categoryList.size() > 0) {
            String categoryName = categoryList.get(0);
            brandList = (List<String>) redisTemplate.boundHashOps("brandList").get(categoryName);
            specList = (List<String>) redisTemplate.boundHashOps("specList").get(categoryName);
        }


        //设置高亮区域
        HighlightOptions options = new HighlightOptions();
        options.addField("item_title");
        options.setSimplePrefix("<font style='color:red'>");
        options.setSimplePostfix("</font>");
        query.setHighlightOptions(options);

        //设置过滤条件
        SimpleFilterQuery filterQuery = new SimpleFilterQuery();
        //添加分类过滤
        String category = (String) searchEntity.get("category");
        if (!StringUtils.isEmpty(category)) {
            filterQuery.addCriteria(new Criteria("item_category").is(category));
        }
        //添加品牌过滤
        String brand = (String) searchEntity.get("brand");
        if (!StringUtils.isEmpty(brand)) {
            filterQuery.addCriteria(new Criteria("item_brand").is(brand));
        }

        //添加规格过滤
        Map<String, String> specMap = (Map) searchEntity.get("spec");
        if (null != specMap && specMap.keySet().size() > 0) {
            for (String key : specMap.keySet()) {
                String value = specMap.get(key);
                filterQuery.addCriteria(new Criteria("item_spec_" + key).is(value));
            }
        }

        //添加价格过滤
        String price = (String) searchEntity.get("price");
        if (!StringUtils.isEmpty(price)) {
            String[] prices = price.split("-");
            if ("*".equals(prices[1])) {
                //3000元以上
                filterQuery.addCriteria(new Criteria("item_price").greaterThanEqual(3000));
            } else {
                filterQuery.addCriteria(new Criteria("item_price").between(prices[0],prices[1]));
            }
        }

        //排序
        String sortField = (String) searchEntity.get("sortField");
        String sort = (String) searchEntity.get("sort");
        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(sortField)) {
            if ("ASC".equals(sort)) {
                query.addSort(new Sort(Sort.Direction.ASC, sortField));
            } else {
                query.addSort(new Sort(Sort.Direction.DESC, sortField));
            }
        }

        query.addFilterQuery(filterQuery);

        //设置分页参数
        Integer page = (Integer) searchEntity.get("page");
        Integer size = (Integer) searchEntity.get("size");
        query.setOffset((page - 1) * size);
        query.setRows(size);
        //查询满足条件的
        HighlightPage<TbItem> tbItems = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //将原有title替换为带有高亮的title
        List<HighlightEntry<TbItem>> highlighted = tbItems.getHighlighted();
        for (HighlightEntry<TbItem> highlightEntry : highlighted) {
            TbItem entity = highlightEntry.getEntity();//原有对象
            List<HighlightEntry.Highlight> highlights = highlightEntry.getHighlights();//高亮对象
            if (null != highlights && highlights.size() > 0
                    && null != highlights.get(0).getSnipplets()
                    && highlights.get(0).getSnipplets().size() > 0) {
                entity.setTitle(highlights.get(0).getSnipplets().get(0));//将原有title替换为带有高亮的title
            }
        }


        Map map = new HashMap();
        map.put("total", tbItems.getTotalElements());//总数量
        map.put("totalPages", tbItems.getTotalPages());//总页数
        map.put("itemList", tbItems.getContent());//商品列表
        map.put("categoryList", categoryList);//分类列表
        map.put("brandList", brandList);//品牌列表
        map.put("specList", specList);//规格列表
        return map;
    }
}
