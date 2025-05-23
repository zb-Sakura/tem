<template>
  <div style="width: 50%; margin: 10px auto">
    <div style="text-align: right">
      <el-button @click="router.back()"> << 返回</el-button>
    </div>
    <div style="margin: 20px 0; text-align: center; font-size: 24px">写游记</div>
    <div>
      <el-form ref="form" :model="data.form" label-width="90px" style="padding: 20px" :rules="data.rules">
        <el-form-item prop="title" label="标题">
          <el-input v-model="data.form.title" placeholder="请输入标题"></el-input>
        </el-form-item>
        <el-form-item label="封面">
          <el-upload
              :action="baseUrl + '/files/upload'"
              :headers="{ 'token': data.user.token }"
              list-type="picture"
              :on-success="handleFileUpload"
          >
            <el-button type="primary">上传图片</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item prop="descr" label="简介">
          <el-input :rows="3" type="textarea" v-model="data.form.descr" placeholder="请输入简介"></el-input>
        </el-form-item>
        <el-form-item prop="startDate" label="出行日期">
          <el-date-picker style="width: 100%" type="datetime" v-model="data.form.startDate" format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DD HH:mm" placeholder="请选择出行日期"></el-date-picker>
        </el-form-item>
        <el-form-item label="费用" prop="money">
          <el-input-number style="width: 200px" v-model="data.form.money" placeholder="请输入费用" :min="1"></el-input-number>
        </el-form-item>
        <el-form-item prop="location" label="出行地点">
          <el-input v-model="data.form.location" placeholder="请输入出行地点"></el-input>
        </el-form-item>
        <el-form-item prop="days" label="出行天数">
          <el-input-number style="width: 200px" v-model="data.form.days" placeholder="请输入出行天数"></el-input-number>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <div style="border: 1px solid #ccc; width: 100%">
            <Toolbar
                style="border-bottom: 1px solid #ccc"
                :editor="editorRef"
                :mode="mode"
            />
            <Editor
                style="height: 500px; overflow-y: hidden;"
                v-model="data.form.content"
                :mode="mode"
                :defaultConfig="editorConfig"
                @onCreated="handleCreated"
            />
          </div>
        </el-form-item>
        <div style="text-align: center">
          <el-button type="primary" style="padding: 20px 40px" @click="save">发布游记</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import router from "@/router/index.js";
import { reactive } from "vue";
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import {onBeforeUnmount, ref, shallowRef} from "vue";
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";

const data = reactive({
  id: router.currentRoute.value.query.id,
  user: JSON.parse(localStorage.getItem('xm-user')),
  form: {},
  rules: {
    title: [
      { required: true, message: '请输入标题', trigger: 'blur' }
    ],
    descr: [
      { required: true, message: '请输入简介', trigger: 'blur' }
    ],
    startDate: [
      { required: true, message: '请选择出行日期', trigger: 'blur' }
    ],
    money: [
      { required: true, message: '请输入花费金额', trigger: 'blur' }
    ],
    location: [
      { required: true, message: '请输入出行地点', trigger: 'blur' }
    ],
    days: [
      { required: true, message: '请输入出行天数', trigger: 'blur' }
    ],
  }
})
const baseUrl = import.meta.env.VITE_BASE_URL
const form = ref()

if (data.id) {
  request.get('/travels/selectById/' + data.id).then(res => {
    data.form = res.data
  })
}

const save = () => {
  if (data.id) {
    update()
  } else {
    add()
  }
}

const add = () => {
  form.value.validate((valid) => {
    if (valid) {  // 表单校验通过
      if (!data.form.content) {
        ElMessage.warning('请输入内容')
        return
      }
      request.post('/travels/add', data.form).then(res => {
        if (res.code === '200') {
          ElMessage.success('发布成功')
          if (res.data.id) {
            router.replace('/front/addTravel?id=' + res.data.id)
          }
        } else {
          ElMessage.error(res.msg)
        }
      })
    }
  })
}

const update = () => {
  form.value.validate((valid) => {
    if (valid) {  // 表单校验通过
      if (!data.form.content) {
        ElMessage.warning('请输入内容')
        return
      }
      request.put('/travels/update', data.form).then(res => {
        if (res.code === '200') {
          ElMessage.success('发布成功')
        } else {
          ElMessage.error(res.msg)
        }
      })
    }
  })
}

const handleFileUpload = (res) => {
  data.form.cover = res.data
}

/* wangEditor5 初始化开始 */
const editorRef = shallowRef()  // 编辑器实例，必须用 shallowRef
const mode = 'default'
const editorConfig = { MENU_CONF: {} }
// 图片上传配置
editorConfig.MENU_CONF['uploadImage'] = {
  headers: {
    token: data.user.token,
  },
  server: baseUrl + '/files/wang/upload',  // 服务端图片上传接口
  fieldName: 'file'  // 服务端图片上传接口参数
}
// 组件销毁时，也及时销毁编辑器，否则可能会造成内存泄漏
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})
// 记录 editor 实例，重要！
const handleCreated = (editor) => {
  editorRef.value = editor
}
/* wangEditor5 初始化结束 */
</script>