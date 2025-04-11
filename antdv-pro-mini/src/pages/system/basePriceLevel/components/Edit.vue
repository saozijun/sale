<template>
  <a-modal
    v-model:open="visible"
    :title="modelRef.id ? '编辑' : '新增'"
    :confirm-loading="confirmLoading"
    @ok="handleOk"
    :afterClose="afterClose"
  >
    <a-form name="form" :label-col="{style:{width: '80px'}}">
      <a-form-item label="等级名称" v-bind="validateInfos.levelName" >
        <a-input v-model:value="modelRef.levelName" placeholder="请输入"/>
      </a-form-item>
      <a-form-item label="等级说明" v-bind="validateInfos.description" >
        <a-textarea v-model:value="modelRef.description" allow-clear placeholder="请输入"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>
<script setup>
import { ref } from "vue";
import { Form, message } from 'ant-design-vue';
import { save } from '~/api/basePriceLevel'
const useForm = Form.useForm;
const visible = ref(false);
const confirmLoading = ref(false);
const emits = defineEmits(["saveOk"]);
const modelRef = ref({
  levelName: "",
  description: "",
  userId: null
})

const afterClose = () => {
  resetFields(); 
};

const { resetFields, validate, validateInfos } = useForm(
  modelRef,
  reactive({
    levelName: [{ required: true, message: '请输入等级名称'}]
  }),
);
const handleOk = async () => {
  await validate();
  confirmLoading.value = true;
  await save(modelRef.value);
  message.success('操作成功');
  emits('saveOk');
  visible.value = false;
  confirmLoading.value = false;
};
const open = (row, uid) => {
  modelRef.value = JSON.parse(JSON.stringify(row));
  modelRef.value.userId = uid;
  visible.value = true;
};
defineExpose({
  open,
});
</script>
<style lang="less" scoped>
.ant-form{
  margin-top: 20px;
}
</style>