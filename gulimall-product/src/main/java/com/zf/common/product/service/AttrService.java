package com.zf.common.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zf.common.utils.PageUtils;
import com.zf.common.product.entity.AttrEntity;

import java.util.Map;

/**
 * 商品属性
 *
 * @author zf
 * @email 429558984@qq.com
 * @date 2022-12-06 20:05:39
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

