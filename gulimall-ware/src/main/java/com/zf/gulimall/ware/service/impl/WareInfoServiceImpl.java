package com.zf.gulimall.ware.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zf.common.utils.PageUtils;
import com.zf.common.utils.Query;

import com.zf.gulimall.ware.dao.WareInfoDao;
import com.zf.gulimall.ware.entity.WareInfoEntity;
import com.zf.gulimall.ware.service.WareInfoService;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> query = new QueryWrapper<>();
        String key = (String) params.get("key");

        if(!StringUtils.isEmpty(key)){
            query.and( q -> {
                q.eq("id", key)
                 .or().like("name",key)
                 .or().like("address",key)
                 .or().eq("areacode",key);
            });
        }

        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                query
        );

        return new PageUtils(page);
    }

}