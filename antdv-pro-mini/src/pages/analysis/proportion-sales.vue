<script setup>
import { EllipsisOutlined, ShoppingCartOutlined, DollarOutlined, StockOutlined, SyncOutlined } from '@ant-design/icons-vue'
import { Pie, Line } from '@antv/g2plot'

const props = defineProps({
  loading: {
    type: Boolean,
    default: false,
  },
  orderStats: {
    type: Object,
    default: () => ({
      statusDistribution: [],
      orderTrend: []
    })
  },
  stockStats: {
    type: Object,
    default: () => ({
      utilizationRate: '0.00',
      turnoverRate: '0.00'
    })
  }
})

// 订单状态映射
const orderStatusMap = {
  'pending': '待付款',
  'paid': '待发货',
  'shipped': '已发货',
  'completed': '已完成',
  'cancelled': '已取消',
  'refunded': '已退款',
  'refunding': '退款中',
  'returned': '已退货',
  'returning': '退货中'
}

const pieContainer = ref()
const lineContainer = ref()

const statusData = computed(() => {
  return props.orderStats.statusDistribution.map(item => ({
    type: orderStatusMap[item.status] || item.status,
    value: Number(item.count)
  }))
})

const trendData = computed(() => {
  return props.orderStats.orderTrend.map(item => ({
    date: item.month,
    value: Number(item.amount)
  }))
})

const pie = shallowRef()
const line = shallowRef()

// 添加空状态判断
const hasStatusData = computed(() => statusData.value.length > 0)
const hasTrendData = computed(() => trendData.value.length > 0)

// 监听数据变化，重新渲染图表
watch([statusData, trendData], () => {
  nextTick(() => {
    renderCharts()
  })
})

// 渲染图表的函数
function renderCharts() {
  // 销毁旧的图表实例
  pie.value?.destroy()
  line.value?.destroy()

  // 渲染订单状态分布饼图
  if (pieContainer.value && statusData.value.length > 0) {
    pie.value = new Pie(pieContainer.value, {
      appendPadding: 10,
      data: statusData.value,
      angleField: 'value',
      colorField: 'type',
      radius: 0.8,
      label: {
        type: 'outer',
        formatter: (item) => {
          return `${item.type}: ${item.value}`
        },
      },
      interactions: [
        { type: 'element-active' },
        { type: 'legend-highlight' }
      ],
      legend: {
        position: 'bottom'
      },
      tooltip: {
        formatter: (datum) => {
          return { name: datum.type, value: datum.value }
        },
      },
    })
    pie.value.render()
  }

  // 渲染订单趋势折线图
  if (lineContainer.value && trendData.value.length > 0) {
    line.value = new Line(lineContainer.value, {
      data: trendData.value,
      xField: 'date',
      yField: 'value',
      smooth: true,
      point: {
        size: 5,
        shape: 'diamond',
      },
      label: {
        style: {
          fill: '#aaa',
        },
      },
      tooltip: {
        formatter: (datum) => {
          return { name: datum.date, value: datum.value }
        },
      },
    })
    line.value.render()
  }
}

onMounted(() => {
  renderCharts()
})

onBeforeUnmount(() => {
  pie.value?.destroy()
  pie.value = undefined
  line.value?.destroy()
  line.value = undefined
})
</script>

<template>
  <a-card
    :loading="loading"
    class="order-card"
    :bordered="false"
    title="订单统计"
  >
    <a-row :gutter="24" style="margin-top: 24px;">
      <a-col :span="12">
        <div class="chart-section">
          <h4 class="chart-title">订单状态分布</h4>
          <div v-if="hasStatusData" ref="pieContainer" class="chart-container" />
          <a-empty v-else description="暂无数据" class="empty-container" />
        </div>
      </a-col>
      <a-col :span="12">
        <div class="chart-section">
          <h4 class="chart-title">订单金额趋势</h4>
          <div v-if="hasTrendData" ref="lineContainer" class="chart-container" />
          <a-empty v-else description="暂无数据" class="empty-container" />
        </div>
      </a-col>
    </a-row>
  </a-card>
</template>

<style scoped lang="less">
.order-card {
  height: 100%;
  
  .stock-indicators {
    display: flex;
    gap: 24px;
    margin-bottom: 24px;
    
    .indicator-item {
      flex: 1;
      display: flex;
      align-items: center;
      padding: 16px;
      background: #fafafa;
      border-radius: 4px;
      
      .indicator-icon {
        width: 48px;
        height: 48px;
        margin-right: 16px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: #e6f7ff;
        border-radius: 8px;
        font-size: 24px;
        color: #1890ff;
      }
      
      .indicator-content {
        flex: 1;
        
        .indicator-title {
          margin-bottom: 4px;
          color: rgba(0, 0, 0, 0.45);
          font-size: 14px;
        }
        
        .indicator-value {
          margin-bottom: 4px;
          color: rgba(0, 0, 0, 0.85);
          font-size: 24px;
          font-weight: 500;
        }
        
        .indicator-desc {
          color: rgba(0, 0, 0, 0.45);
          font-size: 12px;
        }
      }
    }
  }
  
  .chart-section {
    padding: 0 16px;
    
    .chart-title {
      margin-bottom: 16px;
      color: rgba(0, 0, 0, 0.85);
      font-weight: 500;
    }
    
    .chart-container {
      height: 300px;
      width: 100%;
    }

    .empty-container {
      height: 300px;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }
}
</style>
