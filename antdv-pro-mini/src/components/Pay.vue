<template>
    <a-modal v-model:open="show" title="支付" :footer="null" width="400px">
        <div class="pay-modal">
            <div class="pay-amount">
                <span class="label">支付金额</span>
                <span class="amount">￥{{ amount }}</span>
            </div>
            <div class="pay-methods">
                <div class="pay-title">选择支付方式</div>
                <div class="method-list">
                    <div class="method-item" :class="{ active: payMethod === 'alipay' }" @click="payMethod = 'alipay'">
                        <img src="../assets/images/zfb.png" alt="支付宝">
                        <span>支付宝支付</span>
                    </div>
                    <div class="method-item" :class="{ active: payMethod === 'wechat' }" @click="payMethod = 'wechat'">
                        <img src="../assets/images/wx.png" alt="微信">
                        <span>微信支付</span>
                    </div>
                </div>
            </div>
            <div class="pay-footer">
                <a-button @click="handleCancel">取消支付</a-button>
                <a-button type="primary" @click="handlePay" :loading="paying">确认支付</a-button>
            </div>
        </div>
    </a-modal>
</template>

<script setup>
import { ref } from 'vue';
import { message } from 'ant-design-vue';
const router = useRouter()
const isToOrder = ref(false)
const props = defineProps({
    amount: {
        type: [Number, String],
        required: true
    }
});
const emit = defineEmits(['success', 'cancel']);
const show = ref(false);
const payMethod = ref('alipay');
const paying = ref(false);

const open = (toOrder = false) => {
    isToOrder.value = toOrder
    show.value = true;
};

const handlePay = async () => {
    paying.value = true;
    // 模拟支付过程
    setTimeout(() => {
        paying.value = false;
        show.value = false;
        emit('success')
        message.success('支付成功！');
    }, 500);
};

const handleCancel = () => {
    show.value = false;
    emit('cancel')
};

defineExpose({
    open
});
</script>

<style lang="less" scoped>
.pay-modal {
    .pay-amount {
        text-align: center;
        margin-bottom: 20px;
        .label {
            font-size: 14px;
            color: #666;
            margin-right: 10px;
        }
        .amount {
            font-size: 24px;
            color: #ff4d4d;
            font-weight: bold;
        }
    }

    .pay-methods {
        .pay-title {
            font-size: 14px;
            color: #666;
            margin-bottom: 10px;
        }

        .method-list {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;

            .method-item {
                flex: 1;
                display: flex;
                align-items: center;
                gap: 8px;
                padding: 10px;
                border: 1px solid #d9d9d9;
                border-radius: 4px;
                cursor: pointer;
                transition: all 0.3s;

                img {
                    width: 24px;
                    height: 24px;
                }

                &:hover {
                    border-color: #1677ff;
                }

                &.active {
                    border-color: #1677ff;
                    background-color: #e6f7ff;
                }
            }
        }
    }

    .pay-footer {
        display: flex;
        justify-content: flex-end;
        gap: 10px;
        margin-top: 20px;
    }
}
</style>
