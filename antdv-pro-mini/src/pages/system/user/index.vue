<template>
  <div class="box">
    <a-card mb-4>
      <a-form :model="formModel">
        <a-row :gutter="[15, 0]">
          <a-col>
            <a-form-item name="nickname" label="名称">
              <a-input v-model:value="formModel.nickname" placeholder="请输入名称" />
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
        <template v-if="column.key === 'status'">
          <span v-if="record.id !== 1">
            <a-switch v-model:checked="record.status" checked-children="启用" un-checked-children="禁用" :checkedValue="1" :unCheckedValue="0" @change="updateStatus(record)" />
          </span>
          <span v-else>
            <a-tag color="blue">超级管理员</a-tag>
          </span>
        </template>
        <template v-if="column.key === 'role'">
          <span>
            <a-tag  color="blue"> {{ record.role }}</a-tag>
          </span>
        </template>
        <template v-else-if="column.key === 'operation'">
          <a-button style="padding: 0;" type="link" @click="open(record)" v-if="record.id !== 1">编辑</a-button>
          <a-popconfirm title="确定删除吗?" @confirm="onDelete(record.id)">
            <a-button type="link" danger v-if="record.id !== 1">删除</a-button>
          </a-popconfirm>
        </template>
      </template>
    </a-table>
    <div class="pagination">
      <a-pagination v-model:current="formModel.pageNum" :total="total" @change="onPageChange" />
    </div>
    <Edit ref="editRef" @saveOk="getList"></Edit>
  </div>
</template>
<script setup>
import { PlusOutlined } from '@ant-design/icons-vue';
import { ref, onMounted } from 'vue';
import { message } from 'ant-design-vue';
import { list, del, save } from '~/api/user'
import Edit from './components/Edit.vue';
const editRef = ref(null)
const expand = ref(false)
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const formModel = ref({
    pageNum: 1,
    pageSize: 10,
    nickname: "",
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

const updateStatus = async (record) => {
  try {
    const { data } = await save({
      id: record.id,
      status: record.status
    })
    if (data.code === 200) {
      message.success('修改成功')
    }
  } catch (error) {
    console.log(error)
  }
}

const open = (record = {}) => {
  editRef.value.open(record)
}
const columns = [
  {
    title: '序号',
    dataIndex: 'index',
    key: 'index',
    width: 80,
  },
  {
    title: '昵称',
    dataIndex: 'nickname',
    key: 'nickname',
  },
  {
    title: '账号',
    dataIndex: 'username',
    key: 'username',
  },
  {
    title: '联系方式',
    dataIndex: 'phone',
    key: 'phone',
  },
  {
    title: '角色',
    dataIndex: 'role',
    key: 'role',
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
    width: 200,
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