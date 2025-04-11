<script setup>
import { Column } from '@antv/g2plot'
import dayjs from 'dayjs'
import { ShoppingOutlined, InboxOutlined, BarChartOutlined } from '@ant-design/icons-vue'

const props = defineProps({
  loading: {
    type: Boolean,
    default: false,
  },
  productStats: {
    type: Object,
    default: () => ({
      totalProducts: 0,
      totalInventory: 0,
      totalStock: 0,
      totalReservedStock: 0,
      topProducts: [],
      placeDistribution: []
    })
  }
})

const rankingListData = computed(() => {
  return props.productStats.topProducts.map((item, index) => ({
    title: item.name,
    total: item.count
  }))
})

const placeData = computed(() => {
  return props.productStats.placeDistribution.map(item => ({
    x: item.place,
    y: item.count
  }))
})

function convertNumber(number) {
  return number ? number.toLocaleString() : '0'
}

const column = shallowRef()
const columnPlotContainer1 = ref()

// 监听容器元素变化
watch(columnPlotContainer1, (newContainer) => {
  if (newContainer && placeData.value.length > 0) {
    initChart()
  }
}, { immediate: true })

// 监听数据变化
watch(placeData, (newData) => {
  if (column.value) {
    column.value.changeData(newData)
  } else if (columnPlotContainer1.value) {
    initChart()
  }
}, { immediate: true })

function initChart() {
  if (!columnPlotContainer1.value || placeData.value.length === 0) return
  
  if (column.value) {
    column.value.destroy()
  }
  
  column.value = new Column(columnPlotContainer1.value, {
    data: placeData.value,
    xField: 'x',
    yField: 'y',
    height: 300,
    xAxis: {
      label: {
        autoHide: true,
        autoRotate: false,
      },
    },
    meta: {
      y: {
        alias: '数量',
      },
    },
  })
  column.value?.render()
}

onMounted(() => {
  // 确保DOM更新后再初始化
  nextTick(() => {
    initChart()
  })
})

onBeforeUnmount(() => {
  column.value?.destroy()
  column.value = undefined
})
</script>

<template>
  <a-row :gutter="24">
    <a-col :span="24">
      <a-card :loading="loading" :bordered="false" class="overview-card">
        <a-row :gutter="24">
          <a-col :xs="24" :sm="12" :md="6">
            <div class="stat-item">
              <div class="stat-icon">
                <ShoppingOutlined />
              </div>
              <div class="stat-content">
                <div class="stat-title">商品总数</div>
                <div class="stat-value">{{ convertNumber(productStats.totalProducts) }}</div>
              </div>
            </div>
          </a-col>
          <a-col :xs="24" :sm="12" :md="6">
            <div class="stat-item">
              <div class="stat-icon">
                <InboxOutlined />
              </div>
              <div class="stat-content">
                <div class="stat-title">库存总数</div>
                <div class="stat-value">{{ convertNumber(productStats.totalInventory) }}</div>
              </div>
            </div>
          </a-col>
          <a-col :xs="24" :sm="12" :md="6">
            <div class="stat-item">
              <div class="stat-icon">
                <BarChartOutlined />
              </div>
              <div class="stat-content">
                <div class="stat-title">总库存量</div>
                <div class="stat-value">{{ convertNumber(productStats.totalStock) }}</div>
              </div>
            </div>
          </a-col>
          <a-col :xs="24" :sm="12" :md="6">
            <div class="stat-item">
              <div class="stat-icon warning">
                <BarChartOutlined />
              </div>
              <div class="stat-content">
                <div class="stat-title">已预定库存</div>
                <div class="stat-value">{{ convertNumber(productStats.totalReservedStock) }}</div>
              </div>
            </div>
          </a-col>
        </a-row>
      </a-card>
    </a-col>

    <a-col :span="24" style="margin-top: 24px;">
      <a-card :loading="loading" :bordered="false" class="chart-card">
        <a-tabs default-active-key="1">
          <a-tab-pane key="1" tab="热门商品排行">
            <a-row :gutter="24">
              <a-col :xs="24" :sm="24" :md="12">
                <div class="ranking-list">
                  <h4 class="ranking-title">TOP 10 热门商品</h4>
                  <ul>
                    <li v-for="(item, index) in rankingListData" :key="index" class="ranking-item">
                      <span class="ranking-number" :class="{ active: index < 3 }">{{ index + 1 }}</span>
                      <span class="ranking-name">{{ item.title }}</span>
                      <span class="ranking-value">{{ convertNumber(item.total) }}</span>
                    </li>
                  </ul>
                </div>
              </a-col>
              <a-col :xs="24" :sm="24" :md="12">
                <div class="chart-container">
                  <h4 class="chart-title">商品产地分布</h4>
                  <div ref="columnPlotContainer1" />
                </div>
              </a-col>
            </a-row>
          </a-tab-pane>
        </a-tabs>
      </a-card>
    </a-col>
  </a-row>
</template>

<style scoped lang="less">
.overview-card {
  .stat-item {
    display: flex;
    align-items: center;
    padding: 20px;
    background: #fafafa;
    border-radius: 4px;
    .stat-icon {
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
      &.warning {
        background: #fff7e6;
        color: #fa8c16;
      }
    }
    .stat-content {
      flex: 1;
      .stat-title {
        margin-bottom: 4px;
        color: rgba(0, 0, 0, 0.45);
        font-size: 14px;
      }
      .stat-value {
        color: rgba(0, 0, 0, 0.85);
        font-size: 24px;
        font-weight: 500;
      }
    }
  }
}
ul{
  margin: 0;
  padding: 0;
}
.chart-card {
  .ranking-list {
    .ranking-title {
      margin-bottom: 10px;
      color: rgba(0, 0, 0, 0.85);
      font-weight: 500;
    }
    .ranking-item {
      display: flex;
      align-items: center;
      margin-bottom: 16px;
      background-color: #f8f8f8;
      padding: 10px 20px;
      border-radius: 4px;
      .ranking-number {
        width: 20px;
        height: 20px;
        margin-right: 16px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: #fafafa;
        border-radius: 10px;
        font-size: 12px;
        &.active {
          background: #1890ff;
          color: #fff;
        }
      }
      .ranking-name {
        flex: 1;
        margin-right: 16px;
        color: rgba(0, 0, 0, 0.85);
      }
      .ranking-value {
        color: rgba(0, 0, 0, 0.45);
      }
    }
  }
  .chart-container {
    padding: 0 24px;
    .chart-title {
      margin-bottom: 24px;
      color: rgba(0, 0, 0, 0.85);
      font-weight: 500;
    }
  }
}
</style>
