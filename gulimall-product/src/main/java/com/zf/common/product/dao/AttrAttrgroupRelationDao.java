package com.zf.common.product.dao;

import com.zf.common.product.entity.AttrAttrgroupRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性&属性分组关联
 * 
 * @author zf
 * @email 429558984@qq.com
 * @date 2022-12-06 20:05:39
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {
    /**
     * 移除关联关系
     * @param relationEntities
     */
    void removeRelation(@Param("relationEntities") List<AttrAttrgroupRelationEntity> relationEntities);
}
