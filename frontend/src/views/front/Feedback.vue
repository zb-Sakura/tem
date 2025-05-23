<template>
  <div style="width: 40%; margin: 10px auto">
    <div style="font-size: 24px; text-align: center; margin: 20px 0">😊期待您的声音</div>
    <el-form ref="formRef" :model="data.form" label-width="80px" style="margin: 30px 0" :rules="data.rules">
      <el-form-item prop="title" label="反馈标题">
        <el-input v-model="data.form.title" placeholder="请输入反馈标题"></el-input>
      </el-form-item>
      <el-form-item prop="content" label="反馈内容">
        <el-input type="textarea" :rows="5" v-model="data.form.content" placeholder="请输入反馈内容"></el-input>
      </el-form-item>
      <div style="text-align: center">
        <el-button type="primary" style="padding: 15px 30px" @click="add">立即发布</el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";

const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
  form: {},
  rules: {
    title: [
      {required: true, message: '请输入标题', trigger: 'blur'}
    ],
    content: [
      {required: true, message: '请输入内容', trigger: 'blur'}
    ]
  }
})

const formRef = ref()

const add = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      request.post('/feedback/add', data.form).then(res => {
        if (res.code === '200') {
          ElMessage.success('操作成功')
          data.form = {}
        } else {
          ElMessage.error(res.msg)
        }
      })
    }
  })
}
</script>

<style>

</style>