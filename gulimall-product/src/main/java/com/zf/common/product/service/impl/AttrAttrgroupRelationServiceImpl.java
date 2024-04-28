package com.zf.common.product.service.impl;

import com.zf.common.product.entity.vo.AttrGroupRelationVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zf.common.utils.PageUtils;
import com.zf.common.utils.Query;

import com.zf.common.product.dao.AttrAttrgroupRelationDao;
import com.zf.common.product.entity.AttrAttrgroupRelationEntity;
import com.zf.common.product.service.AttrAttrgroupRelationService;
import org.springframework.transaction.annotation.Transactional;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Autowired
    private AttrAttrgroupRelationDao relationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }
    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void saveBatch(List<AttrGroupRelationVo> relationVos) {
        List<AttrAttrgroupRelationEntity> po = relationVos.stream().map(item -> {
            AttrAttrgroupRelationEntity attrgroupRelation = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, attrgroupRelation);
            return attrgroupRelation;
        }).collect(Collectors.toList());

        boolean b = this.saveBatch(po);
        if(!b){
            throw new RuntimeException("新增属性关联分组错误！");
        }
    }

}