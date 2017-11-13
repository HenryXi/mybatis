package com.henryxi.cache.mapper;

import com.henryxi.cache.entity.User;

public interface UserMapper {
    User selectByIdWithCache(Integer id);

    User selectByIdWithoutCache(Integer id);
}