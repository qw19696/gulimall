package com.zf.common.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zf.common.utils.PageUtils;
import com.zf.common.product.entity.SpuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * spu图片
 *
 * @author zf
 * @email 429558984@qq.com
 * @date 2022-12-06 20:05:38
 */
public interface SpuImagesService extends IService<SpuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 批量插入spu图片
     * @param images
     */
    void saveImages(Long spuId,List<String> images);
}

