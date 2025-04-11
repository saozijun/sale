<script setup>
import IntroduceRow from '~/pages/analysis/introduce-row.vue'
import SalesCard from '~/pages/analysis/sales-card.vue'
import TopSearch from '~/pages/analysis/components/top-search.vue'
import ProportionSales from '~/pages/analysis/proportion-sales.vue'
import { statisticsPost } from '~/api/statistics'
import { useUserStore } from '~@/stores/user'

const userStore = useUserStore()
const loading = ref(false)

// 模拟后端数据
const statsData = ref({
  userStats: {
    totalUsers: 0,
    activeUsers: 0,
    roleDistribution: []
  },
  productStats: {
    totalProducts: 0,
    totalInventory: 0,
    totalStock: 0,
    totalReservedStock: 0,
    topProducts: [],
    placeDistribution: []
  },
  orderStats: {
    statusDistribution: [],
    orderTrend: []
  },
  stockUtilizationRate: '00.00'
})
async function fetchStats() {
  try {
    loading.value = true
    const res = await statisticsPost({
      userId: userStore.userInfo.id
    })
    statsData.value = res.data
  } catch (error) {
    console.error('获取统计数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchStats()
})
</script>

<template>
  <page-container>
    <!-- <Suspense :fallback="null">
      <IntroduceRow 
        :loading="loading" 
        :user-stats="statsData.userStats" 
      />
    </Suspense> -->

    <Suspense :fallback="null">
      <SalesCard 
        :loading="loading"
        :product-stats="statsData.productStats"
      />
    </Suspense>

    <a-row
      :gutter="24"
      :style="{ marginTop: '24px' }"
    >
      <a-col :xl="12" :lg="24" :md="24" :sm="24" :xs="24">

      </a-col>
      <a-col :xl="24" :lg="24" :md="24" :sm="24" :xs="24">
        <Suspense :fallback="null">
          <ProportionSales 
            :loading="loading"
            :order-stats="statsData.orderStats"
          />
        </Suspense>
      </a-col>
    </a-row>
  </page-container>
</template>
