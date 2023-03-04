package com.zf.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zf.common.utils.PageUtils;
import com.zf.gulimall.ware.entity.PurchaseEntity;
import com.zf.gulimall.ware.entity.vo.MergeVo;
import com.zf.gulimall.ware.entity.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author zf
 * @email 429558984@qq.com
 * @date 2022-12-08 13:37:15
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceive(Map<String, Object> params);

    void mergePurchase(MergeVo purchase);

    void receive(List<Long> ids);

    void done(PurchaseDoneVo vo);
}

