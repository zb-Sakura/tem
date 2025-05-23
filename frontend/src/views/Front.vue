<template>
  <div>
    <div class="front-notice">
      <div style="padding-left: 40px; flex: 1">
        <i style="font-size: 20px; color: #189500">上旅游网，带你看不一样的世界！</i>
      </div>
      <iframe scrolling="no" src="https://widget.tianqiapi.com/?style=tg&skin=pitaya" frameborder="0" width="470" height="40" allowtransparency="true"></iframe>
    </div>
    <div class="front-header">
      <a href="/front/home">
        <div class="front-header-left">
          <img src="@/assets/imgs/logo.png" alt="">
          <div class="title">旅游网</div>
        </div>
      </a>
      <div class="front-header-center">
        <el-menu :default-active="router.currentRoute.value.path" router mode="horizontal">
          <el-menu-item index="/front/home">首页</el-menu-item>
          <el-menu-item index="/front/article">旅游攻略</el-menu-item>
          <el-menu-item index="/front/routes">旅游路线</el-menu-item>
          <el-menu-item index="/front/tourism">去旅游</el-menu-item>
          <el-menu-item index="/front/feedback">问题反馈</el-menu-item>
        </el-menu>
      </div>
      <div style="width: 400px">
        <el-input style="width: 300px; margin-right: 5px" v-model="data.search" placeholder="请输入目的地搜索"></el-input>
        <el-button type="primary" @click="goPage('/front/search?name=' + data.search)">搜索</el-button>
      </div>
      <div class="front-header-right">
        <div v-if="!data.user.id">
          <el-button @click="router.push('/login')">登录</el-button>
          <el-button @click="router.push('/register')">注册</el-button>
        </div>
        <div v-else>
          <el-dropdown style="cursor: pointer; height: 60px">
            <div style="display: flex; align-items: center">
              <img style="width: 40px; height: 40px; border-radius: 50%;" :src="data.user.avatar" alt="">
              <span style="margin-left: 5px;">{{ data.user.name }}</span><el-icon><arrow-down /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="goPage('/front/orders')">我的订单</el-dropdown-item>
                <el-dropdown-item @click="goPage('/front/myFeedback')">我的反馈</el-dropdown-item>
                <el-dropdown-item @click="goPage('/front/travels')">我的游记</el-dropdown-item>
                <el-dropdown-item @click="goPage('/front/collect')">我的收藏</el-dropdown-item>
                <el-dropdown-item @click="goPage('/front/person')">个人信息</el-dropdown-item>
                <el-dropdown-item @click="goPage('/front/password')">修改密码</el-dropdown-item>
                <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>
    <div class="main-body">
      <RouterView @updateUser="updateUser" />
    </div>
    <Footer />
  </div>
</template>

<script setup>
  import router from "@/router/index.js";
  import { reactive } from "vue";
  import request from "@/utils/request.js";
  import Footer from '@/components/Footer.vue'

  const data = reactive({
    user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
    top: '',
    noticeData: [],
    search: ''
  })

  const goPage = (path) => {
    location.href = path
  }

  const logout = () => {
    localStorage.removeItem('xm-user')
    router.push('/login')
  }

  const updateUser = () => {
    data.user =  JSON.parse(localStorage.getItem('xm-user') || '{}')
  }

  const loadNotice = () => {
    request.get('/notice/selectAll').then(res => {
      data.noticeData = res.data
      let i = 0
      if (data.noticeData && data.noticeData.length) {
        data.top = data.noticeData[0].content
        setInterval(() => {
          data.top = data.noticeData[i].content
          i++
          if (i === data.noticeData.length) {
            i = 0
          }
        }, 2500)
      }
    })
  }
  loadNotice()
</script>

<style scoped>
@import "@/assets/css/front.css";
</style>