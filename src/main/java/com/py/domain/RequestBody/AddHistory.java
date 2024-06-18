package com.py.domain.RequestBody;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("history")
public class AddHistory implements Serializable {
    private static final long serialVersionUID = -40356785423868312L;
    /*
    * 主键
    * */
    @TableId
    private Integer Id;
    /*
    * 游戏Id
    * */
    private Integer GameId;
    /*
    * 用户Id
    * */
    private Integer UserId;
    /*
    * 起始时间
    * */
    private Date date;
    /*
    * 耗时（s/秒）
    * */
    private Integer time;
}
