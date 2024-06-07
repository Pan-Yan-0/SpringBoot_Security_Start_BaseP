package com.py.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.py.domain.Menu;

import java.util.List;

public interface MenuMapper extends BaseMapper<Menu> {

    List<String> selectPermsByUserId(Long userid);


}
