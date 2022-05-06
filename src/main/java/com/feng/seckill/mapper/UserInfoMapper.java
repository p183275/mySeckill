package com.feng.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.feng.seckill.entitys.po.UserPO;
import com.feng.seckill.entitys.vo.UserInfoAndAccountVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author : pcf
 * @date : 2022/1/18 21:49
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserPO> {

    UserInfoAndAccountVO getUserInfoAndAccountVO(@Param("userId") Long userId);

    void updateAccId(@Param("accId") Long accId, @Param("userId") Long userId);

}
