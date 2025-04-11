<template>
  <div class="box" v-if="!showInventory">
    <a-card mb-4>
      <a-form :model="formModel">
        <a-row :gutter="[15, 0]">
          <a-col>
            <a-form-item name="name" label="名称">
              <a-input v-model:value="formModel.name" placeholder="请输入" />
            </a-form-item>
          </a-col>
          <a-col>
            <a-space flex justify-end w-full>
              <a-button :loading="loading" type="primary" @click="onSearch">
                查询
              </a-button>
              <a-button :loading="loading" @click="onReset">
                重置
              </a-button>
              <a-button  type="primary" @click="open">
                <template #icon>
                  <PlusOutlined />
                </template>
                新增
              </a-button>
            </a-space>
          </a-col>
        </a-row>
      </a-form>
    </a-card>
    <a-table :columns="columns" :data-source="tableData" :pagination="false">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'coverImage'">
          <a-image :width="80" :src="record.coverImage"/>
        </template>
        <template v-else-if="column.key === 'status'">
          <a-switch v-model:checked="record.status" checked-children="上架" un-checked-children="下架" :checkedValue="1" :unCheckedValue="0" @change="updateStatus(record)" />
        </template>
        <template v-else-if="column.key === 'operation'">
          <a-button style="padding: 0;" type="link" @click="showInventory = true; productId = record.id">库存管理</a-button>
          <a-button type="link" @click="open(record)">编辑</a-button>
          <a-popconfirm title="确定删除吗?" @confirm="onDelete(record.id)">
            <a-button type="link" style="padding: 0;" danger>删除</a-button>
          </a-popconfirm>
        </template>
      </template>
    </a-table>
    <div class="pagination">
      <a-pagination v-model:current="formModel.pageNum" :total="total" @change="onPageChange" />
    </div>
    <Edit ref="editRef" @saveOk="getList"></Edit>
  </div>
  <Inventory v-else ref="inventoryRef" @backOk="showInventory = false; productId = null" :productId="productId"></Inventory>
</template>
<script setup>
import { PlusOutlined } from '@ant-design/icons-vue';
import { ref, onMounted } from 'vue';
import { message } from 'ant-design-vue';
import { list, del, save, publish } from '~/api/growers/productP.js'
import { useUserStore } from '~@/stores/user'
import Edit from './components/Edit.vue';
import Inventory from './components/Inventory.vue';
const userStore = useUserStore()
const showInventory = ref(false);
const editRef = ref(null)
const expand = ref(false)
const loading = ref(false)
const tableData = ref([])
const productId = ref(null)
const total = ref(0)
const formModel = ref({
    pageNum: 1,
    pageSize: 10,
    userId: userStore.userInfo.id,
    levelName: "",
})

onMounted(() => {
  getList()
})

const onPageChange = (page) => {
  getList()
}
const onSearch = () => {
  getList()
}
const onReset = () => {
  formModel.value = {}
  getList()
}

const updateStatus = async (record) => {
  try {
    const { data } = await publish(record.id, record.status)
    if (data.code === 200) {
      message.success('修改成功')
    }
  } catch (error) {
    record.status = !record.status
    console.log(error)
  }
}

const getList = async () => {
  loading.value = true
  try {
    const { data } = await list(formModel.value)
    total.value = data.total
    data.records.map((item,i) => {item.index = i + 1})
    tableData.value = data.records
  } catch (error) {
    console.log(error)
  } finally {
    loading.value = false
  }
}

const onDelete = async (id) => {
  try {
    await del({id})
    getList()
    message.success('删除成功');
  } catch (error) {
    console.log(error)
  }
}

const open = (record = {}) => {
  editRef.value.open(record, userStore.userInfo.id)
}
const columns = [
  {
    title: '序号',
    dataIndex: 'index',
    key: 'index',
    width: 80,
  },
  {
    title: '封面图',
    dataIndex: 'coverImage',
    key: 'coverImage',
  },
  {
    title: '商品名称',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: '预计上市时间',
    dataIndex: 'listedTime',
    key: 'listedTime',
  },
  {
    title: '描述',
    dataIndex: 'description',
    key: 'description',
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
  },
  {
    title: '操作',
    key: 'operation',
    fixed: 'right',
    width: 250,
  },
];
</script>

<style lang="less" scoped>
.box {
  height: calc(100vh - 170px);
}

:deep( .ant-form-item ) {
  margin-bottom: 0;
}
.pagination{
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>