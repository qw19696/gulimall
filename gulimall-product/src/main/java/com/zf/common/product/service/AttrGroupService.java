package com.zf.common.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zf.common.product.entity.AttrEntity;
import com.zf.common.utils.PageUtils;
import com.zf.common.product.entity.AttrGroupEntity;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author zf
 * @email 429558984@qq.com
 * @date 2022-12-06 20:05:39
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long id);

    /**
     * 根据分类Id获取旗下所有属性分组的所有属性
     * @param cateId
     * @return
     */
    List<AttrEntity> getAttrByCateLogId(Long cateId);
}

