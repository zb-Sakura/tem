<template>
  <div>
    <div class="bg"></div>
    <div style="width: 60%; margin: 20px auto">
      <div style="padding-left: 10px; border-left: 5px solid #189500; font-size: 20px; margin-bottom: 20px">推荐旅游攻略</div>
      <div class="card">
        <div v-for="(item, index) in data.tableData" :key="item.id" style="padding: 20px; border-bottom: 1px solid #ddd">
          <a target="_blank" :href="'/front/articleDetail?id=' + item.id"><div style="font-size: 20px" class="title">{{ item.title }}</div></a>
          <div style="display: flex; grid-gap: 20px; margin-top: 10px" v-if="index % 2 === 0">
            <img :src="item.cover" alt="" style="width: 150px; border-radius: 5px">
            <div style="flex: 1; width: 0; color: #666; ">
              <div style="margin: 10px 0; height: 70px; text-align: justify" class="line3">{{ item.descr }}</div>
              <div>
                <span style="margin-right: 20px">发布日期：{{ item.date }}</span>
                <span style="margin-right: 20px">阅读量：{{ item.readCount }}</span>
              </div>
            </div>
          </div>

          <div style="display: flex; grid-gap: 20px; margin-top: 10px" v-else>
            <div style="flex: 1; width: 0; color: #666; ">
              <div style="margin: 10px 0; height: 70px; text-align: justify" class="line3">{{ item.descr }}</div>
              <div>
                <span style="margin-right: 20px">发布日期：{{ item.date }}</span>
                <span style="margin-right: 20px">阅读量：{{ item.readCount }}</span>
              </div>
            </div>
            <img :src="item.cover" alt="" style="width: 150px; border-radius: 5px">
          </div>
        </div>
      </div>

      <div style="margin: 10px 0; display: flex; justify-content: flex-end" v-if="data.total">
        <el-pagination @current-change="load" background layout="total, prev, pager, next" :page-size="data.pageSize" v-model:current-page="data.pageNum" :total="data.total" />
      </div>
    </div>

  </div>
</template>

<script setup>
import { reactive } from "vue";
import request from "@/utils/request.js";

const data = reactive({
  tableData: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
})
const load = () => {
  request.get('/article/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
    }
  }).then(res => {
    if (res.code === '200') {
      data.tableData = res.data?.list || []
      data.total = res.data?.total
    }
  })
}
load()
</script>

<style scoped>
.bg {
  height: 400px;
  background-image: url("@/assets/imgs/lygl.jpg");
  background-size: 100% 110%;
}
</style>