package com.zf.common.product.service.impl;

import com.zf.common.product.entity.AttrAttrgroupRelationEntity;
import com.zf.common.product.entity.AttrEntity;
import com.zf.common.product.entity.vo.AttrGroupWithAttrsVo;
import com.zf.common.product.service.AttrAttrgroupRelationService;
import com.zf.common.product.service.AttrService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
    public List<AttrGroupWithAttrsVo> getAttrByCateLogId(Long cateId) {
        List<AttrGroupEntity> groups = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", cateId));//查询到该catelogid下所有的商品分组
        List<AttrGroupWithAttrsVo> response = new ArrayList<>();

        if (!CollectionUtils.isEmpty(groups)){
            List<Long> collect = groups.stream()//获取所有groupId
                    .map(AttrGroupEntity::getAttrGroupId)
                    .collect(Collectors.toList());

            List<AttrAttrgroupRelationEntity> attrRelations = (List<AttrAttrgroupRelationEntity>) attrAttrgroupRelationService.listByIds(collect);//查询到所有分组关联关系

            if (!CollectionUtils.isEmpty(attrRelations)){
                List<Long> attrIds = attrRelations.stream()//获取到所有属性id
                        .map(AttrAttrgroupRelationEntity::getAttrId)
                        .collect(Collectors.toList());

                List<AttrEntity> attrs = (List<AttrEntity>) attrService.listByIds(attrIds);//查询到所有属性

                Map<Long, List<AttrAttrgroupRelationEntity>> group = attrRelations.stream().collect(Collectors.groupingBy(AttrAttrgroupRelationEntity::getAttrGroupId));//将关系类 根据groupId分组

                if (!CollectionUtils.isEmpty(attrs)){
                    Map<Long, AttrEntity> attrMap = attrs.stream().collect(Collectors.toMap(AttrEntity::getAttrId, Function.identity()));//将属性list转化为key为attrId的map
                    Map<Long, AttrGroupEntity> groupMap = groups.stream().collect(Collectors.toMap(AttrGroupEntity::getAttrGroupId, Function.identity()));//将属性分组list转化为key为groupId的map
                    response = attrRelations.stream().map(item -> {
                        AttrGroupWithAttrsVo vo = new AttrGroupWithAttrsVo();
                        BeanUtils.copyProperties(groupMap.get(item.getAttrGroupId()), vo);
                        List<AttrEntity> res = group.get(item.getAttrGroupId()).stream().map(relation -> {
                            return attrMap.get(relation.getAttrId());
                        }).collect(Collectors.toList());
                        vo.setAttrs(res);
                        return vo;
                    }).collect(Collectors.toList());
                }
                return response;
            }
        }
        return response;
    }
}