package com.feng.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feng.seckill.entitys.constant.ExceptionConstant;
import com.feng.seckill.entitys.po.FirstFilterPO;
import com.feng.seckill.entitys.vo.HelpPage;
import com.feng.seckill.mapper.FirstFilterMapper;
import com.feng.seckill.service.FirstFilterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author : pcf
 * @date : 2022/1/20 21:31
 */
@Slf4j
@Service
public class FirstFilterServiceImpl extends ServiceImpl<FirstFilterMapper, FirstFilterPO>
        implements FirstFilterService {

    @Autowired
    private FirstFilterMapper firstFilterMapper;

    /**
     * 分页查询加检索
     * @param key 关键字
     * @param passStatus 是否通过
     * @param helpPage 分页属性
     * @return 分页数据
     */
    @Override
    public Page<FirstFilterPO> queryPage(String key, String passStatus, HelpPage helpPage) {

        // 创建分页数据
        Page<FirstFilterPO> filterPOPage = new Page<>();
        // 复制属性
        BeanUtils.copyProperties(helpPage, filterPOPage);

        // 新建包装器
        QueryWrapper<FirstFilterPO> queryWrapper = new QueryWrapper<>();

        if (passStatus != null && passStatus.length() > 0){
            queryWrapper.eq("pass_status", passStatus);
        }
        if (key != null && key.length() > 0){
            queryWrapper.and(wrapper -> wrapper.eq("user_id", key)
                    .or().like("user_name", key));
        }

        return this.baseMapper.selectPage(filterPOPage, queryWrapper);
    }

    /**
     * 删除
     * @param filterIdList 主键
     */
    @Override
    public void deleteRecords(List<Long> filterIdList) {

        // 判空
        if (filterIdList.isEmpty())
            throw new RuntimeException(ExceptionConstant.DATA_NOT_NULL_EXCEPTION);

        // 删除
        firstFilterMapper.deleteBatchIds(filterIdList);
    }

    /**
     * 秒杀活动：初筛结果统计
     * @param userId 用户id
     * @param userName 用户姓名
     * @param passStatus 0 -通过 1-未通过
     */
    @Override
    public void addRecordsByUser(Long userId, String userName, String passStatus) {

        FirstFilterPO filterPO = new FirstFilterPO(userId, userName, passStatus);
        filterPO.setCreateDate(new Date());

        // 入库
        firstFilterMapper.insert(filterPO);
    }
}
