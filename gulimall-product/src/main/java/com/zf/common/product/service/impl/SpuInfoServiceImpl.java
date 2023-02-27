package com.zf.common.product.service.impl;

import com.zf.common.product.dao.*;
import com.zf.common.product.entity.*;
import com.zf.common.product.entity.vo.spu.*;
import com.zf.common.product.feign.CouponFeignService;
import com.zf.common.product.service.*;
import com.zf.common.to.SkuReductionTo;
import com.zf.common.to.SpuBoundTo;
import com.zf.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zf.common.utils.PageUtils;
import com.zf.common.utils.Query;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDao spuInfoDao;
    @Autowired
    private SpuInfoDescDao spuInfoDescDao;
    @Autowired
    private SpuImagesService spuImagesService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private SkuInfoDao skuInfoDao;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SkuSaleAttrValueDao skuSaleAttrValueDao;
    @Autowired
    private AttrService attrService;
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private CouponFeignService couponFeignService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = {Exception.class,})
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {
        //1-保存spu基本信息 pms_spu_info
        SpuInfoEntity spuInfo = new SpuInfoEntity();
        BeanUtils.copyProperties(vo,spuInfo);
        spuInfo.setCreateTime(new Date());
        spuInfo.setUpdateTime(new Date());
        spuInfoDao.insert(spuInfo);

        //2-保存spu的描述图片 pms_spu_info_desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfo.getId());
        spuInfoDescEntity.setDecript(String.join(",",decript));
        spuInfoDescDao.insert(spuInfoDescEntity);

        //3-保存spu的图片集 pms_spu_images
        List<String> images = vo.getImages();
        spuImagesService.saveImages(spuInfo.getId(),images);

        //4-保存spu的规格参数 pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<Long> attrIds = baseAttrs.stream().map(BaseAttrs::getAttrId).collect(Collectors.toList());
        Map<Long, AttrEntity> allAttr = attrService.listByIds(attrIds).stream().collect(Collectors.toMap(AttrEntity::getAttrId, Function.identity()));
        List<ProductAttrValueEntity> attrs = baseAttrs.stream().map(base -> {
            ProductAttrValueEntity attrValueEntity = new ProductAttrValueEntity();
            attrValueEntity.setSpuId(spuInfo.getId());
            attrValueEntity.setAttrId(base.getAttrId());
            attrValueEntity.setAttrValue(base.getAttrValues());
            attrValueEntity.setAttrName(allAttr.get(base.getAttrId()).getAttrName());
            attrValueEntity.setQuickShow(base.getShowDesc());
            return attrValueEntity;
        }).collect(Collectors.toList());

        productAttrValueService.saveBatch(attrs);

        //5-保存spu的积分信息 sms_spu_bounds
        Bounds bounds = vo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds,spuBoundTo);
        spuBoundTo.setSpuId(spuInfo.getId());
        R r = couponFeignService.savaSpuBounds(spuBoundTo);
        if(!r.getCode().equals(0)){
            log.error("远程spu积分信息保存失败");
            throw new RuntimeException("远程spu积分信息保存失败");
        }

        //6-保存spu对应的所有sku信息
        //6.1-sku的基本信息 pms_sku_info
        List<Skus> skus = vo.getSkus();
        if (!CollectionUtils.isEmpty(skus)) {
            List<SkuInfoEntity> skuInfoEntities = skus.stream().map(sku -> {
                String defaultImg = "";
                for (Images image : sku.getImages()) {
                    if (image.getDefaultImg().equals(1)) {
                        defaultImg = image.getImgUrl();
                    }
                }
                Map<String, Object> tmp = new HashMap<>();
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfo.getBrandId());
                skuInfoEntity.setCatalogId(spuInfo.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfo.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoEntity.setImages(sku.getImages());
                skuInfoEntity.setAttr(sku.getAttr());

                tmp.put("sku",sku);
                skuInfoEntity.setTmp(tmp);

                return skuInfoEntity;
            }).collect(Collectors.toList());
            skuInfoService.saveBatch(skuInfoEntities);

            //6.2-sku的图片信息 pms_sku_images
            List<SkuImagesEntity> imagesEntities = new ArrayList<>();
            //6.3-sku的销售属性信息 pms_sku_sale_attr_value
            List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = new ArrayList<>();
            //6.4-sku的优惠,满减信息 gulimall_sms -> sms_sku_ladder\sms_sku_full_reduction\sms_member_price
            List<SkuReductionTo> skuReductionTos = new ArrayList<>();

            skuInfoEntities.forEach(newSku -> {
                newSku.getImages().forEach(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(newSku.getSkuId());
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    if(!StringUtils.isEmpty(skuImagesEntity.getImgUrl())){
                        imagesEntities.add(skuImagesEntity);
                    }
                });

                newSku.getAttr().forEach(attr -> {
                    SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr,attrValueEntity);
                    attrValueEntity.setSkuId(newSku.getSkuId());
                    skuSaleAttrValueEntities.add(attrValueEntity);
                });

                SkuReductionTo skuReductionTo = new SkuReductionTo();
                Skus sku = (Skus) newSku.getTmp().get("sku");
                BeanUtils.copyProperties(sku,skuReductionTo);
                skuReductionTo.setSkuId(newSku.getSkuId());
                if(skuReductionTo.getFullCount() >0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1){
                    skuReductionTos.add(skuReductionTo);
                }
            });
            skuImagesService.saveBatch(imagesEntities);
            skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);
            if (!CollectionUtils.isEmpty(skuReductionTos)){
                R res = couponFeignService.savaSkuReduction(skuReductionTos);
                if(!res.getCode().equals(0)){
                    log.error("远程sku优惠满减信息保存失败");
                    throw new RuntimeException("远程sku优惠满减信息保存失败");
                }
            }
        }
    }

    @Override
    public PageUtils queryPageByConditon(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> query = new QueryWrapper<>();
        String key = (String) params.get("key");
        String status = (String) params.get("status");
        String brandId = (String) params.get("brandId");
        String catelogId = (String) params.get("catelogId");

        if(!StringUtils.isEmpty(key)){
            query.and( q -> {
                q.eq("id", key).or().like("spu_name",key);
            });
        }
        if(!StringUtils.isEmpty(status)){
            query.eq("publish_status",status);
        }
        if(!StringUtils.isEmpty(brandId) && !"0".equals(brandId)){
            query.eq("brand_id",brandId);
        }
        if(!StringUtils.isEmpty(catelogId) && !"0".equals(catelogId)){
            query.eq("catalog_id",catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                query
        );

        return new PageUtils(page);
    }
}