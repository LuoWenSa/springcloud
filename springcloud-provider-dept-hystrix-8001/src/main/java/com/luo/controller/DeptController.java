package com.luo.controller;

import com.luo.pojo.Dept;
import com.luo.service.IDeptService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

//提供restful服务
@RestController
@RequestMapping("/dept")
public class DeptController {

    @Resource
    private IDeptService deptService;

    @PostMapping("/add")
    public boolean addDept(@RequestBody Dept dept){
        return deptService.addDept(dept);
    }

    @GetMapping("/list")
    public List<Dept> queryAll(){
        return deptService.queryAll();
    }

    @GetMapping("/get/{id}")
    @HystrixCommand(fallbackMethod = "hystrixQueryById")
    public Dept queryById(@PathVariable("id") Long id){
        Dept dept = deptService.queryById(id);

        if(dept == null){
            throw new RuntimeException("id=>" + id + ",不存在该用户，或者信息无法找到~");
        }

        return dept;
    }

    //备选方案
    public Dept hystrixQueryById(@PathVariable("id") Long id){
        return new Dept()
                .setDeptNo(id)
                .setName("id=>" + id + "没有对应信息，null--@Hystrix")
                .setDbSource("no this database in MYSQL");
    }

}
