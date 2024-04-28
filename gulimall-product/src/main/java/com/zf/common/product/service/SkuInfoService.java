package com.zf.common.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zf.common.utils.PageUtils;
import com.zf.common.product.entity.SkuInfoEntity;

import java.util.Map;

/**
 * sku信息
 *
 * @author zf
 * @email 429558984@qq.com
 * @date 2022-12-06 20:05:38
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByCondition(Map<String, Object> params);
}

