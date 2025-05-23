package com.example.controller;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import com.example.common.Result;
import com.example.common.enums.RoleEnum;
import com.example.entity.*;
import com.example.mapper.ArticleMapper;
import com.example.mapper.OrdersMapper;
import com.example.mapper.TourismMapper;
import com.example.mapper.TravelsMapper;
import com.example.service.AdminService;
import com.example.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class WebController {

    @Resource
    private AdminService adminService;

    @Resource
    private UserService userService;

    @Resource
    private TravelsMapper travelsMapper;

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private TourismMapper tourismMapper;

    @Resource
    private OrdersMapper ordersMapper;

    /**
     * 默认请求接口
     */
    @GetMapping("/")
    public Result hello () {
        return Result.success();
    }

    /**
     * 登录--已保存到接口文档
     */
    @PostMapping("/login")
    public Result login(@RequestBody Account account) {
        Account loginAccount = null;
        if (RoleEnum.ADMIN.name().equals(account.getRole())) {
            loginAccount = adminService.login(account);
        } else if (RoleEnum.USER.name().equals(account.getRole())) {
            loginAccount = userService.login(account);
        }
        return Result.success(loginAccount);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        userService.add(user);
        return Result.success();
    }

    /**
     * 修改密码--已保存到接口文档
     */
    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Account account) {
        if (RoleEnum.ADMIN.name().equals(account.getRole())) {
            adminService.updatePassword(account);
        } else if (RoleEnum.USER.name().equals(account.getRole())) {
            userService.updatePassword(account);
        }
        return Result.success();
    }

    @GetMapping("/count")
    public Result count() {
        long travelsCount = travelsMapper.selectAll(null).size();
        long articleCount = articleMapper.selectAll(null).size();
        long tourismCount = tourismMapper.selectAll(null).size();
        long ordersCount = ordersMapper.selectAll(null).size();
        Dict dict = Dict.create().set("travelsCount", travelsCount)
                .set("articleCount", articleCount)
                .set("tourismCount", tourismCount)
                .set("ordersCount", ordersCount);
        return Result.success(dict);
    }

    @GetMapping("/ordersData")
    public Result selectOrdersData() {
        List<Dict> dictList = new ArrayList<>();
        Date date = new Date();
        DateTime start = DateUtil.offsetDay(date, -31);
        DateTime end = DateUtil.offsetDay(date, -1);
        // 最近一个月的字符串日期集合 ["2024-09-25", "2024-09-26", ...]
        List<String> dateList = DateUtil.rangeToList(start, end, DateField.DAY_OF_YEAR).stream().map(DateUtil::formatDate)
                .sorted(Comparator.naturalOrder()).toList();
        List<Orders> ordersList = ordersMapper.selectAll(null);
        for (String day : dateList) {
            // 计算出每一天的销售数量
            Integer ordersNum = ordersList.stream().filter(orders -> orders.getTime().contains(day))  // 这里的日期是订单的下单日期
                    .map(Orders::getNum).reduce(Integer::sum).orElse(0);
            Dict dict = Dict.create().set("name", day).set("value", ordersNum);
            dictList.add(dict);
        }
        System.out.println(dictList);
        return Result.success(dictList);
    }

    @GetMapping("/travelsData")
    public Result selectTravelsData() {
        List<Dict> dictList = new ArrayList<>();
        Date date = new Date();
        DateTime start = DateUtil.offsetDay(date, -31);
        DateTime end = DateUtil.offsetDay(date, -1);
        List<String> dateList = DateUtil.rangeToList(start, end, DateField.DAY_OF_YEAR).stream().map(DateUtil::formatDate)
                .sorted(Comparator.naturalOrder()).toList();
        List<Travels> travelsList = travelsMapper.selectAll(null);
        for (String day : dateList) {
            long count = travelsList.stream().filter(travels -> travels.getTime().contains(day)).count();
            Dict dict = Dict.create().set("name", day).set("value", count);
            dictList.add(dict);
        }
        System.out.println(dictList);
        return Result.success(dictList);
    }

}
