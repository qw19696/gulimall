package com.zf.gulimall.ware.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.zf.common.utils.R;
import com.zf.gulimall.ware.feign.ProductFeignService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    @Autowired
    private WareSkuDao wareSkuDao;
    @Autowired
    private ProductFeignService productFeignService;
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

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //先查询该商品是否存在库存记录，有则修改无则新增
        List<WareSkuEntity> wareSkuEntities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if(CollectionUtil.isEmpty(wareSkuEntities)){
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            try{
                R info = productFeignService.info(skuId);
                Map<String,Object> res = (Map<String, Object>) info.get("skuInfo");
                if(info.getCode().equals(0)){
                    wareSkuEntity.setSkuName((String) res.get("skuName"));
                }
            }catch (Exception e){

            }


            wareSkuDao.insert(wareSkuEntity);
        }else {
            wareSkuDao.addStock(skuId,wareId,skuNum);
        }
    }
}