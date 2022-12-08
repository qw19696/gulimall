package com.zf.gulimall.order.dao;

import com.zf.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author zf
 * @email 429558984@qq.com
 * @date 2022-12-08 11:20:02
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
