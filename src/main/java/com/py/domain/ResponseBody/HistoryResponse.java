package com.py.domain.ResponseBody;

import com.py.domain.Game;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Date;
import lombok.NoArgsConstructor;
/**
 * @Author PY
 * @Use 历史记录
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryResponse {
    /*
     * 主键
     * */
    private Integer Id;
    /*
     * 玩的那一个游戏
     * */
    private Game game;
    /*
     * 游戏开始的时间
     * */
    private Date StartTime;
    /*
     * 游戏耗时
     * */
    private Integer Time_Consuming;
}
