package com.zf.common.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zf.common.utils.PageUtils;
import com.zf.common.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author zf
 * @email 429558984@qq.com
 * @date 2022-12-06 20:05:38
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    int removeMenuByIds(List<Long> ids);

    Long[] getCateLogPath(Long catelogId);

    void updateCascade(CategoryEntity category);
}

