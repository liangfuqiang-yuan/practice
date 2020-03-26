package com.self.practice.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class RpcRequest implements Serializable{

    private static final long serialVersionUID = 2860069648075646700L;
    private String className;

    private String methodName;

    private Class<?>[] types;

    private Object[] params;
}
