package com.py.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.py.domain.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("select * from sys_user where user_name = #{username}")
    User selectByUser(String username);
    @Select("select * from sys_user")
    List<User> selectList();
    @Select("select * from sys_user where email = #{email}")
    User selectByEmail(@Param("email") String email);
    Long addUser(User user);
    @Insert("insert into sys_user_role(user_id, role_id) VALUE (#{userId},#{roleId})")
    Integer addAuthentic(Long userId,Long roleId);

    @Select("select email from sys_user where id = #{userId}")
    String selectEmailById(Long userId);

    Integer updateUserPassWord(Long userId, String encode);

    Integer updatePasswordByUserName(String username, String encode);

    Integer update(User user);
    @Update("update sys_user set email = #{email} where id = #{userId}")
    Integer updateEmail(Long userId, String email);
    @Update("update sys_user set user_name = #{email} where id = #{userId}")
    Integer updateUserName(Long userId, String email);

    User selectBaseInformById(Long userId);
}
