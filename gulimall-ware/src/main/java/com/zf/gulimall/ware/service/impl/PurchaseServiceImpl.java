package com.zf.gulimall.ware.service.impl;

import cn.hutool.core.lang.Assert;
import com.zf.common.enums.WareConstant;
import com.zf.gulimall.ware.entity.PurchaseDetailEntity;
import com.zf.gulimall.ware.entity.vo.MergeVo;
import com.zf.gulimall.ware.entity.vo.PurchaseDoneVo;
import com.zf.gulimall.ware.entity.vo.PurchaseItemDoneVo;
import com.zf.gulimall.ware.service.PurchaseDetailService;
import com.zf.gulimall.ware.service.WareSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zf.common.utils.PageUtils;
import com.zf.common.utils.Query;

import com.zf.gulimall.ware.dao.PurchaseDao;
import com.zf.gulimall.ware.entity.PurchaseEntity;
import com.zf.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDao purchaseDao;

    @Autowired
    private PurchaseDetailService purchaseDetailService;
    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {
        QueryWrapper<PurchaseEntity> query = new QueryWrapper<>();
        query.eq("status", WareConstant.PurchaseEnum.CREATED.getCode())
                .or().eq("status", WareConstant.PurchaseEnum.ASSIGNED.getCode());

        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                query
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void mergePurchase(MergeVo vo) {
        Long purchaseId = vo.getPurchaseId();
        List<Long> items = vo.getItems();
        //合并前检查采购需求的状态,不是已分配或新建状态不能合并
        Collection<PurchaseDetailEntity> details = purchaseDetailService.listByIds(items);
        details.forEach(item -> {
            if (!item.getStatus().equals(WareConstant.PurchaseDetailEnum.ASSIGNED.getCode())&&
                !item.getStatus().equals(WareConstant.PurchaseDetailEnum.CREATED.getCode())){

                throw new RuntimeException("仅能合并已分配或新建状态的采购需求");
            }
        });


        //如果采购需求合并没有指定采购单ID，则直接创建新采购单
        if(purchaseId == null){
            PurchaseEntity purchase = new PurchaseEntity();
            purchase.setCreateTime(new Date());
            purchase.setUpdateTime(new Date());
            purchase.setStatus(WareConstant.PurchaseEnum.CREATED.getCode());
            purchaseDao.insert(purchase);

            purchaseId = purchase.getId();
        }


        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> detail = items.stream().map(i -> {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            detailEntity.setId(i);
            detailEntity.setPurchaseId(finalPurchaseId);
            detailEntity.setStatus(WareConstant.PurchaseDetailEnum.ASSIGNED.getCode());
            return detailEntity;
        }).collect(Collectors.toList());

        purchaseDetailService.updateBatchById(detail);
        PurchaseEntity updateTime = new PurchaseEntity();
        updateTime.setId(purchaseId);
        updateTime.setUpdateTime(new Date());
        purchaseDao.updateById(updateTime);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void receive(List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)){
            throw new RuntimeException("采购单id不能为空！");
        }

        List<PurchaseEntity> purchases = purchaseDao.selectBatchIds(ids);
        if(CollectionUtils.isEmpty(purchases)){
            throw new RuntimeException("没有目标采购单！");
        }
        List<PurchaseEntity> needUpdate = purchases.stream().filter(item -> {
            return item.getStatus().equals(WareConstant.PurchaseEnum.CREATED.getCode()) ||
                    item.getStatus().equals(WareConstant.PurchaseEnum.ASSIGNED.getCode());
        }).map(item -> {
            item.setStatus(WareConstant.PurchaseEnum.RECEIVE.getCode());
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());

        //修改采购单状态
        this.updateBatchById(needUpdate);

        //修改采购项状态
        List<Long> id = needUpdate.stream().map(PurchaseEntity::getId).collect(Collectors.toList());
        List<PurchaseDetailEntity> detailEntities = purchaseDetailService.list(new QueryWrapper<PurchaseDetailEntity>().in("purchase_id", id));
        if(!CollectionUtils.isEmpty(detailEntities)){
            detailEntities.forEach(item -> {
                item.setStatus(WareConstant.PurchaseDetailEnum.BUYING.getCode());
            });
            purchaseDetailService.updateBatchById(detailEntities);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void done(PurchaseDoneVo vo) {
        //1.改变采购项状态
        boolean flag = true;
        List<PurchaseItemDoneVo> items = vo.getItems();
        List<PurchaseDetailEntity> updates = new ArrayList<>();
        for (PurchaseItemDoneVo item : items) {
            PurchaseDetailEntity detailEntity =new PurchaseDetailEntity();
            if(item.getStatus() == WareConstant.PurchaseDetailEnum.HASERROR.getCode()){
                flag = false;
                detailEntity.setStatus(item.getStatus());
            }else {
                //.成功采购的进行入库
                detailEntity.setStatus(WareConstant.PurchaseDetailEnum.FINISH.getCode());
                PurchaseDetailEntity detail = purchaseDetailService.getById(item.getId());
                wareSkuService.addStock(detail.getSkuId(),detail.getWareId(),detail.getSkuNum());
            }
            detailEntity.setId(item.getId());
            updates.add(detailEntity);
        }

        purchaseDetailService.updateBatchById(updates);

        //2.改变采购单状态
        Long purchaseId = vo.getId();
        PurchaseEntity purchase = new PurchaseEntity();
        purchase.setId(purchaseId);
        purchase.setStatus(flag? WareConstant.PurchaseEnum.FINISH.getCode():WareConstant.PurchaseEnum.HASERROR.getCode());
        updateById(purchase);


    }

}