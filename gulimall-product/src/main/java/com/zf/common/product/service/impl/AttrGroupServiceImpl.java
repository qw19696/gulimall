package com.zf.common.product.service.impl;

import com.zf.common.product.entity.AttrAttrgroupRelationEntity;
import com.zf.common.product.entity.AttrEntity;
import com.zf.common.product.service.AttrAttrgroupRelationService;
import com.zf.common.product.service.AttrService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zf.common.utils.PageUtils;
import com.zf.common.utils.Query;

import com.zf.common.product.dao.AttrGroupDao;
import com.zf.common.product.entity.AttrGroupEntity;
import com.zf.common.product.service.AttrGroupService;
import org.springframework.util.CollectionUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long id) {
        QueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            queryWrapper.and(obj -> {
                obj.eq("attr_group_id",key).or().like("attr_group_name",key);
            });
        }

        if (id == 0){
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    queryWrapper);
            return new PageUtils(page);
        }else {
            queryWrapper.eq("catelog_id",id);
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    queryWrapper);
            return new PageUtils(page);

        }


    }

    @Override
    public List<AttrEntity> getAttrByCateLogId(Long cateId) {
        List<AttrGroupEntity> relations = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", cateId));

        if (!CollectionUtils.isEmpty(relations)){
            List<Long> collect = relations.stream()
                    .map(AttrGroupEntity::getAttrGroupId)
                    .collect(Collectors.toList());

            List<AttrAttrgroupRelationEntity> attrRelations = (List<AttrAttrgroupRelationEntity>) attrAttrgroupRelationService.listByIds(collect);

            if (!CollectionUtils.isEmpty(attrRelations)){
                List<Long> attrIds = attrRelations.stream()
                        .map(AttrAttrgroupRelationEntity::getAttrId)
                        .collect(Collectors.toList());

                List<AttrEntity> attrs = (List<AttrEntity>) attrService.listByIds(attrIds);
                return attrs;
            }
        }
        return new ArrayList<>();
    }
}