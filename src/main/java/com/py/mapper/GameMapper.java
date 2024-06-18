package com.py.mapper;

import com.py.domain.RequestBody.AddHistory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GameMapper {

    void addHistory(AddHistory addHistory);

    List<AddHistory> getHistory(Integer userId);
}
