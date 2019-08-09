package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {
    @Autowired
    private TbBrandMapper tbBrandMapper;
    @Override
    public List<TbBrand> findAll() {
        return tbBrandMapper.selectByExample(null);
    }

    @Override
    public PageResult<TbBrand> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        Page<TbBrand> pageResult = (Page<TbBrand>) tbBrandMapper.selectByExample(null);
        return new PageResult<TbBrand>(pageResult.getTotal(), pageResult.getResult());
    }

    @Override
    public void save(TbBrand tbBrand) {
        if (tbBrand.getId() == null) {
            tbBrandMapper.insert(tbBrand);
        } else {
            tbBrandMapper.updateByPrimaryKey(tbBrand);
        }

    }

    @Override
    public TbBrand findOne(long id) {
        return tbBrandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void delete(String ids) {
        String[] split = ids.split(",");
        for (String id : split) {
            tbBrandMapper.deleteByPrimaryKey(Long.valueOf(id));
        }
    }
}
