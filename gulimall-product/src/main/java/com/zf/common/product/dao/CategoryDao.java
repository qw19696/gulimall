package com.zf.common.product.dao;

import com.zf.common.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author zf
 * @email 429558984@qq.com
 * @date 2022-12-06 20:05:38
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
