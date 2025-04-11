<template>
  <a-modal v-model:open="visible" :title="modelRef.id ? '编辑' : '新增'" :confirm-loading="confirmLoading" @ok="handleOk"
    :afterClose="afterClose" width="700px">
    <a-form ref="formRef" :model="modelRef" :rules="rules" :label-col="{ style: { width: '80px' } }">
      <a-form-item label="产品等级" name="priceLevelId">
        <a-select v-model:value="modelRef.priceLevelId" placeholder="请选择产品等级" >
          <a-select-option v-for="(item) in levelOptions" :value="item.id">{{item.levelName}} <span style="color: #999;font-size: 12px;">{{ item.description }}</span></a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="产品单价" name="price">
        <a-input-number style="width: 100%;" v-model:value="modelRef.price" addon-after="￥" placeholder="请输入产品单价" />
      </a-form-item>
      <a-form-item label="总库存" name="totalStock" v-if="!modelRef.id">
        <a-input-number style="width: 100%;" v-model:value="modelRef.totalStock" :min="0" :max="999999" addon-after="斤"
          placeholder="请输入总库存" />
      </a-form-item>
      <!-- <a-form-item label="阶梯折扣" name="ladderDiscount">
        <a-form-list name="ladderDiscount">
          <template v-for="(field, index) in modelRef.ladderDiscount" :key="index">
            <a-form-item>
              <div style="display: flex; gap: 10px;">
                <a-form-item 
                  :name="['ladderDiscount', index, 'amount']" 
                  :rules="[
                    { required: true, message: '请输入数量', trigger: 'change' }
                  ]"
                  noStyle
                >
                  <a-input-number v-model:value="field.amount" placeholder="数量" addon-after="斤" style="width: 200px" :min="0" />
                </a-form-item>
                <a-form-item 
                  :name="['ladderDiscount', index, 'discount']" 
                  :rules="[
                    { required: true, message: '请输入折扣', trigger: 'change' },
                    { type: 'number', min: 0, max: 1, message: '折扣必须在0到1之间', trigger: 'change' }
                  ]"
                  noStyle
                >
                  <a-input-number 
                    v-model:value="field.discount" 
                    placeholder="折扣" 
                    addon-after="折" 
                    style="width: 200px" 
                    :min="0" 
                    :max="1" 
                    :step="0.01"
                  />
                </a-form-item>
                <DeleteOutlined @click="() => removeDiscountRule(index)" style="color: #ff4d4f; cursor: pointer" />
              </div>
            </a-form-item>
          </template>
          <a-form-item>
            <a-button type="dashed" block @click="addDiscountRule">
              <PlusOutlined /> 添加折扣规则
            </a-button>
          </a-form-item>
        </a-form-list>
      </a-form-item> -->
    </a-form>
  </a-modal>
</template>
<script setup>
import { ref, computed, reactive, onMounted } from "vue";
import { Form, message } from 'ant-design-vue';
import { UploadOutlined, PlusOutlined, DeleteOutlined } from '@ant-design/icons-vue';
import { save } from '~/api/growers/inventory.js'
const formRef = ref();
const levelOptions = ref([])
const visible = ref(false);
const productId = ref(null);
const confirmLoading = ref(false);
const baseUrl = import.meta.env.VITE_APP_BASE_URL;
const emits = defineEmits(["saveOk"]);

const originOptions = ref([]);

const modelRef = ref({
  priceLevelId: "", // 产品等级id
  price: "", // 产品单价
  totalStock: "", // 总库存
  ladderDiscount: [], // 阶梯折扣规则
})

const afterClose = () => {
  formRef.value.resetFields();
};

const rules = {
  priceLevelId: [{ required: true, message: '请选择产品等级', trigger: 'change' }],
  price: [{ required: true, message: '请输入单价', trigger: 'change' }],
  totalStock: [{ required: true, message: '请输入总库存', trigger: 'change' }]
}

const addDiscountRule = () => {
  const newRule = {
    key: Date.now(),
    amount: undefined,
    discount: undefined
  };
  modelRef.value.ladderDiscount.push(newRule);
};

const removeDiscountRule = (index) => {
  modelRef.value.ladderDiscount.splice(index, 1);
};

const handleOk = async () => {
  try {
    // 验证所有折扣规则
    const rules = modelRef.value.ladderDiscount;
    if (rules.length > 0) {
      // 检查是否有未填写的字段
      const hasEmptyFields = rules.some(rule => !rule.amount || !rule.discount);
      if (hasEmptyFields) {
        message.error('请填写完整的折扣规则');
        return;
      }

      // 检查数量是否按从小到大排序
      const sortedRules = [...rules].sort((a, b) => a.amount - b.amount);
      const isSorted = rules.every((rule, index) => rule.amount === sortedRules[index].amount);
      if (!isSorted) {
        message.error('折扣规则必须按数量从小到大排序');
        return;
      }
    }

    await formRef.value.validate();
    let tempData = { 
      ...modelRef.value,
      ladderDiscount: JSON.stringify(modelRef.value.ladderDiscount.map(rule => ({
        amount: rule.amount,
        discount: rule.discount
      })))
    };
    confirmLoading.value = true;
    tempData.productId = productId.value;
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

const open = (row, pId, options) => {
  modelRef.value = { 
    ...row,
    ladderDiscount: row.ladderDiscount ? JSON.parse(row.ladderDiscount).map((rule, index) => ({
      key: index,
      amount: rule.amount,
      discount: rule.discount
    })) : []
  };
  productId.value = pId;
  levelOptions.value = options;
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

.img-box {
  width: 90px;
  cursor: pointer;
  border-radius: 4px;
  overflow: hidden;
  position: relative;

  .delete-icon {
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

  &:hover {
    .delete-icon {
      display: block;
    }
  }

  img {
    width: 100%;
  }
}
</style>