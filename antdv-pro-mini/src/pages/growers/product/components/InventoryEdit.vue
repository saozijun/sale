<template>
    <a-modal
      v-model:open="visible"
      title="库存修改"
      :confirm-loading="confirmLoading"
      @ok="handleOk"
      :afterClose="afterClose"
      width="700px"
    >
      <a-form ref="formRef" :model="modelRef" :rules="rules" :label-col="{style:{width: '80px'}}">
        <a-form-item label="库存数量" name="totalStock">
            <a-input-number style="width: 100%;" v-model:value="modelRef.totalStock" :min="0" :max="999999" addon-after="斤" placeholder="请输入库存数量" />
        </a-form-item>
      </a-form>
    </a-modal>
  </template>
  <script setup>
  import { ref, computed, reactive } from "vue";
  import { Form, message } from 'ant-design-vue';
  import { UploadOutlined, PlusOutlined, DeleteOutlined } from '@ant-design/icons-vue';
  import { updateStock } from '~/api/growers/inventory.js'
  import { basePlaceList } from '~/api/baseplace/index.js'
  const formRef = ref();
  const visible = ref(false);
  const inventoryId = ref(null);
  const confirmLoading = ref(false);
  const baseUrl = import.meta.env.VITE_APP_BASE_URL;
  const emits = defineEmits(["saveOk"]);
  
  const originOptions = ref([]);
  
  const modelRef = ref({
    totalStock: "",
  })
  
  const afterClose = () => {
    formRef.value.resetFields(); 
  };
  
  const rules = {
    totalStock: [{ required: true, message: '请输入库存数量', trigger: 'change' }],
  }

  const handleOk = async () => {
    try {
      await formRef.value.validate();
      let tempData = {...modelRef.value}
      tempData.inventoryId = inventoryId.value
      confirmLoading.value = true;
      await updateStock(tempData);
      message.success('操作成功');
      emits('saveOk');
      visible.value = false;
    } catch (error) {
      console.error('表单验证失败:', error);
    } finally {
      confirmLoading.value = false;
    }
  };
  
  const open = (item) => {
    console.log(item, 'item');
    
    modelRef.value.totalStock = item.totalStock;
    inventoryId.value = item.id;
    visible.value = true;
  };
  
  
  defineExpose({
    open,
  });
  </script>
  <style lang="less" scoped>
  .ant-form {
    margin-top: 20px;
  }
  :deep(.ant-upload-list-picture-card-container) {
    width: 100px;
    height: 100px;
  }
  </style>