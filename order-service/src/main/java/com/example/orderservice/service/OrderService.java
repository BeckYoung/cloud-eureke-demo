package com.example.orderservice.service;

import com.example.orderservice.controller.ResponseResult;
import com.example.orderservice.mapper.OrderMapper;
import com.example.orderservice.pojo.Order;
import com.example.orderservice.pojo.User;
import com.google.common.reflect.TypeToken;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Optional;

@Service
public class OrderService {
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private RestTemplate restTemplate;

    public Order queryOrderById(Long orderId) {
        // 1.查询订单
        Order order = orderMapper.findById(orderId);
        // 无法做负载均衡
        //String url = "http://localhost:8080/users/" + order.getUserId();
        // 2.用RestTemplate远程调用
        String url = "http://userservice/users/" + order.getUserId();
        // 2.2.发送http请求，实现远程调用
        ParameterizedTypeReference<ResponseResult<User>> typeReference = new ParameterizedTypeReference<ResponseResult<User>>() {};
        ResponseEntity<ResponseResult<User>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, typeReference);
        System.out.println("OrderService statusCode:" + responseEntity.getStatusCode());
        ResponseResult<User> result = responseEntity.getBody();
        Optional.ofNullable(result).map(res -> res.getData()).ifPresent(user -> order.setUser(user));
//        User user = result.getData();
//        if (user !=null) {
//            order.setUser(user);
//        }
        // 4.返回
        return order;
    }
}
