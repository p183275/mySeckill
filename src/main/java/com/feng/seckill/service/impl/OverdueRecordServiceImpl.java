package com.feng.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feng.seckill.entitys.po.OverdueRecordPO;
import com.feng.seckill.mapper.OverdueRecordMapper;
import com.feng.seckill.service.OverdueRecordService;
import org.springframework.stereotype.Service;

/**
 * @author : pcf
 * @date : 2022/2/14 21:00
 */
@Service
public class OverdueRecordServiceImpl extends ServiceImpl<OverdueRecordMapper, OverdueRecordPO>
        implements OverdueRecordService {
}
