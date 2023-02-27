package com.zf.gulimall.ware.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zf.common.utils.PageUtils;
import com.zf.common.utils.Query;

import com.zf.gulimall.ware.dao.WareSkuDao;
import com.zf.gulimall.ware.entity.WareSkuEntity;
import com.zf.gulimall.ware.service.WareSkuService;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> query = new QueryWrapper<>();
        String key = (String) params.get("key");
        String skuId = (String) params.get("skuId");

        if(!StringUtils.isEmpty(key)){
            query.and( q -> {
                q.eq("id", key)
                        .or().eq("ware_id",key)
                        .or().like("sku_name",key);
            });
        }

        if(!StringUtils.isEmpty(skuId)){
            query.eq("sku_id",skuId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                query
        );

        return new PageUtils(page);
    }

}