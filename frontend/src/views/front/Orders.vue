<template>
  <div style="width: 90%; margin: 10px auto; padding: 20px" class="card">
    <div style="margin-bottom: 10px">
      <el-input v-model="data.orderNo" prefix-icon="Search" style="width: 280px; margin-right: 10px" placeholder="请输入订单编号查询"></el-input>
      <el-button type="info" plain @click="load">查询</el-button>
      <el-button type="warning" plain style="margin: 0 10px" @click="reset">重置</el-button>
    </div>

    <div style="margin-bottom: 10px">
      <el-table stripe :data="data.tableData" @selection-change="handleSelectionChange">
        <el-table-column prop="orderNo" label="订单编号" width="150"></el-table-column>
        <el-table-column prop="name" label="商品名称" width="200" show-overflow-tooltip>
          <template #default="scope">
            <a target="_blank" style="color: #1890ff; text-decoration: underline" :href="'/front/tourismDetail?id=' + scope.row.tourismId">{{ scope.row.name }}</a>
          </template>
        </el-table-column>
        <el-table-column prop="tourismImg" label="商品图片">
          <template #default="scope">
            <el-image style="width: 50px; height: 50px; border-radius: 5px" :src="scope.row.tourismImg" :preview-src-list="[scope.row.img]" preview-teleported></el-image>
          </template>
        </el-table-column>
        <el-table-column prop="tourismPrice" label="商品价格"></el-table-column>
        <el-table-column prop="num" label="购买数量"></el-table-column>
        <el-table-column prop="total" label="商品总价">
          <template #default="scope">
            <span style="color: orangered; font-weight: bold">￥{{scope.row.total }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="time" label="下单时间"  width="150"></el-table-column>
        <el-table-column prop="status" label="订单状态">
          <template #default="scope">
            <el-tag type="danger" v-if="scope.row.status === '已取消'">已取消</el-tag>
            <el-tag type="primary" v-if="scope.row.status === '待支付'">待支付</el-tag>
            <el-tag type="info" v-if="scope.row.status === '待发货'">待发货</el-tag>
            <el-tag type="success" v-if="scope.row.status === '已完成'">已完成</el-tag>
            <el-tag type="danger" v-if="scope.row.status === '已退款'">已退款</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payNo" label="支付单号" width="150"></el-table-column>
        <el-table-column prop="payTime" label="支付时间" width="150"></el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template v-slot="scope">
            <el-button type="primary" @click="pay(scope.row)" v-if="scope.row.status === '待支付'">支付</el-button>
            <el-button type="danger" @click="refund(scope.row)" v-if="scope.row.status === '待发货'">退款</el-button>
            <el-button type="danger" @click="changeStatus(scope.row)" v-if="scope.row.status === '待支付'">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div v-if="data.total">
      <el-pagination @current-change="load" background layout="total, prev, pager, next" :page-size="data.pageSize" v-model:current-page="data.pageNum" :total="data.total" />
    </div>
  </div>
</template>

<script setup>

import {reactive} from "vue";
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";
import {Delete, Edit} from "@element-plus/icons-vue";


const data = reactive({
  formVisible: false,
  form: {},
  tableData: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
  title: null,
  ids: []
})

const changeStatus = (row) => {
  ElMessageBox.confirm('取消订单后无法恢复，您确认取消订单吗？', '取消订单', { type: 'warning' }).then(res => {
    let form = JSON.parse(JSON.stringify(row))  // 防止表格数据被修改
    form.status = '已取消'
    request.put('/orders/update', form).then(res => {
      if (res.code === '200') {
        ElMessage.success('操作成功')
        data.formVisible = false
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(err => {})
}

const refund = (row) => {
  ElMessageBox.confirm('退款后无法恢复，您确认退款吗？', '退款确认', { type: 'warning' }).then(res => {
    request.put('/alipay/refund?orderNo=' + row.orderNo).then(res => {
      if (res.code === '200') {
        ElMessage.success('操作成功')
        data.formVisible = false
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(err => {})
}

const pay = (row) => {
  let baseUrl = import.meta.env.VITE_BASE_URL
  window.open(baseUrl + '/alipay/pay?orderNo=' + row.orderNo)
}

const load = () => {
  request.get('/orders/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      orderNo: data.orderNo
    }
  }).then(res => {
    if (res.code === '200') {
      data.tableData = res.data?.list || []
      data.total = res.data?.total
    }
  })
}

const update = () => {
  request.put('/orders/update', data.form).then(res => {
    if (res.code === '200') {
      ElMessage.success('操作成功')
      data.formVisible = false
      load()
    }
  })
}

const del = (id) => {
  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗？', '删除确认', { type: 'warning' }).then(res => {
    request.delete('/orders/delete/' + id).then(res => {
      if (res.code === '200') {
        ElMessage.success("删除成功")
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(err => {
    console.error(err)
  })
}
const delBatch = () => {
  if (!data.ids.length) {
    ElMessage.warning("请选择数据")
    return
  }
  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗？', '删除确认', { type: 'warning' }).then(res => {
    request.delete("/orders/delete/batch", {data: data.ids}).then(res => {
      if (res.code === '200') {
        ElMessage.success('操作成功')
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(err => {
    console.error(err)
  })
}
const handleSelectionChange = (rows) => {
  data.ids = rows.map(v => v.id)
}

const reset = () => {
  data.orderNo = null
  load()
}

load()
</script>