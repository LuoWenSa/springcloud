package com.luo.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Dept implements Serializable { //Dept 实体类 orm 类表关系映射
    private Long deptNo;
    private String name;

    //这个字段存在哪个数据库的字段~微服务， 一个服务对应一个数据库，同一个信息可能存在不同的数据库
    private String dbSource;

    public Dept(String name){
        this.name = name;
    }

}
