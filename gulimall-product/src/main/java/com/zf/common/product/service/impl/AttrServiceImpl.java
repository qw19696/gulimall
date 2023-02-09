package com.zf.common.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zf.common.constant.ProductConstant;
import com.zf.common.product.dao.AttrAttrgroupRelationDao;
import com.zf.common.product.dao.AttrGroupDao;
import com.zf.common.product.dao.CategoryDao;
import com.zf.common.product.entity.AttrAttrgroupRelationEntity;
import com.zf.common.product.entity.AttrGroupEntity;
import com.zf.common.product.entity.CategoryEntity;
import com.zf.common.product.entity.vo.AttrGroupRelationVo;
import com.zf.common.product.entity.vo.AttrResponseVo;
import com.zf.common.product.entity.vo.AttrVo;
import com.zf.common.product.service.CategoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        attrDao.insert(attrEntity);

        if (attr.getAttrType().equals(ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) && attr.getAttrGroupId() != null) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attrEntity.getAttrId());
            relationDao.insert(relationEntity);
        }
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long cateLogId, String type) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
        PageUtils res = null;

        queryWrapper.eq("attr_type", "base".equals(type) ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        if (cateLogId != 0) {
            queryWrapper.eq("catelog_id", cateLogId);
        }

        String key = (String) params.get("key");

        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(obj -> obj.eq("attr_id", key).or().like("attr_name", key));
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
        List<AttrGroupEntity> attrGroupEntities = new ArrayList<>();

        if ("base".equals(type)) {
            attrGroupEntities = attrGroupDao.selectBatchIds(attrGroupIds);
        }
        List<CategoryEntity> entities = categoryDao.selectBatchIds(catelogIds);

        Map<Long, AttrGroupEntity> attrGroups = attrGroupEntities.stream().collect(Collectors.toMap(AttrGroupEntity::getAttrGroupId, t -> t));
        Map<Long, CategoryEntity> cateLogs = entities.stream().collect(Collectors.toMap(CategoryEntity::getCatId, t -> t));
        Map<Long, AttrAttrgroupRelationEntity> relationEntityMap = attrAttrgroupRelationEntities.stream().collect(Collectors.toMap(AttrAttrgroupRelationEntity::getAttrId, t -> t));

        List<AttrResponseVo> response = records.stream().map(item -> {
            AttrResponseVo responseVo = new AttrResponseVo();
            BeanUtils.copyProperties(item, responseVo);
            AttrAttrgroupRelationEntity relation = relationEntityMap.get(item.getAttrId());
            if (relation != null) {
                AttrGroupEntity attrGroupEntity = attrGroups.get(relation.getAttrGroupId());
                if (attrGroupEntity != null) {
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
        BeanUtils.copyProperties(attrEntity, vo);

        if (attrEntity.getAttrType().equals(ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode())) {
            AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if (relationEntity != null) {
                vo.setAttrGroupId(relationEntity.getAttrGroupId());
                AttrGroupEntity group = attrGroupDao.selectById(vo.getAttrGroupId());
                if (group != null) {
                    vo.setGroupName(group.getAttrGroupName());
                }
            }
        }


        Long[] cateLogPath = categoryService.getCateLogPath(attrEntity.getCatelogId());
        vo.setCatelogPath(cateLogPath);

        CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
        if (categoryEntity != null) {
            vo.setCatelogName(categoryEntity.getName());
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        int i = attrDao.updateById(attrEntity);
        if (i <= 0) {
            throw new RuntimeException("修改规格参数失败！");
        }

        if (attr.getAttrType().equals(ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode())) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attr.getAttrId());
            int res = relationDao.update(relationEntity, new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            if (res <= 0) {
                throw new RuntimeException("规格参数关联关系修改失败！");
            }
        }
    }

    /**
     * 根据分组id找到所有的基本属性
     *
     * @param groupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long groupId) {
        List<AttrAttrgroupRelationEntity> attrGroupRelationEntities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", groupId));
        List<AttrEntity> attrEntities = null;
        if (attrGroupRelationEntities.size() > 0) {
            List<Long> attrIds = attrGroupRelationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
            attrEntities = attrDao.selectBatchIds(attrIds);
        }

        return attrEntities;
    }

    @Override
    public void deleteRelation(AttrGroupRelationVo[] req) {
        List<AttrGroupRelationVo> attrGroupRelationVos = Arrays.asList(req);
        List<AttrAttrgroupRelationEntity> relationEntities = attrGroupRelationVos.stream().map(item -> {
            AttrAttrgroupRelationEntity po = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, po);
            return po;
        }).collect(Collectors.toList());
        relationDao.removeRelation(relationEntities);
    }

    @Override
    public PageUtils getNoRelationAttr(Long groupId, Map<String, Object> params) {
        //当前分组只能关联和自己所属分类相同的参数 （分类即catelog）
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(groupId);
        Long catelogId = attrGroupEntity.getCatelogId();
        //查询出当前分类下(除传进来的分组id外)的所有分组
        List<AttrGroupEntity> attrGroups = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<Long> groupIds = attrGroups.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());
        List<AttrAttrgroupRelationEntity> relations = new ArrayList<>();
        IPage<AttrEntity> attrs = null;
        if (groupIds.size() > 0) {
            relations = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", groupIds));
        }

        List<Long> attrIds = relations.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());

        String key = (String) params.get("key");
        QueryWrapper<AttrEntity> query = new QueryWrapper<>();
        query.eq("catelog_id", catelogId).eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (attrIds.size() > 0){
            query.notIn("attr_id", attrIds);
        }
        //查询出当前已被关联的所有属性
        if (!StringUtils.isEmpty(key)) {
            query.eq("attr_id", key).or().like("attr_name", key);
        }
        attrs = attrDao.selectPage(new Query<AttrEntity>().getPage(params), query);

        return new PageUtils(attrs);
    }

}