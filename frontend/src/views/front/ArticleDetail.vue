<template>
  <div style="width: 60%; margin: 10px auto">
    <div style="font-size: 24px; font-weight: bold; margin: 20px 0; text-align: center">{{ data.article.title }}</div>
    <div style="color: #666; display: flex; align-items: center; justify-content: center">
      <span style="margin-right: 20px"><el-icon size="16" style="top: 2px"><Calendar /></el-icon> {{ data.article.date }}</span>
      <span style="margin-right: 20px"><el-icon size="16" style="top: 2px"><View /></el-icon> {{ data.article.readCount }}</span>
      <div style="color: orange; font-weight: bold; display: flex; align-items: center; grid-gap: 2px">
        <span style="font-size: 20px">{{ data.article.collectCount }}</span>
        <el-button @click="collect" size="small" style="background-color: orange; padding: 5px 10px; color: white; border-color: orange">收藏</el-button>
      </div>
    </div>
    <div style="padding: 20px 0" class="content">
      <div v-html="data.article.content"></div>
    </div>

    <div>
      <Comment :module="'article'"  />
    </div>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import router from "@/router/index.js";
import request from "@/utils/request.js";
import Comment from "@/components/Comment.vue";
import {ElMessage} from "element-plus";

const data = reactive({
  id: router.currentRoute.value.query.id,
  article: {}
})

const collect = () => {
  request.post('/collect/add', { fid: data.id }).then(res => {
    if (res.code === '200') {
      ElMessage.success('收藏成功')
      load()  // 重新获取当前的最新的攻略信息
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const load = () => {
  request.get('/article/selectById/' + data.id).then(res => {
    data.article = res.data
  })
}
load()
</script>