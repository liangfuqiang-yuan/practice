package com.self.practice.service.impl;

import com.self.practice.annotation.RpcAnnotation;
import com.self.practice.dto.UserDTO;
import com.self.practice.service.IPracticeDubboUserService;

@RpcAnnotation(IPracticeDubboUserService.class)
public class PracticeDubboUserServiceImpl implements IPracticeDubboUserService {
    @Override
    public UserDTO getUser(int id) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(id);
        userDTO.setAge(29);
        userDTO.setUserName("Dubbo");
        System.out.println("user:" + userDTO);
        return userDTO;
    }

    @Override
    public String getString(String name) {
        String ss = "Hello ! " + name;
        System.out.println("msg = " + ss);
        return ss;
    }
}
