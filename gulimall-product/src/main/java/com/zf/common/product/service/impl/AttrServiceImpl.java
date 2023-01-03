package com.zf.common.product.service.impl;

import com.zf.common.product.dao.AttrAttrgroupRelationDao;
import com.zf.common.product.dao.AttrGroupDao;
import com.zf.common.product.dao.CategoryDao;
import com.zf.common.product.entity.AttrAttrgroupRelationEntity;
import com.zf.common.product.entity.AttrGroupEntity;
import com.zf.common.product.entity.CategoryEntity;
import com.zf.common.product.entity.vo.AttrResponseVo;
import com.zf.common.product.entity.vo.AttrVo;
import com.zf.common.product.service.CategoryService;
import org.apache.commons.lang.StringUtils;
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

import com.zf.common.product.dao.AttrDao;
import com.zf.common.product.entity.AttrEntity;
import com.zf.common.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrDao attrDao;
    @Autowired
    private AttrAttrgroupRelationDao relationDao;
    @Autowired
    private AttrGroupDao attrGroupDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private CategoryService categoryService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }
    @Transactional
    @Override
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        attrDao.insert(attrEntity);

        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrGroupId(attr.getAttrGroupId());
        relationEntity.setAttrId(attrEntity.getAttrId());
        relationDao.insert(relationEntity);
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long cateLogId) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
        PageUtils res = null;
        if(cateLogId != 0){
            queryWrapper.eq("catelog_id",cateLogId);
        }

        String key = (String) params.get("key");

        if(!StringUtils.isEmpty(key)){
            queryWrapper.and(obj -> {
                obj.eq("attr_id",key).or().like("attr_name",key);
            });
        }

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );
        List<AttrEntity> records = page.getRecords();
        List<Long> attrIds = records.stream().map(AttrEntity::getAttrId).collect(Collectors.toList());
        List<Long> catelogIds = records.stream().map(AttrEntity::getCatelogId).collect(Collectors.toList());
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = relationDao.selectBatchIds(attrIds);
        List<Long> attrGroupIds = attrAttrgroupRelationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrGroupId).collect(Collectors.toList());

        List<AttrGroupEntity> attrGroupEntities = attrGroupDao.selectBatchIds(attrGroupIds);
        List<CategoryEntity> entities = categoryDao.selectBatchIds(catelogIds);

        Map<Long, AttrGroupEntity> attrGroups = attrGroupEntities.stream().collect(Collectors.toMap(AttrGroupEntity::getAttrGroupId, t -> t));
        Map<Long, CategoryEntity> cateLogs = entities.stream().collect(Collectors.toMap(CategoryEntity::getCatId, t -> t));
        Map<Long, AttrAttrgroupRelationEntity> relationEntityMap = attrAttrgroupRelationEntities.stream().collect(Collectors.toMap(AttrAttrgroupRelationEntity::getAttrId, t -> t));

        List<AttrResponseVo> response = records.stream().map(item -> {
            AttrResponseVo responseVo = new AttrResponseVo();
            BeanUtils.copyProperties(item, responseVo);
            AttrAttrgroupRelationEntity relation = relationEntityMap.get(item.getAttrId());
            if (relation != null){
                AttrGroupEntity attrGroupEntity = attrGroups.get(relation.getAttrGroupId());
                if(attrGroupEntity != null){
                    responseVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }

            CategoryEntity categoryEntity = cateLogs.get(item.getCatelogId());
            if (categoryEntity != null) {
                responseVo.setCatelogName(categoryEntity.getName());
            }
            return responseVo;
        }).collect(Collectors.toList());
        res = new PageUtils(page);
        res.setList(response);
        return res;
    }

    @Override
    public AttrResponseVo getAttrInfo(Long attrId) {
        AttrEntity attrEntity = attrDao.selectById(attrId);
        AttrResponseVo vo = new AttrResponseVo();
        BeanUtils.copyProperties(attrEntity,vo);

        AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
        if(relationEntity != null){
            vo.setAttrGroupId(relationEntity.getAttrGroupId());
            AttrGroupEntity group = attrGroupDao.selectById(vo.getAttrGroupId());
            if(group != null ){
                vo.setGroupName(group.getAttrGroupName());
            }
        }

        Long[] cateLogPath = categoryService.getCateLogPath(attrEntity.getCatelogId());
        vo.setCatelogPath(cateLogPath);

        CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
        if(categoryEntity != null){
            vo.setCatelogName(categoryEntity.getName());
        }

        return vo;
    }

}