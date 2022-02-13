package com.feng.seckill.entitys.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : pcf
 * @date : 2022/1/16 16:09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "分页数据")
public class HelpPage{

    @ApiModelProperty(value = "当前页数", example = "1")
    private Long current;
    @ApiModelProperty(value = "每页最大显示数量", example = "1")
    private Long maxLimit;

}
