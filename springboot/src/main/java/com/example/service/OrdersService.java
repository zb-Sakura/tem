package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Orders;
import com.example.entity.Tourism;
import com.example.exception.CustomException;
import com.example.mapper.OrdersMapper;
import com.example.mapper.TourismMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 业务层方法
 */
@Service
public class OrdersService {

    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private TourismMapper tourismMapper;

    @Transactional
    public void add(Orders orders) {
        Account currentUser = TokenUtils.getCurrentUser();
        orders.setUserId(currentUser.getId());
        orders.setTime(DateUtil.now());
        orders.setStatus("待支付");
        orders.setOrderNo(IdUtil.getSnowflakeNextIdStr());  // 订单编号使用随机数，避免重复
        // 直接下单 之前要判断库存
        Integer tourismId = orders.getTourismId();
        Tourism tourism = tourismMapper.selectById(tourismId);
        if (tourism == null) {
            throw new CustomException("500", "商品不存在");
        }
        if (tourism.getStore() < orders.getNum()) {  // 商品数量不足
            throw new CustomException("500", "库存不足");
        }
        ordersMapper.insert(orders);
        tourism.setStore(tourism.getStore() - orders.getNum());
        tourismMapper.updateById(tourism);
    }

    public void updateAccountById(Orders orders) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            Integer tourismId = orders.getTourismId();
            Tourism tourism = tourismMapper.selectById(tourismId);
            if (tourism != null) {
                tourism.setStore(tourism.getStore() + orders.getNum());
                tourismMapper.updateById(tourism);
            }
        }
        ordersMapper.updateById(orders);
    }

    public void updateById(Orders orders) {
        ordersMapper.updateById(orders);
    }

    public void deleteById(Integer id) {
        ordersMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            ordersMapper.deleteById(id);
        }
    }

    public Orders selectById(Integer id) {
        return ordersMapper.selectById(id);
    }

    public List<Orders> selectAll(Orders orders) {
        return ordersMapper.selectAll(orders);
    }

    public PageInfo<Orders> selectPage(Orders orders, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (currentUser.getRole().equals(RoleEnum.USER.name())) {
            orders.setUserId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Orders> list = ordersMapper.selectAll(orders);
        return PageInfo.of(list);
    }

    public Orders selectByOrderNo(String orderNo) {
        return ordersMapper.selectByOrderNo(orderNo);
    }
}
