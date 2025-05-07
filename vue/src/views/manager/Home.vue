<template>
  <div>
    <div style="display: flex; align-items: center; grid-gap: 15px">
      <div class="card item">
        <div style="flex: 1; text-align: center">
          <img src="@/assets/imgs/游记.png" alt="" style="width: 60px; height: 60px">
        </div>
        <div style="flex: 1; font-size: 20px">
          <div style="margin-bottom: 5px">游记总数</div>
          <div style="font-weight: bold">{{ data.count.travelsCount }}</div>
        </div>
      </div>
      <div class="card item">
        <div style="flex: 1; text-align: center">
          <img src="@/assets/imgs/攻略.png" alt="" style="width: 60px; height: 60px">
        </div>
        <div style="flex: 1; font-size: 20px">
          <div style="margin-bottom: 5px">攻略总数</div>
          <div style="font-weight: bold">{{ data.count.articleCount }}</div>
        </div>
      </div>
      <div class="card item">
        <div style="flex: 1; text-align: center">
          <img src="@/assets/imgs/商品.png" alt="" style="width: 60px; height: 60px">
        </div>
        <div style="flex: 1; font-size: 20px">
          <div style="margin-bottom: 5px">商品总数</div>
          <div style="font-weight: bold">{{ data.count.tourismCount }}</div>
        </div>
      </div>
      <div class="card item">
        <div style="flex: 1; text-align: center">
          <img src="@/assets/imgs/订单.png" alt="" style="width: 60px; height: 60px">
        </div>
        <div style="flex: 1; font-size: 20px">
          <div style="margin-bottom: 5px">订单总数</div>
          <div style="font-weight: bold">{{ data.count.ordersCount }}</div>
        </div>
      </div>
    </div>

    <div style="margin: 15px 0; display: flex; align-items: center; grid-gap: 15px">
      <div style="flex: 1; padding: 20px" class="card">
        <div id="line" style="height: 450px"></div>
      </div>
      <div style="flex: 1; padding: 20px" class="card">
        <div id="line1" style="height: 450px"></div>
      </div>
    </div>

  </div>
</template>

<script setup>

import {reactive, onMounted} from "vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import * as echarts from 'echarts'

const lineOption = {
  title: {
    text: '订单销量趋势图',
    subtext: '折线图',
    left: 'center'
  },
  tooltip: {
    trigger: 'axis'
  },
  legend: {
    left: 'left'
  },
  xAxis: {
    type: 'category',
    data: []
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      data: [],
      type: 'line',
      smooth: true,
    }
  ]
}

const lineOption1 = {
  title: {
    text: '游记发布数量趋势图',
    subtext: '折线图',
    left: 'center'
  },
  tooltip: {
    trigger: 'axis'
  },
  legend: {
    left: 'left'
  },
  xAxis: {
    type: 'category',
    data: []
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      data: [],
      type: 'line',
      smooth: true,
    }
  ]
}

const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
  count: {}
})

request.get('/count').then(res => {
  data.count = res.data
})

onMounted(() => {
  // 页面所有html元素加载好之后才会触发这里的代码
  let line = document.getElementById('line');
  let lineChart = echarts.init(line);
  request.get('/ordersData').then(res => {
    lineOption.xAxis.data = res.data.map(v => v.name)
    lineOption.series[0].data = res.data.map(v => v.value)
    lineChart.setOption(lineOption)
  })

  let line1 = document.getElementById('line1');
  let lineChart1 = echarts.init(line1);
  request.get('/travelsData').then(res => {
    lineOption1.xAxis.data = res.data.map(v => v.name)
    lineOption1.series[0].data = res.data.map(v => v.value)
    lineChart1.setOption(lineOption1)
  })
})

</script>

<style scoped>
.item {
  flex: 1;
  display: flex;
  align-items: center;
}
</style>