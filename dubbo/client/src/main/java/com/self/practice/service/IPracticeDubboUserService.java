package com.self.practice.service;

import com.self.practice.dto.UserDTO;

public interface IPracticeDubboUserService {

    UserDTO getUser(int id);

    String getString(String name);
}
