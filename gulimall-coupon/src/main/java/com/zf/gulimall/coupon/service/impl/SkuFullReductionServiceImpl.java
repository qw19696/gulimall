package com.zf.gulimall.coupon.service.impl;

import com.zf.common.to.MemberPrice;
import com.zf.common.to.SkuReductionTo;
import com.zf.gulimall.coupon.entity.MemberPriceEntity;
import com.zf.gulimall.coupon.entity.SkuLadderEntity;
import com.zf.gulimall.coupon.service.MemberPriceService;
import com.zf.gulimall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zf.common.utils.PageUtils;
import com.zf.common.utils.Query;

import com.zf.gulimall.coupon.dao.SkuFullReductionDao;
import com.zf.gulimall.coupon.entity.SkuFullReductionEntity;
import com.zf.gulimall.coupon.service.SkuFullReductionService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    private SkuLadderService skuLadderService;

    @Autowired
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSkuReduction(List<SkuReductionTo> reductionTos) {
        //sku的优惠
        List<SkuLadderEntity> ladderEntities = new ArrayList<>();
        //sku的满减信息
        List<SkuFullReductionEntity> reductionEntities = new ArrayList<>();
        //会员价格
        List<MemberPriceEntity> prices = new ArrayList<>();

        reductionTos.forEach(reductionTo -> {
            SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
            skuLadderEntity.setSkuId(reductionTo.getSkuId());
            skuLadderEntity.setFullCount(reductionTo.getFullCount());
            skuLadderEntity.setDiscount(reductionTo.getDiscount());
            skuLadderEntity.setAddOther(reductionTo.getCountStatus());
            if(skuLadderEntity.getFullCount() > 0){
                ladderEntities.add(skuLadderEntity);
            }


            SkuFullReductionEntity reductionEntity = new SkuFullReductionEntity();
            BeanUtils.copyProperties(reductionTo,reductionEntity);
            if(reductionEntity.getFullPrice().compareTo(new BigDecimal("0")) == 1){
                reductionEntities.add(reductionEntity);
            }

            List<MemberPrice> memberPrice = reductionTo.getMemberPrice();
            List<MemberPriceEntity> newPrices = memberPrice.stream().map(price -> {
                MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
                memberPriceEntity.setSkuId(reductionTo.getSkuId());
                memberPriceEntity.setMemberLevelId(price.getId());
                memberPriceEntity.setMemberLevelName(price.getName());
                memberPriceEntity.setMemberPrice(price.getPrice());
                memberPriceEntity.setAddOther(1);
                return memberPriceEntity;
            }).filter(item -> {
                return item.getMemberPrice().compareTo(new BigDecimal("0")) == 1;
            }).collect(Collectors.toList());

            prices.addAll(newPrices);
        });

        if(!CollectionUtils.isEmpty(ladderEntities)){
            skuLadderService.saveBatch(ladderEntities);
        }
        if(!CollectionUtils.isEmpty(reductionEntities)){
            this.saveBatch(reductionEntities);
        }
        if(!CollectionUtils.isEmpty(prices)){
            memberPriceService.saveBatch(prices);
        }
    }
}