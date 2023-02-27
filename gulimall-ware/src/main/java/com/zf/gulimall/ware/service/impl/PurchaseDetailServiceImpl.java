package com.zf.gulimall.ware.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zf.common.utils.PageUtils;
import com.zf.common.utils.Query;

import com.zf.gulimall.ware.dao.PurchaseDetailDao;
import com.zf.gulimall.ware.entity.PurchaseDetailEntity;
import com.zf.gulimall.ware.service.PurchaseDetailService;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseDetailEntity> query = new QueryWrapper<>();
        String key = (String) params.get("key");
        String wareId = (String) params.get("wareId");
        String status = (String) params.get("status");

        if(!StringUtils.isEmpty(key)){
            query.and( q -> {
                q.eq("purchase_id", key)
                        .or().eq("sku_id",key);
            });
        }

        if(!StringUtils.isEmpty(wareId)){
            query.eq("ware_id",wareId);
        }

        if(!StringUtils.isEmpty(status)){
            query.eq("status",status);
        }

        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                query
        );

        return new PageUtils(page);
    }

}