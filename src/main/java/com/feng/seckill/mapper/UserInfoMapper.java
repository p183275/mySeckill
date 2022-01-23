package com.feng.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.feng.seckill.entitys.po.UserPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : pcf
 * @date : 2022/1/18 21:49
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserPO> {
}
