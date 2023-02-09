package com.zf.common.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zf.common.product.entity.vo.AttrGroupRelationVo;
import com.zf.common.product.entity.vo.AttrResponseVo;
import com.zf.common.product.entity.vo.AttrVo;
import com.zf.common.utils.PageUtils;
import com.zf.common.product.entity.AttrEntity;

import java.util.List;
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

    void saveAttr(AttrVo attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long cateLogId, String type);

    AttrResponseVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);

    List<AttrEntity> getRelationAttr(Long groupId);

    void deleteRelation(AttrGroupRelationVo[] req);

    /**
     * 查询当前属性没关联的参数
     * @param groupId
     * @param params
     * @return
     */
    PageUtils getNoRelationAttr(Long groupId, Map<String, Object> params);
}

