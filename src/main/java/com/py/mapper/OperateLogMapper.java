package com.py.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.py.domain.OperateLog;

public interface OperateLogMapper extends BaseMapper<OperateLog> {

    void add(OperateLog operateLog);
}
