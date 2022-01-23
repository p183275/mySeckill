package com.feng.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feng.seckill.entitys.constant.ExceptionConstant;
import com.feng.seckill.entitys.po.SeckillResultPO;
import com.feng.seckill.entitys.po.SeckillResultPO;
import com.feng.seckill.entitys.vo.HelpPage;
import com.feng.seckill.mapper.SeckillResultMapper;
import com.feng.seckill.service.SeckillResultService;
import com.feng.seckill.service.SeckillRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : pcf
 * @date : 2022/1/20 21:51
 */
@Slf4j
@Service
public class SeckillResultServiceImpl extends ServiceImpl<SeckillResultMapper, SeckillResultPO>
        implements SeckillResultService {

    @Autowired
    private SeckillResultMapper seckillResultMapper;

    @Override
    public Page<SeckillResultPO> queryPage(String key, Long productId, HelpPage helpPage) {
        
        // 创建分页数据
        Page<SeckillResultPO> seckillResultPOPage = new Page<>();
        // 复制属性
        BeanUtils.copyProperties(helpPage, seckillResultPOPage);

        // 新建包装器
        QueryWrapper<SeckillResultPO> queryWrapper = new QueryWrapper<>();

        if (productId != null){
            queryWrapper.eq("product_id", productId);
        }
        if (key != null && key.length() > 0){
            queryWrapper.and(wrapper -> wrapper.eq("user_id", key)
                    .or().like("user_name", key))
                    .or().like("product_name", key);
        }

        return this.baseMapper.selectPage(seckillResultPOPage, queryWrapper);
    }

    @Override
    public void deleteRecords(List<Long> resultIdList) {

        // 判空
        if (resultIdList.isEmpty())
            throw new RuntimeException(ExceptionConstant.DATA_NOT_NULL_EXCEPTION);

        // 删除
        seckillResultMapper.deleteBatchIds(resultIdList);
    }
}
