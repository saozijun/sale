import type { RouteRecordRaw } from 'vue-router'
export const ROOT_ROUTE_REDIRECT_PATH = '/analysis'
import { useUserStore } from '~@/stores/user'

const Layout = () => import('~/layouts/index.vue')
const basicRouteMap = {
  // iframe模式下使用
  Iframe: () => import('~/pages/common/iframe.vue'),
  // 一般用于存在子集的页面
  RouteView: () => import('~/pages/common/route-view.vue'),
}

// 定义不同角色的路由
const adminRoutes = [
  {
    path: '/analysis',
    name: 'Analysis', 
    component: () => import('~/pages/analysis/index.vue'),
    meta: {
      title: '工作台',
      icon: 'FundProjectionScreenOutlined',
      roles: ['admin', 'growers', 'wholesaler']
    },
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('~/pages/system/settings/index.vue'),
    meta: {
      title: '个人中心',
      hideInMenu: true,
      roles: ['admin', 'growers', 'retailer', 'wholesaler']
    },
  },
  {
    path: '/product',
    name: 'Product',
    component: () => import('~/pages/growers/product/index.vue'),
    meta: {
      title: '种植商品管理',
      icon: 'CarOutlined',
      roles: ['growers']
    }
  },
  {
    path: '/wholesaler/product',
    name: 'wholesalerProduct',
    component: () => import('~/pages/wholesaler/product/index.vue'),
    meta: {
      title: '商品管理',
      icon: 'CarOutlined',
      roles: ['wholesaler']
    }
  },
  {
    path: '/retailer/purchase',
    name: 'retailerPurchase',
    component: () => import('~/pages/retailer/purchase/index.vue'),
    meta: {
      title: '商品中心',
      icon: 'InboxOutlined',
      roles: ['retailer']
    },
  },
  {
    path: '/retailer/order',
    name: 'retailerOrder',
    component: () => import('~/pages/retailer/order/index.vue'),
    meta: {
      title: '商品订单',
      icon: 'ProfileOutlined',
      roles: ['retailer','wholesaler']
    }
  },
  {
    path: '/purchase',
    name: 'purchase',
    component: () => import('~/pages/wholesaler/purchase/index.vue'),
    meta: {
      title: '采购中心',
      icon: 'InboxOutlined',
      roles: ['wholesaler']
    },
  },
  {
    path: '/wholesaler/order',
    name: 'wholesalerOrder',
    component: () => import('~/pages/wholesaler/order/index.vue'),
    meta: {
      title: '采购订单',
      icon: 'ProfileOutlined',
      roles: ['growers','wholesaler']
    }
  },
  {
    path: '/system',
    name: 'System',
    meta: {
      title: '系统管理',
      icon: 'SettingOutlined',
      roles: ['admin']
    },
    component: basicRouteMap.RouteView,
    children: [
      {
        path: '/system/role',
        name: 'Role',
        component: () => import('~/pages/system/role/index.vue'),
        meta: {
          title: '角色列表',
          roles: ['admin']
        },
      },
      {
        path: '/system/user',
        name: 'User',
        component: () => import('~/pages/system/user/index.vue'),
        meta: {
          title: '用户管理',
          roles: ['admin']
        },
      }
    ],
  },
  {
    path: '/system2',
    name: 'System2',
    meta: {
      title: '系统管理',
      icon: 'SettingOutlined',
      roles: ['growers','wholesaler']
    },
    component: basicRouteMap.RouteView,
    children: [
      {
        path: '/system/baseplace',
        name: 'BasePlace',
        component: () => import('~/pages/system/baseplace/index.vue'),
        meta: {
          title: '产地管理',
          roles: ['growers', 'wholesaler']
        },
      },
      {
        path: '/system/basePriceLevel',
        name: 'BasePriceLevel',
        component: () => import('~/pages/system/basePriceLevel/index.vue'),
        meta: {
          title: '商品等级管理',
          roles: ['growers', 'wholesaler']
        },
      },
    ],
  }
]

export const rootRoute: RouteRecordRaw = {
  path: '/',
  name: 'rootPath',
  redirect: ()=>{
    const userStore = useUserStore()
    const role = userStore.userInfo?.role
    if (!role) return '/login'
    
    switch(role) {
      case 'admin':
        return ROOT_ROUTE_REDIRECT_PATH
      case 'growers':
        return ROOT_ROUTE_REDIRECT_PATH
      case 'wholesaler':
        return ROOT_ROUTE_REDIRECT_PATH
      case 'retailer':
        return '/retailer/purchase'
      default:
        return '/login'
    }
  },
  component: Layout,
  children: [],
}

// 根据角色过滤路由
const filterRoutesByRole = (routes: any[], role: string) => {
  return routes.filter(route => {
    if (route.meta && route.meta.roles) {
      return route.meta.roles.includes(role)
    }
    return true
  }).map(route => {
    if (route.children) {
      route.children = filterRoutesByRole(route.children, role)
    }
    return route
  })
}

// 导出路由
export default adminRoutes as RouteRecordRaw[]

// 导出获取角色路由的方法
export const getRoutesByRole = (role: string) => {
  return filterRoutesByRole(adminRoutes, role) as RouteRecordRaw[]
}
