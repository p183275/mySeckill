package com.feng.seckill.entitys.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author : pcf
 * @date : 2022/2/14 20:50
 */
@Data
@AllArgsConstructor
@ApiModel(value = "逾期配置条件")
public class OverdueConfig {

    @ApiModelProperty(value = "逾期年限", example = "3")
    private Integer overdueYear;
    @ApiModelProperty(value = "逾期次数", example = "3")
    private Integer overdueTimes;
    @ApiModelProperty(value = "逾期最小金额", example = "3.0")
    private BigDecimal overdueMinMoney;
    @ApiModelProperty(value = "逾期天数", example = "3")
    private Integer overdueDay;

    public OverdueConfig(){
        this.overdueYear = 3;
        this.overdueTimes = 2;
        this.overdueMinMoney = new BigDecimal(1000);
        this.overdueDay = 3;
    }

}
