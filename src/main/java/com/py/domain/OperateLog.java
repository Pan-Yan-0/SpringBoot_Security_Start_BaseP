package com.py.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("operate_log")
public class OperateLog implements Serializable {
    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 操作人Id
     */
    private Long operate_user;
    /**
     * '操作时间'
     */
    private Date operate_time;
    /**
     * '操作的类名'
     */
    private String class_name;
    /**
     * '操作的方法名'
     */
    private String method_name;
    /**
     * '方法参数'
     */
    private String method_params;
    /**
     * '返回值'
     */
    private String return_value;
    /**
     * '方法执行耗时, 单位:ms'
     */
    private Long cost_time;


}
