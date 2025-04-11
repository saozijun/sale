<script setup>
import { editPow } from '~@/api/user'
import { message } from 'ant-design-vue'
import { useUserStore } from '~@/stores/user'

const userId = useUserStore().userInfo.id

const data = computed(() => {
  return [
    {
      title: "账户密码",
      desc:  "当前密码强度: 强",
    }
  ]
})

const visible = ref(false)
const formRef = ref()
const formState = reactive({
  newPassword: '',
  confirmPassword: ''
})

const rules = {
  newPassword: [
    { required: true, message: '请输入新密码' },
    { min: 6, message: '密码长度不能小于6位' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码' },
    {
      validator: async (rule, value) => {
        if (value !== formState.newPassword) {
          throw new Error('两次输入的密码不一致')
        }
      }
    }
  ]
}

const handleOk = async () => {
  try {
    await formRef.value.validate()
    await editPow({
      id: userId,
      password: formState.newPassword
    })
    message.success('密码修改成功')
    visible.value = false
    // 重置表单
    formRef.value.resetFields()
  } catch (error) {
    message.error(error.message || '密码修改失败')
  }
}

const handleCancel = () => {
  visible.value = false
  formRef.value?.resetFields()
}
</script>

<template>
  <a-card title="安全设置" :bordered="false">
    <a-list item-layout="horizontal" :data-source="data">
      <template #renderItem="{ item }">
        <a-list-item>
          <a-list-item-meta
            :description="item.desc"
          >
            <template #title>
              <p>{{ item.title }}</p>
            </template>
          </a-list-item-meta>
          <template #actions>
            <a-button type="link" @click="visible = true">
              修改
            </a-button>
          </template>
        </a-list-item>
      </template>
    </a-list>
  </a-card>

  <a-modal
    v-model:visible="visible"
    title="修改密码"
    @ok="handleOk"
    @cancel="handleCancel"
  >
    <a-form
      ref="formRef"
      :model="formState"
      :rules="rules"
      :label-col="{ span: 6 }"
      :wrapper-col="{ span: 16 }"
    >
      <a-form-item label="新密码" name="newPassword">
        <a-input-password v-model:value="formState.newPassword" placeholder="请输入新密码" />
      </a-form-item>
      <a-form-item label="确认密码" name="confirmPassword">
        <a-input-password v-model:value="formState.confirmPassword" placeholder="请再次输入新密码" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<style scoped lang="less">
:deep(.ant-card-body) {
  padding-left: 0 !important;
}
</style>
