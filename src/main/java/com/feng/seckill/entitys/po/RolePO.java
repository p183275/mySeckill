package com.feng.seckill.entitys.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author : pcf
 * @date : 2022/1/23 21:37
 */
@TableName(value = "t_role")
@Data
public class RolePO {

    @TableId
    private Long roleId; // id
    private String roleName; // 角色名称
    private String content; // 说明
    private Date createTime; // 创建时间

}
