<template>
    <div class="box" v-if="!showInventory">
        <a-card mb-4>
            <a-form :model="formModel">
                <a-row :gutter="[15, 0]">
                    <a-col>
                        <a-form-item name="name" label="订单状态">
                            <a-select v-model:value="formModel.status" placeholder="请选择订单状态" style="width: 150px" >
                                <a-select-option value="pending">待付款</a-select-option>
                                <a-select-option value="paid">已付款</a-select-option>
                                <a-select-option value="shipped">已发货</a-select-option>
                                <a-select-option value="completed">已完成</a-select-option>
                                <a-select-option value="cancelled">已取消</a-select-option>
                                <a-select-option value="refunding">退款中</a-select-option>
                                <a-select-option value="refunded">已退款</a-select-option>
                                <a-select-option value="returning">退货中</a-select-option>
                                <a-select-option value="returned">已退货</a-select-option>
                            </a-select>
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
                        </a-space>
                    </a-col>
                </a-row>
            </a-form>
        </a-card>
        <a-table :columns="columns" :data-source="tableData" :pagination="false">
            <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'productImage'">
                    <a-image :width="80" :src="record.productImage" />
                </template>
                <template v-else-if="column.key === 'status'">
                    <a-tag color="blue">{{ orderStatus[record.status] }}</a-tag>
                </template>
                <template v-else-if="column.key === 'user'">
                    <div>
                        <div>卖家：{{ record.sellerName}}</div>
                        <div>联系方式：{{ record.sellerPhone }}</div>
                    </div>
                </template>
                <template v-else-if="column.key === 'unitPrice'">
                    <div>
                        <div>单价：<span class="total-amount"> <span>￥</span>{{ record.unitPrice }}</span></div>
                        <div>数量：{{ record.quantity }}（斤）</div>
                    </div>
                </template>
                <template v-else-if="column.key === 'totalAmount'">
                    <div class="total-amount"> <span>￥</span>{{ record.totalAmount }}</div>
                </template>
                <template v-else-if="column.key === 'operation'">
                    <div v-if="userId == record.buyerId">
                        <Pay  ref="payRef"
                            :amount="(record.quantity * record.unitPrice).toFixed(2)"
                            @success="handlePaySuccess(record.id)"
                        />
                        <a-button type="link" style="padding: 0;" v-if="record.status == 'pending'" @click="openPay">立即支付</a-button>
                        <a-popconfirm v-if="record.status === 'shipped'" title="确定收货吗?" @confirm="changeStatusFn(record.id, 'completed')">
                            <a-button type="link" style="padding: 0;">确定收货</a-button>
                        </a-popconfirm>
                        <a-popconfirm v-if="record.status === 'paid'" title="确定退款吗?" @confirm="changeStatusFn(record.id, 'refunding')">
                            <a-button type="link" style="padding: 0;">申请退款</a-button>
                        </a-popconfirm>
                        <a-popconfirm v-if="record.status === 'shipped'" title="确定退货吗?" @confirm="changeStatusFn(record.id, 'returning')">
                            <a-button type="link" >申请退货</a-button>
                        </a-popconfirm>
                        <a-popconfirm v-if="record.status === 'pending'" title="确定取消吗?" @confirm="changeStatusFn(record.id, 'cancelled')">
                            <a-button type="link" danger>取消</a-button>
                        </a-popconfirm>
                        <a-popconfirm v-if="record.status === 'completed'" title="确定删除吗?"
                            @confirm="onDelete(record.id)">
                            <a-button type="link" danger>删除</a-button>
                        </a-popconfirm>
                    </div>
                    <div v-if="userId == record.sellerId">
                        <a-popconfirm v-if="record.status === 'refunding'" title="确定同意退款吗?" @confirm="changeStatusFn(record.id, 'refunded')">
                            <a-button type="link" style="padding: 0;">同意退款</a-button>
                        </a-popconfirm>
                        <a-popconfirm v-if="record.status === 'returning'" title="确定同意退货吗?" @confirm="changeStatusFn(record.id, 'returned')">
                            <a-button type="link" style="padding: 0;">同意退货</a-button>
                        </a-popconfirm>
                        <a-popconfirm v-if="record.status === 'paid'" title="确定发货吗?" @confirm="changeStatusFn(record.id, 'shipped')">
                            <a-button type="link" style="padding: 0;">确定发货</a-button>
                        </a-popconfirm>
                    </div>
                </template>
            </template>
        </a-table>
        <div class="pagination">
            <a-pagination v-model:current="formModel.pageNum" :total="total" @change="onPageChange" />
        </div>
    </div>
</template>
<script setup>
import { PlusOutlined } from '@ant-design/icons-vue';
import { ref, onMounted, computed } from 'vue';
import { message } from 'ant-design-vue';
import { list, del, save, changeStatus } from '~/api/wholesaler/order.js'
import { useUserStore } from '~@/stores/user'
import Pay from '~/components/Pay.vue';
const payRef = ref()
const userStore = useUserStore()
const expand = ref(false)
const loading = ref(false)
const userId = computed(() => userStore.userInfo.id)
const tableData = ref([])
const productId = ref(null)
const total = ref(0)
const orderStatus = {
    'pending': '待付款',
    'paid': '待发货',
    'shipped': '已发货',
    'completed': '已完成',
    'cancelled': '已取消',
    'refunded': '已退款',
    'refunding': '退款中',
    'returned': '已退货',
    'returning': '退货中',
}
const formModel = ref({
    pageNum: 1,
    pageSize: 10,
    userId: userStore.userInfo.id,
    status: null
})

onMounted(() => {
    getList()
})

const onPageChange = (page) => {
    getList()
}
const handlePaySuccess = async (id) => {
    changeStatusFn(id, 'paid')
}
const onSearch = () => {
    getList()
}
const onReset = () => {
    formModel.value = {
        pageNum: 1,
        pageSize: 10,
        userId: userStore.userInfo.id,
        status: null
    }
    getList()
}

const changeStatusFn = async (id, status) => {
    try {
        const { data } = await changeStatus(id, status)
        message.success('操作成功')
        getList()
    } catch (error) {
        console.log(error)
    }
}

const getList = async () => {
    loading.value = true
    console.log(formModel.value);
    
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

const openPay = () => {
    payRef.value.open()
}

const columns = [
    {
        title: '序号',
        dataIndex: 'index',
        key: 'index',
        width: 80,
    },
    {
        title: '商品图片',
        dataIndex: 'productImage',
        key: 'productImage',
        width: 120,
    },
    {
        title: '订单编号',
        dataIndex: 'orderNo',
        key: 'orderNo',
        width: 200,
    },
    {
        title: '商品名称',
        dataIndex: 'productName',
        key: 'productName',
    },
    {
        title: '单价/数量',
        dataIndex: 'unitPrice',
        key: 'unitPrice',
    },
    {
        title: '小计',
        dataIndex: 'totalAmount',
        key: 'totalAmount',
    },
    {
        title: '卖家信息',
        dataIndex: 'user',
        key: 'user',
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

:deep(.ant-form-item) {
    margin-bottom: 0;
}

.pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
}

.total-amount {
    font-weight: bold;
    color: #ea2626;

    span {
        font-size: 10px;
    }
}
</style>