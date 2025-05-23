<template>
  <div style="width: 60%; margin: 10px auto">
    <div style="margin: 20px 0; font-size: 24px; font-weight: bold; text-align: center">{{ data.routes.name }}</div>
    <div style="margin: 10px 0">
      <span style="margin-right: 20px">旅游地点：<strong>{{ data.routes.location }}</strong></span>
      <span>旅游天数：<strong>{{ data.routes.days }}天</strong></span>
    </div>
    <div style="margin: 10px 0; padding: 10px; background-color: #eee; border-radius: 5px">
      <strong>小提示：</strong>{{ data.routes.tips }}
    </div>
    <div style="margin: 20px 0; text-align: center">
      <img :src="data.routes.img" alt="" style="width: 50%">
    </div>
    <div style="margin: 20px 0; line-height: 24px; text-align: justify">
      <div v-html="data.routes.content"></div>
    </div>
    <div style="margin: 20px 0">
      <div style="width: 100%; height: 500px" id="container"></div>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted } from "vue";
import request from "@/utils/request.js";
import router from "@/router/index.js";

const data = reactive({
  id: router.currentRoute.value.query.id,
  routes: {}
})


onMounted(() => {
  request.get('/routes/selectById/' + data.id).then(res => {
    data.routes = res.data

    let map = new BMapGL.Map('container'); // 创建Map实例
    map.centerAndZoom(new BMapGL.Point(data.routes.longitude, data.routes.latitude), 13); // 初始化地图,设置中心点坐标和地图级别
    map.enableScrollWheelZoom(true); // 开启鼠标滚轮缩放

    // 创建标注
    let point = new BMapGL.Point(data.routes.longitude, data.routes.latitude)
    let marker = new BMapGL.Marker(point);
    map.addOverlay(marker);
    let opts = {
      width : 300,     // 信息窗口宽度
      height: 100,     // 信息窗口高度
      title : '景点坐标' , // 信息窗口标题
    }
    let location = data.routes.location
    let infoWindow = new BMapGL.InfoWindow(
        "<b>位置</b>：" + location + "<br/>",
        opts);  // 创建信息窗口对象

    marker.addEventListener("click", function(){
      map.openInfoWindow(infoWindow,point); //开启信息窗口
    })
  })
})
</script>