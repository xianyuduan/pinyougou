package com.pinyougou.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {
    @Reference
    private ContentService contentService;

    //查询有效的轮播图广告
    @RequestMapping("/findContentList/{categoryId}")
    public List<TbContent> findContentList(@PathVariable("categoryId") Long categoryId) {
        return contentService.findContentList(categoryId);
    }
}
