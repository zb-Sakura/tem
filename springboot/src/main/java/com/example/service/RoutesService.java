package com.example.service;

import com.example.entity.Routes;
import com.example.mapper.RoutesMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 业务层方法
 */
@Service
public class RoutesService {

    @Resource
    private RoutesMapper routesMapper;

    public Integer add(Routes routes) {
        routesMapper.insert(routes);
        return routes.getId();
    }

    public void updateById(Routes routes) {
        routesMapper.updateById(routes);
    }

    public void deleteById(Integer id) {
        routesMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            routesMapper.deleteById(id);
        }
    }

    public Routes selectById(Integer id) {
        return routesMapper.selectById(id);
    }

    public List<Routes> selectAll(Routes routes) {
        return routesMapper.selectAll(routes);
    }

    public PageInfo<Routes> selectPage(Routes routes, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Routes> list = routesMapper.selectAll(routes);
        return PageInfo.of(list);
    }

}
