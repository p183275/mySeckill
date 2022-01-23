package com.feng.seckill.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.feng.seckill.entitys.po.FirstFilterPO;
import com.feng.seckill.entitys.po.SeckillResultPO;
import com.feng.seckill.entitys.vo.HelpPage;

import java.util.List;

/**
 * @author : pcf
 * @date : 2022/1/20 21:51
 */
public interface SeckillResultService extends IService<SeckillResultPO> {

    /**
     * 分页查询加检索
     * @param key 关键字
     * @param productId 产品id
     * @param helpPage 分页属性
     * @return 分页数据
     */
    Page<SeckillResultPO> queryPage(String key, Long productId, HelpPage helpPage);

    /**
     * 删除
     * @param resultIdList 主键
     */
    void deleteRecords(List<Long> resultIdList);

}
