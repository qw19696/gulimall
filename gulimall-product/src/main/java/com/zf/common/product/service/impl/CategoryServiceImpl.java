package com.zf.common.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zf.common.utils.PageUtils;
import com.zf.common.utils.Query;

import com.zf.common.product.dao.CategoryDao;
import com.zf.common.product.entity.CategoryEntity;
import com.zf.common.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        try{
            QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("show_status",1);
            List<CategoryEntity> entities = categoryDao.selectList(wrapper);
            List<CategoryEntity> tree = entities.stream()
                    .filter(category -> category.getCatLevel().equals(1))
                    .map(category -> {category.setChildren(getChildren(category,entities)); return category;})
                    .sorted((category1,category2) -> {return (category1.getSort()==null?0:category1.getSort()) - (category2.getSort()==null?0:category2.getSort());})
                    .collect(Collectors.toList());
//        List<Long> parentIds = entities.stream().map(CategoryEntity::getParentCid).collect(Collectors.toList());
            return tree;
        }catch (Exception e){
            System.out.println(e);
            return null;
        }
    }

    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all){
        List<CategoryEntity> collect = all.stream()
                .filter(categoryEntity -> {
                    return categoryEntity.getParentCid().equals(root.getCatId());
                })
                .map(categoryEntity -> {
                    categoryEntity.setChildren(getChildren(categoryEntity, all));
                    return categoryEntity;
                })
                .sorted((menu1, menu2) -> {
                    return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
                })
                .collect(Collectors.toList());
        return collect;
    }

   @Override
   public int removeMenuByIds(List<Long> ids) {
       UpdateWrapper<CategoryEntity> wrapper = new UpdateWrapper<>();
       wrapper.set("show_status", 0).in("cat_id",ids);
       return categoryDao.update(null, wrapper);
   }
}