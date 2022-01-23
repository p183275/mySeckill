package com.feng.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.feng.seckill.entitys.po.BreakRulePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author : pcf
 * @date : 2022/1/20 20:02
 */
@Mapper
public interface BreakRuleMapper extends BaseMapper<BreakRulePO> {

    /**
     * 通过id查询用户打破的规则
     * @param userId 用户id
     * @return 用户打破规则的id
     */
    List<Long> getRuleIdListByUserId(@Param("userId") Long userId);
}
