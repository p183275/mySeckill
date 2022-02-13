package com.feng.seckill.entitys.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author : pcf
 * @date : 2022/1/20 21:27
 */
@TableName(value = "t_seckill_result_first_filter")
@Data
@ApiModel(value = "初筛结果封装类")
public class FirstFilterPO {

    @ApiModelProperty(value = "主键", example = "1")
    @TableId
    private Long filterId;
    @ApiModelProperty(value = "用户id", example = "1")
    private Long userId;
    @ApiModelProperty(value = "用户姓名", example = "杰克")
    private String userName;
    @ApiModelProperty(value = "0-未通过 1-通过", example = "0")
    private String passStatus;
    @ApiModelProperty(value = "创建时间", example = "2022-01-17 16:05:18")
    private Date createDate;

    public FirstFilterPO(Long userId, String userName, String passStatus){
        this.userId = userId;
        this.userName = userName;
        this.passStatus = passStatus;
    }

}
