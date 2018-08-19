package com.henryxi.mapper;

import com.henryxi.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {
    @Select("SELECT * FROM users WHERE id = #{userId}")
    User getUser(@Param("userId") int userId);

    @Insert("INSERT INTO users (id, name) VALUES (#{userId},#{userName}) ")
    void save(@Param("userId") int userId, @Param("userName") String userName);
}
