<template>
    <div class="purchase">
        <a-modal v-model:open="show" title="购买" @ok="handleOk" width="700px">
            <div class="content">
                <div class="img-box">
                    <div class="img-big">
                        <a-image :src="info.images[nowImg].url" />
                    </div>
                    <div class="img-small">
                        <ul>
                            <li v-for="(item, index) in info.images" :key="index" @click="nowImg = index"
                                :class="{ 'v-active': index === nowImg }">
                                <img :src="item.url" :alt="info.name">
                            </li>
                        </ul>
                    </div>
                </div>
                <div class="info">
                    <p class="title">{{ info.name }}</p>
                    <div class="desc-box">
                        <p>商家描述：{{ info.description }}</p>
                        <p>商家：{{ info.manufacturer }}</p>
                        <p>联系方式：{{ info.contact }}</p>
                        <p>产地：{{ info.origin }}</p>
                        <p>预计上市时间：{{ info.listedTime }}</p>
                    </div>
                    <div class="sku" v-if="nowInfo">
                        <p>总库存：<span>{{ kcInfo.totalStock }}/{{ kcInfo.availableStock}}</span></p>
                        <p>选择商品规格：</p>
                        <div class="sku-list">
                            <div class="sku-item" :class="{ 'sku-active': item.id === nowInfo.id }" v-for="(item, index) in kcInfo.inventories" :key="index" @click="nowInfo = item; quantity = 1">
                                <a-tooltip placement="top">
                                    <template #title>
                                        <span>{{ item.priceLevelDescription }}</span>
                                    </template>
                                    <p>{{ item.priceLevelName }} <span style="font-size: 12px; color: #999;">{{ item.totalStock - item.reservedStock }}</span></p>
                                </a-tooltip>
                            </div>
                        </div>
                        <div class="sku-info">
                            <div class="sku-info-tips">描述：{{ nowInfo.priceLevelDescription }}</div>
                            <div class="sku-info-stock">库存：{{ nowInfo.totalStock - nowInfo.reservedStock }}</div>
                            <div class="sku-info-ladder" v-if="ladderRules.length">
                                <div class="ladder-title">优惠规则：</div>
                                <div class="ladder-rules">
                                    <p v-for="(rule, index) in ladderRules" :key="index">
                                        满{{ rule.amount }}件，单价打{{ (rule.discount * 10).toFixed(1) }}折
                                    </p>
                                </div>
                            </div>
                            <div class="sku-info-quantity">
                                <span>购买数量：</span>
                                <a-input-number 
                                    v-model:value="quantity" 
                                    :min="1" 
                                    :max="nowInfo.totalStock - nowInfo.reservedStock"
                                    size="small"
                                />
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <template #footer>
                <div class="price-box">
                    <span class="sku-info-price" v-if="nowInfo">
                        <span>￥</span>{{ calculateFinalPrice() }}
                        <span class="original-price" v-if="hasDiscount">原价：￥{{ (quantity * nowInfo.price).toFixed(2) }}</span>
                    </span>
                    <div class="discount-tip" v-if="nextLadderTip">
                        {{ nextLadderTip }}
                    </div>
                </div>
                <a-button key="back" @click="show = false">取消</a-button>
                <a-button key="submit" type="primary" @click="handleOk">立即采购</a-button>
            </template>
        </a-modal>
        
        <Pay 
            v-if="nowInfo"
            ref="payRef"
            :amount="calculateFinalPrice()"
            @success="handlePaySuccess"
            @cancel="handlePayCancel"
        />
    </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { listAll } from '~/api/growers/inventoryP.js'
import { save } from '~/api/retailer/order.js'
import { useUserStore } from '~@/stores/user'
import { message } from 'ant-design-vue';
import Pay from './Pay.vue';

const router = useRouter()
const userStore = useUserStore();
const show = ref(false);
const kcInfo = ref({});
const nowInfo = ref(null);
const info = ref({});
const nowImg = ref(0);
const quantity = ref(1);
const payRef = ref(null);
const ladderRules = ref([]);
const hasDiscount = ref(false);
const nextLadderTip = ref('');

const open = (row) => {
    show.value = true;
    info.value = { ...row };
    info.value.images = JSON.parse(info.value.images);
    getkcInfo();
}

watch(nowInfo ,(val)=>{
    if (val.ladderDiscount) {
        ladderRules.value = JSON.parse(val.ladderDiscount);
        ladderRules.value.sort((a, b) => a.amount - b.amount);
        updateNextLadderTip();
    }
})

const getkcInfo = async () => {
    const { data } = await listAll({
        productId: info.value.id
    })
    quantity.value = 1;
    kcInfo.value = data;
    nowInfo.value = kcInfo.value.inventories[0];
    // 解析阶梯价格规则
    if (nowInfo.value.ladderDiscount) {
        ladderRules.value = JSON.parse(nowInfo.value.ladderDiscount);
        ladderRules.value.sort((a, b) => a.amount - b.amount);
    }
    updateNextLadderTip();
}

const handleOk = () => {
    // 判断库存
    if (quantity.value > nowInfo.value.totalStock - nowInfo.value.reservedStock  || quantity.value < 1) {
        message.error('库存不足');
        return;
    }
    payRef.value.open(true);
}

