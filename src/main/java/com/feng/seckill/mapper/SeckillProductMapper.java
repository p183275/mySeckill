package com.feng.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.feng.seckill.entitys.po.SeckillProductPO;
import com.feng.seckill.entitys.vo.SeckillProductVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author : pcf
 * @date : 2022/1/16 15:41
 */
@Mapper
public interface SeckillProductMapper extends BaseMapper<SeckillProductPO> {

    IPage<SeckillProductVO> selectPageVo(IPage<?> page);

    /**
     * 让产品库存变为0
     * @param productId 产品id
     */
    void letProductionZero(@Param("productId") Long productId);
}
