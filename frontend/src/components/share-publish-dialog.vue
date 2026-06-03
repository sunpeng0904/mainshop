<template>
  <el-dialog
    v-model="visible"
    title="发布分享"
    width="600px"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    @close="handleClose"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
      <!-- 文本内容 -->
      <el-form-item prop="content">
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="4"
          placeholder="分享你的想法..."
          maxlength="1000"
          show-word-limit
        />
      </el-form-item>

      <!-- 图片上传 -->
      <el-form-item label="图片">
        <el-upload
          ref="uploadRef"
          :file-list="fileList"
          :auto-upload="false"
          list-type="picture-card"
          :limit="9"
          accept="image/*"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          :before-upload="beforeUpload"
        >
          <el-icon><Plus /></el-icon>
          <template #tip>
            <div class="upload-tip">最多上传9张图片，单张不超过10MB</div>
          </template>
        </el-upload>
      </el-form-item>

      <!-- 位置信息 -->
      <el-form-item label="所在位置">
        <el-input
          v-model="form.location"
          placeholder="添加位置（选填）"
          maxlength="100"
          clearable
        >
          <template #prefix>
            <el-icon><Location /></el-icon>
          </template>
        </el-input>
      </el-form-item>

      <!-- 可见范围 -->
      <el-form-item label="可见范围">
        <el-select v-model="form.visibility" placeholder="选择可见范围">
          <el-option :label="'所有人可见'" :value="1">
            <el-icon><Unlock /></el-icon>
            <span>所有人可见</span>
          </el-option>
          <el-option :label="'好友可见'" :value="2">
            <el-icon><User /></el-icon>
            <span>好友可见</span>
          </el-option>
          <el-option :label="'仅自己可见'" :value="5">
            <el-icon><Lock /></el-icon>
            <span>仅自己可见</span>
          </el-option>
        </el-select>
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <div class="footer-left">
          <el-button text @click="handleSaveDraft">
            <el-icon><Document /></el-icon> 存草稿
          </el-button>
        </div>
        <div class="footer-right">
          <el-button @click="visible = false">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">
            发布
          </el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, computed } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { createShare, uploadShareImage } from '@/api/share'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'published'])

const store = useStore()
const formRef = ref(null)
const uploadRef = ref(null)
const submitting = ref(false)
const fileList = ref([])
const uploadedUrls = ref([])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const form = reactive({
  content: '',
  location: '',
  visibility: 1
})

const rules = {
  content: [
    { required: true, message: '请输入分享内容', trigger: 'blur' },
    { max: 1000, message: '内容最多1000字', trigger: 'blur' }
  ]
}

// 监听草稿恢复
watch(visible, (val) => {
  if (val) {
    const draft = store.state.share.draft
    if (draft) {
      form.content = draft.content || ''
      form.location = draft.location || ''
      form.visibility = draft.visibility || 1
    }
  }
})

// 文件变化
const handleFileChange = (file, files) => {
  fileList.value = files
}

// 文件移除
const handleFileRemove = (file, files) => {
  fileList.value = files
}

// 上传前校验
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过10MB')
    return false
  }
  return true
}

// 上传图片
const uploadImages = async () => {
  if (fileList.value.length === 0) return []

  const urls = []
  for (const fileItem of fileList.value) {
    if (fileItem.raw) {
      try {
        const res = await uploadShareImage(fileItem.raw)
        urls.push(res.data.url)
      } catch (error) {
        ElMessage.error('图片上传失败')
        throw error
      }
    } else if (fileItem.url) {
      urls.push(fileItem.url)
    }
  }
  return urls
}

// 提交发布
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }

  submitting.value = true
  try {
    // 先上传图片
    const imageUrls = await uploadImages()

    // 创建分享
    await createShare({
      content: form.content,
      imageUrls,
      location: form.location || undefined,
      visibility: form.visibility
    })

    ElMessage.success('发布成功')
    visible.value = false
    store.dispatch('share/clearDraft')
    emit('published')
    resetForm()
  } catch (error) {
    // 错误已在拦截器处理
  } finally {
    submitting.value = false
  }
}

// 保存草稿
const handleSaveDraft = () => {
  store.dispatch('share/saveDraft', {
    content: form.content,
    location: form.location,
    visibility: form.visibility
  })
  ElMessage.success('草稿已保存')
  visible.value = false
}

// 关闭对话框
const handleClose = () => {
  // 如果有内容，提示是否保存草稿
  if (form.content.trim() || fileList.value.length > 0) {
    store.dispatch('share/saveDraft', {
      content: form.content,
      location: form.location,
      visibility: form.visibility
    })
  }
}

// 重置表单
const resetForm = () => {
  form.content = ''
  form.location = ''
  form.visibility = 1
  fileList.value = []
  uploadedUrls.value = []
}
</script>

<style lang="scss" scoped>
.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.dialog-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .footer-left {
    .el-button {
      color: #909399;
    }
  }

  .footer-right {
    display: flex;
    gap: 8px;
  }
}

:deep(.el-upload--picture-card) {
  width: 100px;
  height: 100px;
}

:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 100px;
  height: 100px;
}
</style>
