package com.luo.service;

import com.luo.pojo.Dept;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(value = "SPRINGCLOUD-PROVIDER-DEPT", fallbackFactory = DeptClientServiceFallbackFactory.class)
@RequestMapping("/dept")
public interface DeptClientService {
    @PostMapping("/add")
    boolean addDept(Dept dept);

    @GetMapping("/get/{id}")
    Dept queryById(@PathVariable("id") Long id);

    @GetMapping("/list")
    List<Dept> queryAll();
}
