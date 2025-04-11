<script setup lang="ts">
import { EllipsisOutlined, InfoCircleOutlined } from '@ant-design/icons-vue'
import { TinyArea } from '@antv/g2plot'
import NumberInfo from '~/pages/analysis/number-info.vue'
import Trend from '~/pages/analysis/trend.vue'

const props = defineProps({
  loading: {
    type: Boolean,
    default: false,
  },
  stockUtilizationRate: {
    type: String,
    default: '0.00'
  }
})

const columns = [
  {
    title: '指标',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: '数值',
    dataIndex: 'value',
    key: 'value',
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
  }
]

const stockData = [
  {
    name: '库存利用率',
    value: `${props.stockUtilizationRate}%`,
    status: '正常'
  },
  {
    name: '库存周转率',
    value: '2.5',
    status: '良好'
  },
  {
    name: '库存积压率',
    value: '5.2%',
    status: '警告'
  }
]

const tinyAreaContainer1 = ref()
const tinyAreaContainer2 = ref()

const tinyArea1 = shallowRef()
const tinyArea2 = shallowRef()

onMounted(() => {
  tinyArea1.value = new TinyArea(tinyAreaContainer1.value, {
    height: 46,
    data: [30, 40, 35, 50, 49, 60, 70, 91, 125],
    smooth: true,
    autoFit: true,
    areaStyle: {
      fill: 'l(270) 0:#ffffff 0.5:#d4bcf2 1:#975FE4',
    },
    line: {
      color: '#975FE4',
    },
  })
  tinyArea1.value?.render()

  tinyArea2.value = new TinyArea(tinyAreaContainer2.value, {
    height: 46,
    data: [20, 30, 25, 40, 39, 50, 60, 81, 115],
    smooth: true,
    autoFit: true,
    areaStyle: {
      fill: 'l(270) 0:#ffffff 0.5:#d4bcf2 1:#975FE4',
    },
    line: {
      color: '#975FE4',
    },
  })
  tinyArea2.value?.render()
})

onBeforeUnmount(() => {
  tinyArea1.value?.destroy()
  tinyArea1.value = undefined
  tinyArea2.value?.destroy()
  tinyArea2.value = undefined
})
</script>

<template>
  <a-card
    :loading="loading"
    :bordered="false"
    title="库存分析"
    :style="{ height: '100%' }"
  >
    <template #extra>
      <span class="iconGroup">
        <a-dropdown placement="bottomRight">
          <template #overlay>
            <a-menu>
              <a-menu-item>操作一</a-menu-item>
              <a-menu-item>操作二</a-menu-item>
            </a-menu>
          </template>
          <EllipsisOutlined />
        </a-dropdown>
      </span>
    </template>
    <a-row :gutter="68">
      <a-col :sm="12" :xs="24" :style="{ marginBottom: '24px' }">
        <NumberInfo
          :gap="8"
          :total="parseFloat(stockUtilizationRate)"
          status="up"
          :sub-total="17.1"
        >
          <template #subTitle>
            <span>
              库存利用率
              <Tooltip title="反映库存使用效率，数值越高表示库存利用越充分">
                <InfoCircleOutlined :style="{ marginLeft: '8px' }" />
              </Tooltip>
            </span>
          </template>
        </NumberInfo>
        <div ref="tinyAreaContainer1" />
      </a-col>
      <a-col :sm="12" :xs="24" :style="{ marginBottom: '24px' }">
        <NumberInfo
          :gap="8"
          :total="2.7"
          status="down"
          :sub-total="26.2"
        >
          <template #subTitle>
            <span>
              库存周转率
              <Tooltip title="反映库存周转速度，数值越高表示库存周转越快">
                <InfoCircleOutlined :style="{ marginLeft: '8px' }" />
              </Tooltip>
            </span>
          </template>
        </NumberInfo>
        <div ref="tinyAreaContainer2" />
      </a-col>
    </a-row>
    <a-table
      size="small"
      :columns="columns"
      :data-source="stockData"
      :pagination="false"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <a-tag :color="record.status === '正常' ? 'green' : record.status === '良好' ? 'blue' : 'orange'">
            {{ record.status }}
          </a-tag>
        </template>
      </template>
    </a-table>
  </a-card>
</template>

<style scoped lang="less">
.iconGroup {
  span.anticon {
    margin-left: 16px;
    color: inherit;
    cursor: pointer;
    transition: color 0.32s;
    &:hover {
      color: var(--text-color);
    }
  }
}
</style>
