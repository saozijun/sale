<script setup>
import { UserOutlined, TeamOutlined, RiseOutlined } from '@ant-design/icons-vue'
import { Progress, TinyArea, TinyColumn } from '@antv/g2plot'
import Trend from '~/pages/analysis/trend.vue'

const props = defineProps({
  loading: {
    type: Boolean,
    default: false,
  },
  userStats: {
    type: Object,
    default: () => ({
      totalUsers: 0,
      activeUsers: 0,
      roleDistribution: []
    })
  }
})

function convertNumber(number) {
  return number.toLocaleString()
}

const topColResponsiveProps = {
  xs: 24,
  sm: 12,
  md: 12,
  lg: 8,
  xl: 6,
  style: { marginBottom: '24px' },
}

const roleData = computed(() => {
  return props.userStats.roleDistribution.map(item => ({
    x: item.role === 'admin' ? '管理员' : '普通用户',
    y: item.count
  }))
})

const activeRate = computed(() => {
  return props.userStats.totalUsers > 0 
    ? ((props.userStats.activeUsers / props.userStats.totalUsers) * 100).toFixed(1) 
    : '0.0'
})

const tinyAreaContainer = ref()
const tinyColumnContainer = ref()
const progressContainer = ref()

const tinyArea = shallowRef()
const tinyColumn = shallowRef()
const progress = shallowRef()

onMounted(() => {
  tinyArea.value = new TinyArea(tinyAreaContainer.value, {
    height: 46,
    data: roleData.value.map(item => item.y),
    smooth: true,
    autoFit: true,
    areaStyle: {
      fill: 'l(270) 0:#ffffff 0.5:#d4bcf2 1:#975FE4',
    },
    line: {
      color: '#975FE4',
    },
  })

  tinyArea.value?.render()

  tinyColumn.value = new TinyColumn(tinyColumnContainer.value, {
    height: 46,
    autoFit: true,
    data: roleData.value.map(item => item.y),
  })
  tinyColumn.value?.render()

  progress.value = new Progress(progressContainer.value, {
    height: 46,
    autoFit: true,
    percent: props.userStats.activeUsers / props.userStats.totalUsers,
    barWidthRatio: 0.2,
    color: '#13C2C2',
  })
  progress.value?.render()
})

onBeforeUnmount(() => {
  tinyArea.value?.destroy()
  tinyArea.value = undefined
  tinyColumn.value?.destroy()
  tinyColumn.value = undefined
  progress.value?.destroy()
  progress.value = undefined
})
</script>

<template>
  <a-row :gutter="24">
    <a-col v-bind="{ ...topColResponsiveProps }">
      <a-card :loading="loading" :bordered="false" class="stat-card">
        <div class="stat-header">
          <div class="stat-title">
            <UserOutlined class="stat-icon" />
            <span>用户总数</span>
          </div>
          <div class="stat-value">{{ convertNumber(userStats.totalUsers) }}</div>
        </div>
        <div class="stat-footer">
          <div class="stat-trend">
            <Trend flag="up">
              <span class="trend-text">较上月</span>
              <span class="trend-value">+12%</span>
            </Trend>
          </div>
          <div ref="tinyAreaContainer" />
        </div>
      </a-card>
    </a-col>

    <a-col v-bind="{ ...topColResponsiveProps }">
      <a-card :loading="loading" :bordered="false" class="stat-card">
        <div class="stat-header">
          <div class="stat-title">
            <TeamOutlined class="stat-icon" />
            <span>活跃用户</span>
          </div>
          <div class="stat-value">{{ convertNumber(userStats.activeUsers) }}</div>
        </div>
        <div class="stat-footer">
          <div class="stat-trend">
            <Trend flag="up">
              <span class="trend-text">活跃率</span>
              <span class="trend-value">{{ activeRate }}%</span>
            </Trend>
          </div>
          <div ref="tinyColumnContainer" />
        </div>
      </a-card>
    </a-col>

    <a-col v-bind="{ ...topColResponsiveProps }">
      <a-card :loading="loading" :bordered="false" class="stat-card">
        <div class="stat-header">
          <div class="stat-title">
            <RiseOutlined class="stat-icon" />
            <span>用户角色分布</span>
          </div>
          <div class="stat-value">
            <a-tag color="blue">管理员: {{ userStats.roleDistribution.find(r => r.role === 'admin')?.count || 0 }}</a-tag>
            <a-tag color="green">用户: {{ userStats.roleDistribution.find(r => r.role === 'retailer')?.count || 0 }}</a-tag>
          </div>
        </div>
        <div class="stat-footer">
          <div class="stat-progress">
            <div class="progress-label">管理员占比</div>
            <div ref="progressContainer" />
          </div>
        </div>
      </a-card>
    </a-col>
  </a-row>
</template>

<style scoped lang="less">
.stat-card {
  height: 100%;
  .stat-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    .stat-title {
      display: flex;
      align-items: center;
      font-size: 16px;
      color: rgba(0, 0, 0, 0.85);
      .stat-icon {
        margin-right: 8px;
        font-size: 20px;
        color: #1890ff;
      }
    }
    .stat-value {
      font-size: 24px;
      font-weight: 500;
      color: rgba(0, 0, 0, 0.85);
    }
  }
  .stat-footer {
    .stat-trend {
      margin-bottom: 8px;
      .trend-text {
        margin-right: 8px;
        color: rgba(0, 0, 0, 0.45);
      }
      .trend-value {
        font-weight: 500;
      }
    }
    .stat-progress {
      .progress-label {
        margin-bottom: 8px;
        color: rgba(0, 0, 0, 0.45);
      }
    }
  }
}
</style>
