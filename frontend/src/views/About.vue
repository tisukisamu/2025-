<template>
  <div class="space-y-5">
    <div class="rounded-xl bg-#111111 text-white p-5 flex items-center justify-between">
      <div>
        <h1 class="text-22px m-0">关于系统</h1>
        <p class="m-0 mt-2 text-#9ca3af">了解 HabitFlow 习惯养成打卡系统</p>
      </div>
    </div>

    <a-card :bordered="false" class="border border-#e5e7eb rounded-xl">
      <div class="text-center py-8">
        <div class="text-48px font-bold text-#111111 mb-4">HabitFlow</div>
        <p class="text-#6b7280 text-lg max-w-2xl mx-auto">
          一款现代化的个人习惯养成打卡系统，帮助您建立、追踪和维护良好的日常习惯，
          通过可视化的打卡记录和数据分析，激励您持续坚持。
        </p>
        <div class="mt-6">
          <a-tag color="blue">Vue 3</a-tag>
          <a-tag color="green">Spring Boot 3</a-tag>
          <a-tag color="orange">TypeScript</a-tag>
          <a-tag color="purple">Ant Design Vue</a-tag>
        </div>
      </div>
    </a-card>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <a-card title="核心功能" :bordered="false" class="border border-#e5e7eb rounded-xl">
        <a-list :data-source="features" bordered>
          <template #renderItem="{ item }">
            <a-list-item>
              <a-list-item-meta>
                <template #avatar>
                  <div class="w-8 h-8 rounded-lg flex items-center justify-center text-white" :class="item.bgClass">
                    <component :is="item.icon" />
                  </div>
                </template>
                <template #title>{{ item.title }}</template>
                <template #description>{{ item.desc }}</template>
              </a-list-item-meta>
            </a-list-item>
          </template>
        </a-list>
      </a-card>

      <a-card title="技术栈" :bordered="false" class="border border-#e5e7eb rounded-xl">
        <a-tabs>
          <a-tab-pane key="frontend" tab="前端">
            <ul class="space-y-2 text-#4b5563">
              <li class="flex items-center gap-2">
                <check-circle-outlined class="text-green-500" />
                Vue 3 + TypeScript
              </li>
              <li class="flex items-center gap-2">
                <check-circle-outlined class="text-green-500" />
                Vite 构建工具
              </li>
              <li class="flex items-center gap-2">
                <check-circle-outlined class="text-green-500" />
                Ant Design Vue UI组件库
              </li>
              <li class="flex items-center gap-2">
                <check-circle-outlined class="text-green-500" />
                Pinia 状态管理
              </li>
              <li class="flex items-center gap-2">
                <check-circle-outlined class="text-green-500" />
                UnoCSS 原子化CSS
              </li>
            </ul>
          </a-tab-pane>
          <a-tab-pane key="backend" tab="后端">
            <ul class="space-y-2 text-#4b5563">
              <li class="flex items-center gap-2">
                <check-circle-outlined class="text-green-500" />
                Spring Boot 3
              </li>
              <li class="flex items-center gap-2">
                <check-circle-outlined class="text-green-500" />
                Spring Security + JWT
              </li>
              <li class="flex items-center gap-2">
                <check-circle-outlined class="text-green-500" />
                Spring Data JPA
              </li>
              <li class="flex items-center gap-2">
                <check-circle-outlined class="text-green-500" />
                MySQL 数据库
              </li>
              <li class="flex items-center gap-2">
                <check-circle-outlined class="text-green-500" />
                RESTful API 设计
              </li>
            </ul>
          </a-tab-pane>
        </a-tabs>
      </a-card>
    </div>

    <a-card :bordered="false" class="border border-#e5e7eb rounded-xl">
      <template #title>版本信息</template>
      <a-descriptions bordered :column="{ xs: 1, sm: 2, md: 3 }">
        <a-descriptions-item label="系统版本">v1.0.0</a-descriptions-item>
        <a-descriptions-item label="发布日期">2026-01-01</a-descriptions-item>
        <a-descriptions-item label="开发框架">前后端分离</a-descriptions-item>
        <a-descriptions-item label="前端框架">Vue 3.4</a-descriptions-item>
        <a-descriptions-item label="后端框架">Spring Boot 3.2</a-descriptions-item>
        <a-descriptions-item label="数据库">MySQL 8.0</a-descriptions-item>
      </a-descriptions>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import {
  CheckCircleOutlined,
  CheckSquareOutlined,
  CalendarOutlined,
  BarChartOutlined,
  SettingOutlined,
  UserOutlined
} from '@ant-design/icons-vue'

const features = ref([
  { title: '习惯管理', desc: '创建、编辑、分类管理您的日常习惯', icon: CheckSquareOutlined, bgClass: 'bg-blue-500' },
  { title: '每日打卡', desc: '一键打卡，记录您的坚持历程', icon: CalendarOutlined, bgClass: 'bg-green-500' },
  { title: '数据统计', desc: '可视化展示习惯完成率和趋势', icon: BarChartOutlined, bgClass: 'bg-orange-500' },
  { title: '用户管理', desc: '完善的用户认证和权限管理', icon: UserOutlined, bgClass: 'bg-purple-500' },
  { title: '系统管理', desc: '后台管理功能，数据监控', icon: SettingOutlined, bgClass: 'bg-red-500' }
])
</script>
