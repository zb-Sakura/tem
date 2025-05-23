<template>
  <div>
    <div class="bg"></div>
    <div style="width: 60%; margin: 20px auto">
      <div style="padding-left: 10px; border-left: 5px solid #189500; font-size: 20px; margin-bottom: 20px">推荐旅游路线</div>
      <el-row :gutter="20">
       <el-col :span="8" v-for="item in data.routesList" :key="item.id" style="margin-bottom: 20px">
         <a :href="'/front/routesDetail?id=' + item.id">
           <div>
            <img :src="item.img" alt="" style="width: 100%; height: 400px">
            <div style="height: 50px; text-align: justify" class="line2 title">{{ item.name }}</div>
          </div>
         </a>
       </el-col>
      </el-row>
      <div style="margin: 10px 0" v-if="data.total">
        <el-pagination size="small" @current-change="load" background layout="total, prev, pager, next" :page-size="data.pageSize" v-model:current-page="data.pageNum" :total="data.total" />
      </div>
    </div>
  </div>

</template>

<script setup>
import { reactive} from "vue";
import request from "@/utils/request.js";

const data = reactive({
  routesList: [],
  pageNum: 1,
  pageSize: 6,
  total: 0,
})

const load = () => {
  request.get('/routes/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      name: data.name
    }
  }).then(res => {
    if (res.code === '200') {
      data.routesList = res.data?.list || []
      data.total = res.data?.total
    }
  })
}
load()
</script>

<style scoped>
.bg {
  height: 400px;
  background-image: url("@/assets/imgs/lxbg.jpg");
  background-size: 100% 110%;
}
</style>