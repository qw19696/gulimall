package com.zf.common.product.dao;

import com.zf.common.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * spu信息
 * 
 * @author zf
 * @email 429558984@qq.com
 * @date 2022-12-06 20:05:38
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    /**
     * 添加spu基本信息
     * @param spuInfo
     */
    void saveBaseSpuInfo(SpuInfoEntity spuInfo);
}
