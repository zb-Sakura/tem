package com.example.mapper;

import com.example.entity.Orders;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OrdersMapper {

    int insert(Orders orders);

    void updateById(Orders orders);

    void deleteById(Integer id);

    Orders selectById(Integer id);

    List<Orders> selectAll(Orders orders);

    @Select("select * from orders where order_no = #{orderNo}")
    Orders selectByOrderNo(String orderNo);
}
