<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { ShoppingCartOutlined } from '@ant-design/icons-vue'

type FlyPayload = {
  imageUrl?: string
  startX: number
  startY: number
  endX: number
  endY: number
}

const flying = ref(false)
const x = ref(0)
const y = ref(0)
const scale = ref(1)
const opacity = ref(1)
const imageUrl = ref<string>('')

let timer: number | null = null

const fly = async (payload: FlyPayload) => {
  if (timer) window.clearTimeout(timer)

  flying.value = true
  imageUrl.value = payload.imageUrl || ''
  x.value = payload.startX
  y.value = payload.startY
  scale.value = 1
  opacity.value = 1

  await nextTick()

  // 下一帧启动动画（确保初始位置已渲染）
  requestAnimationFrame(() => {
    x.value = payload.endX
    y.value = payload.endY
    scale.value = 0.2
    opacity.value = 0
  })

  timer = window.setTimeout(() => {
    flying.value = false
  }, 650)
}

defineExpose({ fly })
</script>

<template>
  <Teleport to="body">
    <div
      v-if="flying"
      class="fly-wrap"
      :style="{
        left: x + 'px',
        top: y + 'px',
        transform: `translate(-50%, -50%) scale(${scale})`,
        opacity
      }"
    >
      <div class="fly-ball">
        <img v-if="imageUrl" :src="imageUrl" alt="fly" />
        <ShoppingCartOutlined v-else class="fly-icon" />
      </div>
    </div>
  </Teleport>
</template>

<style scoped>
.fly-wrap {
  position: fixed;
  z-index: 9999;
  pointer-events: none;
  transition:
    left 650ms cubic-bezier(0.2, 0.8, 0.2, 1),
    top 650ms cubic-bezier(0.2, 0.8, 0.2, 1),
    transform 650ms cubic-bezier(0.2, 0.8, 0.2, 1),
    opacity 650ms ease;
  will-change: left, top, transform, opacity;
}

.fly-ball {
  width: 56px;
  height: 56px;
  border-radius: 999px;
  background: #ffffff;
  border: 3px solid rgba(244, 63, 94, 0.5);
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.18);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.fly-ball img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.fly-icon {
  font-size: 22px;
  color: #ef4444;
}
</style>
