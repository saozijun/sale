<template>
  <a-modal
    v-model:open="visible"
    :title="modelRef.id ? '编辑' : '新增'"
    :confirm-loading="confirmLoading"
    @ok="handleOk"
    :afterClose="afterClose"
    width="700px"
  >
    <a-form ref="formRef" :model="modelRef" :rules="rules" :label-col="{style:{width: '80px'}}">
      <a-row :gutter="24">
        <a-col :span="24">
          <a-form-item label="名称" name="name">
            <a-input v-model:value="modelRef.name" placeholder="请输入商品名称"/>
          </a-form-item>
          <a-form-item label="上市时间" name="listedTime">
            <a-date-picker v-model:value="modelRef.listedTime" valueFormat="YYYY-MM-DD" placeholder="请输入"/>
          </a-form-item>
          <a-form-item label="产地" name="originPlaceId">
            <a-select
              v-model:value="modelRef.originPlaceId"
              placeholder="请选择产地"
              :options="originOptions"
            />
          </a-form-item>
          <a-form-item label="描述" name="description">
            <a-textarea
              v-model:value="modelRef.description"
              placeholder="请输入商品描述"
              :rows="4"
            />
          </a-form-item>
          <!-- <a-form-item label="状态">
            <a-switch v-model:checked="modelRef.status" checked-children="上架" un-checked-children="下架" :checkedValue="1" :unCheckedValue="0" />
          </a-form-item> -->
        </a-col>
        <a-col :span="24">
          <a-form-item label="封面图" name="coverImage">
            <div>
              <a-upload
                name="file"
                :action="baseUrl + '/file/upload'"
                accept="image/jpeg,image/png,image/gif"
                :show-upload-list="false"
                :before-upload="beforeUpload"
                @change="handleCoverChange"
              >
                <div v-if="modelRef.coverImage" class="img-box">
                  <div class="delete-icon">
                    <DeleteOutlined @click.stop="modelRef.coverImage = ''" style="font-size: 16px;color: #fff;"/>
                  </div>
                  <img :src="modelRef.coverImage" alt="封面图" />
                </div>
                <a-button v-else>
                  <UploadOutlined />
                  上传封面图
                </a-button>
              </a-upload>
            </div>
          </a-form-item>
          <a-form-item label="商品图片" name="images">
            <a-upload
              v-model:file-list="modelRef.images"
              accept="image/jpeg,image/png,image/gif"
              :action="baseUrl + '/file/upload'"
              list-type="picture-card"
              :before-upload="beforeUpload"
              @change="handleImagesChange"
            >
              <div>
                <PlusOutlined />
                <div style="margin-top: 8px">上传</div>
              </div>
            </a-upload>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
  </a-modal>
</template>
<script setup>
import { ref, computed, reactive } from "vue";
import { Form, message } from 'ant-design-vue';
import { UploadOutlined, PlusOutlined, DeleteOutlined } from '@ant-design/icons-vue';
import { save } from '~/api/growers/product.js'
import { basePlaceList } from '~/api/baseplace/index.js'
const formRef = ref();
const visible = ref(false);
const userId = ref(null);
const confirmLoading = ref(false);
const baseUrl = import.meta.env.VITE_APP_BASE_URL;
const emits = defineEmits(["saveOk"]);

const originOptions = ref([]);

const modelRef = ref({
  coverImage: "", // 封面图
  images: [], // 多张现货图片
  name: "", // 商品名称
  originPlaceId: null, // 产地id
  listedTime: "", // 上架时间
  description: "", // 商品描述
  // status: 0, // 状态 1上架，0下架
  userId: null, // 用户id
})

const afterClose = () => {
  formRef.value.resetFields(); 
};

const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'change' }],
  originPlaceId: [{ required: true, message: '请选择产地', trigger: 'change' }],
  description: [{ required: true, message: '请输入描述', trigger: 'change' }],
  listedTime: [{ required: true, message: '请选择预计上市时间', trigger: 'change' }],
  coverImage: [{ required: true, message: '请上传封面图', trigger: 'change' }],
  images: [{ required: true, message: '请上传商品图片', trigger: 'change' }],
}
const beforeUpload = (file) => {
  // 限制文件类型 只能上传图片
  const isJpgOrPng = file.type === "image/jpeg" || file.type === "image/png" || file.type === "image/gif";
  if (!isJpgOrPng) {
    message.error("只能上传图片文件!");
    return false;
  }
  return isJpgOrPng;
};
const handleCoverChange = (info) => {
  if (info.file.status === "done") {
    message.success(`上传成功`);
    modelRef.value.coverImage = info.file.response;
  } else if (info.file.status === "error") {
    message.error(`上传失败`);
  }
};

const handleImagesChange = (info) => {
  if (info.file.status === "done") {
    message.success(`上传成功`);
  } else if (info.file.status === "error") {
    message.error(`上传失败`);
  }
};

const handleOk = async () => {
  try {
    await formRef.value.validate();
    let tempData = {...modelRef.value}
    let tempImgs = tempData.images.map(item => {
      return {
        uid: item.uid,
        status: item.status,
        type: item.type,
        name:item.name,
        url:item.response,
        response: item.response
      }
    })
    tempData.images = JSON.stringify(tempImgs)
    tempData.userId = userId.value
    confirmLoading.value = true;
    await save(tempData);
    message.success('操作成功');
    emits('saveOk');
    visible.value = false;
  } catch (error) {
    console.error('表单验证失败:', error);
  } finally {
    confirmLoading.value = false;
  }
};

const open = (row, uId) => {
  modelRef.value = {...row};
  if(modelRef.value.images) modelRef.value.images = JSON.parse(modelRef.value.images)
  userId.value = uId;
  visible.value = true;
  getBasePlaceList()
};

const getBasePlaceList = async ()=>{
  const res = await basePlaceList()
  originOptions.value = res.data.map(item => {
    return {
      label: item.name,
      value: item.id
    }
  })
}

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
.img-box{
  width: 90px;
  cursor: pointer;
  border-radius: 4px;
  overflow: hidden;
  position: relative;
  .delete-icon{
    position: absolute;
    top: 0;
    right: 0;
    width: 100%;
    height: 100%;
    display: none;
    background-color: #00000040;
    padding: 5px 10px;
    text-align: right;
  }
  &:hover{
    .delete-icon{
      display: block;
    }
  }
  img{
    width: 100%;
  }
}
</style>