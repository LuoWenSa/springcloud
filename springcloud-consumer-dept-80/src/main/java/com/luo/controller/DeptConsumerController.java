package com.luo.controller;

import com.luo.pojo.Dept;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/consumer/dept")
public class DeptConsumerController {

    @Resource
    private RestTemplate restTemplate;

    //private static final String REST_URL_PREFIX = "http://localhost:8001/dept";
    //Ribbon。由于eureka是集群，我们这里的地址，应该是一个变量，通过服务名来访问（SPRINGCLOUD-PROVIDER-DEPT）
    private static final String REST_URL_PREFIX = "http://SPRINGCLOUD-PROVIDER-DEPT/dept";

    @GetMapping("/get/{id}")
    public Dept get(@PathVariable("id") Long id){
        return restTemplate.getForObject(REST_URL_PREFIX+"/get/"+id,Dept.class);
    }

    @PostMapping("/add")
    public boolean add(Dept dept){
        return Boolean.TRUE.equals(restTemplate.postForObject(REST_URL_PREFIX + "/add", dept, Boolean.class));
    }

    @GetMapping("/list")
    public List list(){
        return restTemplate.getForObject(REST_URL_PREFIX+"/list", List.class);
    }
}
