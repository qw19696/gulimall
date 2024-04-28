package com.zf.common.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zf.common.utils.PageUtils;
import com.zf.common.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author zf
 * @email 429558984@qq.com
 * @date 2022-12-06 20:05:38
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * spu管理-规格查询
     * @param spuId
     * @return
     */
    List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId);

    /**
     * 根据spuId去修改旗下相关规格数据
     * @param spuId
     * @param entitys
     */
    void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> entitys);
}

