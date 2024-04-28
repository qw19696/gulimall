package com.zf.common.product.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zf.common.utils.PageUtils;
import com.zf.common.utils.Query;

import com.zf.common.product.dao.SkuInfoDao;
import com.zf.common.product.entity.SkuInfoEntity;
import com.zf.common.product.service.SkuInfoService;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> query = new QueryWrapper<>();
        String key = (String) params.get("key");
        String brandId = (String) params.get("brandId");
        String catelogId = (String) params.get("catelogId");
        String min = (String) params.get("min");
        String max = (String) params.get("max");

        if(!StringUtils.isEmpty(key)){
            query.and( q -> {
                q.eq("sku_id", key).or().like("sku_name",key);
            });
        }
        if(!StringUtils.isEmpty(brandId) && !"0".equals(brandId)){
            query.eq("brand_id",brandId);
        }
        if(!StringUtils.isEmpty(catelogId) && !"0".equals(catelogId)){
            query.eq("catalog_id",catelogId);
        }
        if(!StringUtils.isEmpty(min) && !"0".equals(min)){
            query.ge("price", min);
        }
        if(!StringUtils.isEmpty(max) && !"0".equals(max)){
            query.le("price", max);
        }


        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                query
        );

        return new PageUtils(page);
    }

}