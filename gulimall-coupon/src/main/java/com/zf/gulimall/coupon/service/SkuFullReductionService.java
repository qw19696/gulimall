package com.zf.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zf.common.to.SkuReductionTo;
import com.zf.common.utils.PageUtils;
import com.zf.gulimall.coupon.entity.SkuFullReductionEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品满减信息
 *
 * @author zf
 * @email 429558984@qq.com
 * @date 2022-12-08 11:01:32
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReduction(List<SkuReductionTo> reductionTos);
}

