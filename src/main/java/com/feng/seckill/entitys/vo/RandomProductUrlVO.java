package com.feng.seckill.entitys.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author : pcf
 * @date : 2022/1/16 17:56
 */
@Data
@ApiModel(value = "随机生成活动链接封装类")
@AllArgsConstructor
public class RandomProductUrlVO {

    @ApiModelProperty(value = "产品id", example = "1")
    private Long productId;
    @ApiModelProperty(value = "随机连接的长度", example = "20到200之间")
    private Integer length = 50;

}
