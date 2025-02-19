package com.luo.service;

import com.luo.pojo.Dept;

import java.util.List;

public interface IDeptService {
    boolean addDept(Dept dept);

    Dept queryById(Long id);

    List<Dept> queryAll();
}
