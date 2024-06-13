package com.py.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.ArrayList;
/**
 * @Author PY
 * @Use 游戏类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("Game")
public class Game {
    /*
    * 主键
    * */
    @Id
    private int Id;
    /*
    * 游戏格子类型
    * */
    private ArrayList<ArrayList<Integer>> Types;
    /*
    * 格子内的值,假如格子类型为 3 ,即是两个部分都有, 下前边，右后边（4545）下边的值乘以100加上右边的值
    * */
    private ArrayList<ArrayList<Integer>> Values;
    /*
    * 游戏格子类型为 4 才会有答案
    * 答案表
    * */

    private ArrayList<ArrayList<Integer>> Answer;
    /*
    * 游戏的难度
    * */
    private Integer Difficulty;
}
