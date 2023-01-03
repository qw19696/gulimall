package com.zf.common.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zf.common.product.dao.BrandDao;
import com.zf.common.product.dao.CategoryDao;
import com.zf.common.product.entity.BrandEntity;
import com.zf.common.product.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zf.common.utils.PageUtils;
import com.zf.common.utils.Query;

import com.zf.common.product.dao.CategoryBrandRelationDao;
import com.zf.common.product.entity.CategoryBrandRelationEntity;
import com.zf.common.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    private BrandDao brandDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private CategoryBrandRelationDao categoryBrandRelationDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public int saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long cateLogId = categoryBrandRelation.getCatelogId();
        BrandEntity brand = brandDao.selectById(brandId);
        CategoryEntity categoryEntity = categoryDao.selectById(cateLogId);
        categoryBrandRelation.setBrandName(brand.getName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());
        return categoryBrandRelationDao.insert(categoryBrandRelation);
    }

    @Override
    public int updateBrand(Long id, String name) {
        CategoryBrandRelationEntity categoryBrandRelation = new CategoryBrandRelationEntity();
        categoryBrandRelation.setBrandId(id);
        categoryBrandRelation.setBrandName(name);
        return categoryBrandRelationDao.update(categoryBrandRelation, new UpdateWrapper<CategoryBrandRelationEntity>().eq("brand_id",id));
    }

    @Override
    public int updateCategory(Long id, String name) {
        return categoryBrandRelationDao.updateCategory(id,name);
    }

}