package com.feng.seckill.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.feng.seckill.entitys.po.FirstFilterPO;
import com.feng.seckill.entitys.vo.HelpPage;

import java.util.List;

/**
 * @author : pcf
 * @date : 2022/1/20 21:30
 */
public interface FirstFilterService extends IService<FirstFilterPO> {

    /**
     * 分页查询加检索
     * @param key 关键字
     * @param passStatus 是否通过
     * @param helpPage 分页属性
     * @return 分页数据
     */
    Page<FirstFilterPO> queryPage(String key, String passStatus, HelpPage helpPage);

    /**
     * 删除
     * @param filterIdList 主键
     */
    void deleteRecords(List<Long> filterIdList);

    /**
     * 秒杀活动：初筛结果统计
     * @param userId 用户id
     * @param userName 用户姓名
     * @param passStatus 0 -通过 1-未通过
     */
    void addRecordsByUser(Long userId, String userName, String passStatus);
}
