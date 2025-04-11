<template>
    <div class="box">
        <a-card mb-4>
            <div style="display: flex; justify-content: space-between; align-items: center;">
                <div>
                    <a-button shape="circle" :icon="h(ArrowLeftOutlined)" @click="back" />
                    <a-button shape="circle"  style="margin:0 10px;" :icon="h(UndoOutlined)" @click="onSearch" />
                    <span>库存管理</span>
                </div>
                <a-button type="primary" @click="open">
                    <template #icon>
                        <PlusOutlined />
                    </template>
                    新增库存
                </a-button>
            </div>
        </a-card>
        <a-table :columns="columns" :data-source="tableData" :pagination="false">
            <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'priceLevel'">
                    <a-tag>{{ getLevelName(record.priceLevelId) }}</a-tag>
                </template>
                <template v-else-if="column.key === 'operation'">
                    <a-button type="link" style="padding: 0;" @click="iyEdit(record)">修改库存</a-button>
                    <a-button type="link"  @click="open(record)">编辑</a-button>
                    <a-popconfirm title="确定删除吗?" @confirm="onDelete(record.id)">
                        <a-button type="link" style="padding: 0;" danger>删除</a-button>
                    </a-popconfirm>
                </template>
            </template>
        </a-table>
        <div class="pagination">
            <a-pagination v-model:current="formModel.pageNum" :total="total" @change="onPageChange" />
        </div>
        <InventoryEdit ref="inventoryEditRef" @saveOk="getList"></InventoryEdit>
        <Iedit ref="editRef" @saveOk="getList"></Iedit>
    </div>
</template>
<script setup>
import { PlusOutlined, ArrowLeftOutlined, UndoOutlined } from '@ant-design/icons-vue';
import { ref, onMounted, h } from 'vue';
import { message } from 'ant-design-vue';
import { list, del } from '~/api/growers/inventoryP.js'
import { levelList } from '~/api/basePriceLevel'
import { useUserStore } from '~@/stores/user'
import Iedit from './Iedit.vue';
import InventoryEdit from './InventoryEdit.vue';
const props = defineProps({
    productId: {
        type: Number,
        required: true
    },
})
const userStore = useUserStore()
const levelOptions = ref([])
const editRef = ref(null)
const inventoryEditRef = ref(null)
const expand = ref(false)
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const emit = defineEmits(['backOk'])
const formModel = ref({
    pageNum: 1,
    pageSize: 10,
    productId: null,
})

onMounted(() => {
    getList()
    getLevelList()
})
const getLevelList = async () => {
    let res = await levelList({
        userId: userStore.userInfo.id
    })
    levelOptions.value = res.data
}

const back = () => {
    emit('backOk')
}
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

const getLevelName = (id) => {
    return levelOptions.value.find(item => item.id === id)?.levelName
}

const getList = async () => {
    loading.value = true
    formModel.value.productId = props.productId
    try {
        const { data } = await list(formModel.value)
        total.value = data.total
        data.records.map((item, i) => { item.index = i + 1 })
        tableData.value = data.records
    } catch (error) {
        console.log(error)
    } finally {
        loading.value = false
    }
}

const onDelete = async (id) => {
    try {
        await del({ id })
        getList()
        message.success('删除成功');
    } catch (error) {
        console.log(error)
    }
}

const open = (record = {}) => {
    editRef.value.open(record, props.productId, levelOptions.value)
}

const iyEdit = (record) => {
    inventoryEditRef.value.open(record)
}
const columns = [
    {
        title: '序号',
        dataIndex: 'index',
        key: 'index',
        width: 80,
    },
    {
        title: '等级',
        dataIndex: 'priceLevel',
        key: 'priceLevel',
    },
    {
        title: '单价',
        dataIndex: 'price',
        key: 'price',
    },
    {
        title: '总库存（斤）',
        dataIndex: 'totalStock',
        key: 'totalStock',
    },
    {
        title: '已预订库存',
        dataIndex: 'reservedStock',
        key: 'reservedStock',
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

:deep(.ant-form-item) {
    margin-bottom: 0;
}

.pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
}
</style>