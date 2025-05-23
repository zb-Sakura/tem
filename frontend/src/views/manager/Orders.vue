<template>
  <div>
    <div class="card" style="margin-bottom: 5px">
      <el-input v-model="data.orderNo" prefix-icon="Search" style="width: 240px; margin-right: 10px" placeholder="请输入订单编号查询"></el-input>
      <el-button type="info" plain @click="load">查询</el-button>
      <el-button type="warning" plain style="margin: 0 10px" @click="reset">重置</el-button>
    </div>
    <div class="card" style="margin-bottom: 5px">
      <el-button type="danger" plain @click="delBatch">批量删除</el-button>
    </div>

    <div class="card" style="margin-bottom: 5px">
      <el-table stripe :data="data.tableData" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="orderNo" label="订单编号" width="150"></el-table-column>
        <el-table-column prop="name" label="商品名称" show-overflow-tooltip></el-table-column>
        <el-table-column prop="tourismImg" label="商品图片">
          <template #default="scope">
            <el-image style="width: 50px; height: 50px; border-radius: 5px" :src="scope.row.tourismImg" :preview-src-list="[scope.row.img]" preview-teleported></el-image>
          </template>
        </el-table-column>
        <el-table-column prop="tourismPrice" label="商品价格"></el-table-column>
        <el-table-column prop="tourismId" label="商品ID"></el-table-column>
        <el-table-column prop="num" label="购买数量"></el-table-column>
        <el-table-column prop="total" label="商品总价">
          <template #default="scope">
            <span style="color: orangered; font-weight: bold">￥{{scope.row.total }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="下单人"></el-table-column>
        <el-table-column prop="time" label="下单时间" width="150"></el-table-column>
        <el-table-column prop="status" label="订单状态">
          <template #default="scope">
            <el-tag type="danger" v-if="scope.row.status === '已取消'">已取消</el-tag>
            <el-tag type="primary" v-if="scope.row.status === '待支付'">待支付</el-tag>
            <el-tag type="info" v-if="scope.row.status === '待发货'">待发货</el-tag>
            <el-tag type="success" v-if="scope.row.status === '已完成'">已完成</el-tag>
            <el-tag type="danger" v-if="scope.row.status === '已退款'">已退款</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payNo" label="支付单号"  width="150"></el-table-column>
        <el-table-column prop="payTime" label="支付时间"  width="150"></el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template v-slot="scope">
            <el-button type="primary" @click="changeStatus(scope.row)" v-if="scope.row.status === '待发货'">发货</el-button>
            <el-button type="danger" circle :icon="Delete" @click="del(scope.row.id)"></el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="card" v-if="data.total">
      <el-pagination @current-change="load" background layout="prev, pager, next" :page-size="data.pageSize" v-model:current-page="data.pageNum" :total="data.total" />
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
  let form = JSON.parse(JSON.stringify(row))  // 防止表格数据被修改
  form.status = '已完成'
  request.put('/orders/update', form).then(res => {
    if (res.code === '200') {
      ElMessage.success('操作成功')
      data.formVisible = false
      load()
    } else {
      ElMessage.error(res.msg)
    }
  })
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