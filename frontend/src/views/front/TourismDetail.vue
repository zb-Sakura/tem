<template>
  <div style="width: 60%; margin: 10px auto">
    <div style="display: flex; grid-gap: 20px">
      <div style="flex: 1">
        <img :src="data.tourism.img" alt="" style="width: 100%; height: 300px">
      </div>

      <div style="flex: 1">
        <div style="font-size: 20px">{{ data.tourism.name }}</div>
        <div style="margin: 10px 0">
          <el-tag style="margin-right: 10px" v-for="item in JSON.parse(data.tourism.specials || '[]')">{{ item }}</el-tag>
        </div>
        <div style="margin: 10px 0; color: #666; font-size: 13px">{{ data.tourism.descr }}</div>
        <div style="margin: 10px 0; background-color: #eee; padding: 15px; display: flex; align-items: baseline">
          <span style="font-size: 24px; font-weight: bold; color: orangered; margin-right: 30px">￥{{ data.tourism.price }}</span>
          <span style="color: #666">库存：{{ data.tourism.store }}</span>
        </div>
        <div style="margin: 10px 0; color: #666; font-size: 13px">
          预订须知：支付完成后商家最晚会在2个工作小时内（工作日9:00-18:00）确认是否预定成功
        </div>
        <div>
          <el-button @click="addOrder" style="padding: 20px 30px; background-color: orange; border-color: orange; color: #fff">立即购买</el-button>
        </div>
      </div>
    </div>

    <div style="margin: 20px 0">
      <div style="padding-left: 10px; border-left: 5px solid #189500; font-size: 20px; margin: 20px 0">图文介绍</div>
      <div>
        <div v-html="data.tourism.content"></div>
      </div>
    </div>

    <el-dialog title="确认订单" v-model="data.formVisible" width="40%" destroy-on-close>
      <el-form label-width="70px" style="padding: 20px">
        <el-form-item label="商品名称">{{data.tourism.name }}</el-form-item>
        <el-form-item label="商品单价">{{data.tourism.price }}</el-form-item>
        <el-form-item label="购买数量">
          <el-input-number :min="1" :max="10" style="width: 150px" v-model="data.form.num" @change="changeNum"></el-input-number>
        </el-form-item>
        <el-form-item label="商品总价"><span style="color: red; font-weight: bold; font-size: 16px">￥{{ data.form.total }}</span></el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="data.formVisible = false">取 消</el-button>
          <el-button type="primary" @click="saveOrder">确认下单</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive} from "vue";
import request from "@/utils/request.js";
import router from "@/router/index.js";
import {ElMessage} from "element-plus";

const data = reactive({
  id: router.currentRoute.value.query.id,
  tourism: {},
  formVisible: false,
  form: {}  // 表示订单的对象
})

const changeNum = () => {
  data.form.total = data.tourism.price * data.form.num
}

const saveOrder = () => {
  request.post('/orders/add', data.form).then(res => {
    if (res.code === '200') {
      ElMessage.success('下单成功')
      router.push('/front/orders')
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const addOrder = () => {
  data.formVisible = true
  data.form.tourismId = data.tourism.id
  data.form.tourismImg = data.tourism.img
  data.form.name = data.tourism.name
  data.form.tourismPrice = data.tourism.price
  data.form.num = 1
  data.form.total = data.tourism.price
}

const load = () => {
  request.get('/tourism/selectById/' + data.id).then(res => {
    if (res.code === '200') {
      data.tourism = res.data
    }
  })
}
load()
</script>

<style scoped>
:deep(.el-form-item__label) {
  font-weight: bold !important;
}
:deep(.el-form-item) {
  margin-bottom: 10px;
}
</style>