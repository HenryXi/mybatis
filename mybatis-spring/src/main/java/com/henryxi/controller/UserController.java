package com.henryxi.controller;

import com.henryxi.entity.User;
import com.henryxi.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @RequestMapping("/get")
    @ResponseBody
    public User get(@RequestParam("id") int id) {
        User user = userMapper.getUser(id);
        return user;
    }

    @ResponseBody
    @RequestMapping("/save")
    public String save(@RequestParam("id") int id, @RequestParam("name") String name) {
        userMapper.save(id, name);
        return "succ";
    }
}
