package com.example.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.example.common.Result;
import com.example.common.config.AliPayConfig;
import com.example.entity.Orders;
import com.example.exception.CustomException;
import com.example.service.OrdersService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/alipay")
public class AliPayController {

    // 支付宝沙箱网关地址
    private static final String GATEWAY_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private static final String FORMAT = "JSON";
    private static final String CHARSET = "UTF-8";
    //签名方式
    private static final String SIGN_TYPE = "RSA2";

    @Resource
    private AliPayConfig aliPayConfig;

    @Resource
    private OrdersService ordersService;

    @GetMapping("/pay")  //  /alipay/pay?orderNo=xxx
    public void pay(String orderNo, HttpServletResponse httpResponse) throws Exception {
        // 查询订单信息
        Orders orders = ordersService.selectByOrderNo(orderNo);
        if (orders == null) {
            return;
        }
        // 1. 创建Client，通用SDK提供的Client，负责调用支付宝的API
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL, aliPayConfig.getAppId(),
                aliPayConfig.getAppPrivateKey(), FORMAT, CHARSET, aliPayConfig.getAlipayPublicKey(), SIGN_TYPE);

        // 2. 创建 Request并设置Request参数
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();  // 发送请求的 Request类
        request.setNotifyUrl(aliPayConfig.getNotifyUrl());
        JSONObject bizContent = new JSONObject();
        bizContent.set("out_trade_no", orders.getOrderNo());  // 我们自己生成的订单编号
        bizContent.set("total_amount", orders.getTotal()); // 订单的总金额
        bizContent.set("subject", orders.getName());   // 支付的名称
        bizContent.set("product_code", "FAST_INSTANT_TRADE_PAY");  // 固定配置
        request.setBizContent(bizContent.toString());
        request.setReturnUrl("http://127.0.0.1:5173/front/orders"); // 支付完成后自动跳转到本地页面的路径
        // 执行请求，拿到响应的结果，返回给浏览器
        String form = "";
        try {
            form = alipayClient.pageExecute(request).getBody(); // 调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        httpResponse.setContentType("text/html;charset=" + CHARSET);
        httpResponse.getWriter().write(form);// 直接将完整的表单html输出到页面
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
    }

    @PostMapping("/notify")  // 注意这里必须是POST接口
    public void payNotify(HttpServletRequest request) throws Exception {
        if (request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
            System.out.println("=========支付宝异步回调========");

            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, request.getParameter(name));
            }

            String sign = params.get("sign");
            String content = AlipaySignature.getSignCheckContentV1(params);
            boolean checkSignature = AlipaySignature.rsa256CheckContent(content, sign, aliPayConfig.getAlipayPublicKey(), "UTF-8"); // 验证签名
            // 支付宝验签
            if (checkSignature) {
                // 验签通过
                System.out.println("交易名称: " + params.get("subject"));
                System.out.println("交易状态: " + params.get("trade_status"));
                System.out.println("支付宝交易凭证号: " + params.get("trade_no"));
                System.out.println("商户订单号: " + params.get("out_trade_no"));
                System.out.println("交易金额: " + params.get("total_amount"));
                System.out.println("买家在支付宝唯一id: " + params.get("buyer_id"));
                System.out.println("买家付款时间: " + params.get("gmt_payment"));
                System.out.println("买家付款金额: " + params.get("buyer_pay_amount"));


                String tradeNo = params.get("out_trade_no");
                String gmtPayment = params.get("gmt_payment");
                String alipayTradeNo = params.get("trade_no");
                // 更新订单状态为已支付，设置支付信息
                Orders orders = ordersService.selectByOrderNo(tradeNo);
                orders.setStatus("待发货");
                orders.setPayTime(gmtPayment);
                orders.setPayNo(alipayTradeNo);
                ordersService.updateById(orders);
            }
        }
    }

    /**
     * 退款接口
     */
    @PutMapping("/refund")
    public Result refund(String orderNo) {
        Orders orders = ordersService.selectByOrderNo(orderNo);
        if (ObjectUtil.isNull(orders)) {
            throw new CustomException("500", "未找到订单");
        }
        // 1. 创建Client，通用SDK提供的Client，负责调用支付宝的API
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL, aliPayConfig.getAppId(),
                aliPayConfig.getAppPrivateKey(), FORMAT, CHARSET, aliPayConfig.getAlipayPublicKey(), SIGN_TYPE);

        // 2. 创建 Request并设置Request参数
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        request.setNotifyUrl(aliPayConfig.getNotifyUrl());
        JSONObject bizContent = new JSONObject();
        bizContent.set("out_trade_no", orders.getOrderNo());  // 我们自己生成的订单编号  必须是不重复的退款订单号
        bizContent.set("refund_amount", orders.getTotal()); // 订单的总金额
        bizContent.set("trade_no", orders.getPayNo()); // 支付宝支付订单号
        bizContent.set("out_request_no", IdUtil.fastSimpleUUID());   // 随机数
        request.setBizContent(bizContent.toString());
        try {
            // 退款调用接口
            AlipayTradeRefundResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                System.out.println("订单号" + orderNo + "退款成功");
            }
            Orders dbOrder = ordersService.selectByOrderNo(orderNo);
            dbOrder.setStatus("已退款");
            ordersService.updateById(dbOrder);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            System.err.println("退款失败");
        }

        return Result.success();
    }

}