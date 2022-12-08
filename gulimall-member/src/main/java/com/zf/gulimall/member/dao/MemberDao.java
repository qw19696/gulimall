package com.zf.gulimall.member.dao;

import com.zf.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author zf
 * @email 429558984@qq.com
 * @date 2022-12-08 11:12:28
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
