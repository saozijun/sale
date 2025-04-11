<template>
    <div class="query-box">
        <a-card>
            <a-form :model="formModel">
                <a-row :gutter="[15, 0]">
                    <a-col>
                        <a-form-item name="name" label="商品名称">
                            <a-input v-model:value="formModel.name"  placeholder="请输入" />
                        </a-form-item>
                    </a-col>
                    <a-col>
                        <a-space flex justify-end w-full>
                            <a-button :loading="loading" type="primary" @click="onSearch">查询</a-button>
                            <a-button :loading="loading" @click="onReset"> 重置 </a-button>
                        </a-space>
                    </a-col>
                </a-row>
            </a-form>
        </a-card>
    </div>
    <div class="list" v-if="listData.length">
        <div class="item" v-for="item in listData" :key="item.id">
            <img :src="item.coverImage" alt="">
            <div class="info">
                <div class="item-name">{{item.name}}</div>
                <div class="item-tips">{{item.description}}</div>
            </div>
            <div class="mask">
                <a-button :loading="loading" type="primary" @click="openModal(item)">购买</a-button>
            </div>
        </div>
    </div>
    <div style="margin: 150px 0;" v-else><a-empty /></div>
    <PurchaseModalP ref="purchaseModal" />
</template>
<script setup>
import { ref, onMounted } from 'vue';
import { StarOutlined, LikeOutlined, MessageOutlined } from '@ant-design/icons-vue';
import { publishedPage } from '~/api/retailer/purchase.js'

import PurchaseModalP from '~/components/PurchaseModalP.vue';
const listData = ref([]);
const purchaseModal = ref(null);
const formModel = ref({
    pageNum: 1,
    pageSize: 10,
    name: ""
});
onMounted(() => {
    getList()
})

const onSearch = () => {
    formModel.value.pageNum = 1
    getList()
}

const onReset = () => {
    formModel.value = {
        pageNum: 1,
        pageSize: 10,
        name: ""
    }
    getList()
}

const getList = async () => {
    const { data } = await publishedPage(formModel.value)
    listData.value = data.records
}
const openModal = (item) => {
    console.log(item);
    
    purchaseModal.value.open(item)
}

</script>

<style lang="less" scoped>
:deep(.ant-form-item){
    margin-bottom: 0;
}
.list{
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
    padding: 20px;
    margin-top: 10px;
    .item{
        width: 300px;
        border-radius: 6px;
        overflow: hidden;
        border: 1px solid #f0f0f0;
        background-color: #fff;
        cursor: pointer;
        position: relative;
        .mask{
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0,0,0,0.5);
            opacity: 0;
            z-index: -1;
            transition: all 0.3s;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        &:hover{
            .mask{
                opacity: 1;
                z-index: 1;
            }
        }
        img{
            width: 100%;
            height: 170px;
            object-fit: cover;
        }
        .info{
            padding: 20px;
            .item-name{
                font-size: 16px;
                font-weight: bold;
                color: #333;
            }
            .item-tips{
                margin-top: 10px;
                color: #666;
            }
        }
    }
}
</style>