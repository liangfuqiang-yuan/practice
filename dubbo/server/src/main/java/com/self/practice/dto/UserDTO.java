package com.self.practice.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDTO implements Serializable{

    private int id;

    private String userName;

    private int age;
}