const handlePaySuccess = async () => {
    show.value = false;
    await saveOrder('paid');
}

const handlePayCancel = async () => {
    await saveOrder('pending');
    router.replace('/retailer/order')
    message.success('已下单，请到订单管理查看订单状态。');
}

// 下单
const saveOrder = async (status) => {
    await save({
        orderNo: getOrderNo(),
        buyerId: userStore.userInfo.id,
        sellerId: info.value.userId,
        productId: info.value.id,
        inventoryId: nowInfo.value.id,
        quantity: quantity.value,
        totalAmount: calculateFinalPrice(),
        status,
        unitPrice: nowInfo.value.price
    })
}

// 生成随机订单编号
const getOrderNo = () => {
    return Date.now() + Math.random().toString(36).substring(2);
}

// 计算最终价格
const calculateFinalPrice = () => {
    if (!nowInfo.value) return 0;
    
    let finalPrice = quantity.value * nowInfo.value.price;
    hasDiscount.value = false;
    
    if (ladderRules.value.length) {
        // 找到适用的最高档阶梯价格
        const applicableRule = [...ladderRules.value]
            .reverse()
            .find(rule => quantity.value >= rule.amount);
            
        if (applicableRule) {
            finalPrice = finalPrice * applicableRule.discount;
            hasDiscount.value = true;
        }
    }
    
    updateNextLadderTip();
    return finalPrice.toFixed(2);
}

// 更新下一档优惠提示
const updateNextLadderTip = () => {
    if (!ladderRules.value.length) {
        nextLadderTip.value = '';
        return;
    }
    
    const nextRule = ladderRules.value.find(rule => quantity.value < rule.amount);
    if (nextRule) {
        const needMore = nextRule.amount - quantity.value;
        nextLadderTip.value = `再购买${needMore}件，可享受${(nextRule.discount * 10).toFixed(0)}折优惠`;
    } else {
        nextLadderTip.value = '已达到最高优惠';
    }
}

// 监听数量变化
watch(quantity, () => {
    calculateFinalPrice();
});

defineExpose({
    open
})
</script>

<style lang="less" scoped>
.content {
    display: flex;
    align-items: flex-start;
    gap: 20px;

    .img-big {
        width: 300px;
        height: 300px;
        border-radius: 6px;
        overflow: hidden;

        :deep(.ant-image-img) {
            height: 300px;
            object-fit: cover;
        }
    }

    .img-small {
        margin-top: 20px;

        ul {
            display: flex;
            gap: 10px;
            margin: 0;
            padding: 0;

            li {
                width: 80px;
                height: 80px;
                border-radius: 4px;
                border: 2px solid #f0f0f0;
                overflow: hidden;
                transition: all .3s;
                cursor: pointer;

                img {
                    width: 100%;
                    height: 100%;
                    object-fit: cover;
                }

                &:hover {
                    border-color: #1677ff;
                }
            }

            .v-active {
                border-color: #1677ff;
            }
        }
    }

    .info {
        p {
            margin: 0;
        }

        .title {
            font-size: 20px;
            font-weight: bold;
            margin-bottom: 10px;
        }

        .desc-box {
            display: flex;
            flex-direction: column;
            gap: 4px;
            color: #666;
        }
    }

    .sku {
        margin-top: 5px;

        >p {
            margin-bottom: 5px;
            font-weight: bold;
            span{
                color: #1677ff;
                font-weight: 400;
            }
        }

        .sku-list {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;

            .sku-item {
                width: fit-content;
                min-width: 40px;
                text-align: center;
                padding: 2px 10px;
                border-radius: 4px;
                background-color: #f0f0f0;
                border: 1px solid #f0f0f0;
                cursor: pointer;
                font-size: 14px;

                &:hover {
                    border-color: #1677ff;
                    background-color: #eaf3ff;
                    color: #1677ff;
                }
            }
            .sku-active{
                border-color: #1677ff;
                color: #1677ff;
                background-color: #eaf3ff;
            }
        }
    }
    .sku-info{
        margin-top: 5px;
        .sku-info-quantity {
            display: flex;
            align-items: center;
            margin-top: 10px;
            gap: 10px;
        }
    }
}
.sku-info-price{
    color: #ff4d4d;
    font-size: 26px;
    font-weight: bold;
    margin-right: 10px;
    position: relative;
    top: 4px;
    span{
        font-size: 14px;
    }
}
.sku-info-ladder {
    margin: 10px 0;
    .ladder-title {
        font-weight: bold;
        margin-bottom: 5px;
    }
    .ladder-rules {
        color: #ff4d4d;
        font-size: 13px;
        p {
            margin: 2px 0;
        }
    }
}

.price-box {
    display: inline-flex;
    flex-direction: column;
    align-items: flex-start;
    margin-right: 10px;
    
    .original-price {
        font-size: 12px;
        color: #999;
        text-decoration: line-through;
        margin-left: 8px;
    }
    
    .discount-tip {
        font-size: 12px;
        color: #ff4d4d;
    }
}
</style>