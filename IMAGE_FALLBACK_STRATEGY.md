# 商品图片双保底策略 - 实现文档与测试计划

## 1. 方案概述

### 1.1 背景
当前 `Product` 实体存在两个图片路径字段：
- `imageUrl`: 存储绝对路径（如 `http://example.com/uploads/apple.jpg`）
- `imageUrls`: 存储相对路径（如 `/uploads/apple.jpg,/uploads/apple2.jpg`）

### 1.2 目标
- 保持现有数据不变，确保向后兼容
- 实现智能路径选择：优先使用相对路径，失败时降级到绝对路径
- 提供统一API响应格式，便于前端处理
- 确保在各种网络环境下的稳定性

### 1.3 核心策略
```
┌─────────────────────────────────────────────────────────────┐
│                    图片双保底策略流程                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   ┌──────────────┐                                          │
│   │ 请求商品图片  │                                          │
│   └──────┬───────┘                                          │
│          ▼                                                  │
│   ┌─────────────────┐                                       │
│   │ 检查 imageUrls  │──── 有数据 ────▶ 使用相对路径          │
│   │  (相对路径)      │         (primaryImageUrl)            │
│   └────────┬────────┘                                       │
│            │ 无数据                                         │
│            ▼                                                │
│   ┌─────────────────┐                                       │
│   │ 检查 imageUrl   │──── 有数据 ────▶ 使用绝对路径          │
│   │  (绝对路径)      │         (fallback)                   │
│   └────────┬────────┘                                       │
│            │ 无数据                                         │
│            ▼                                                │
│   ┌─────────────────┐                                       │
│   │   返回空/占位图  │                                        │
│   └─────────────────┘                                       │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## 2. 后端实现

### 2.1 核心类

#### ImageUrlUtil.java
位置：`backend/src/main/java/com/agri/store/util/ImageUrlUtil.java`

**主要方法**:
- `getPrimaryImage(Product)`: 获取主图（优先相对路径）
- `getAllImages(Product)`: 获取所有图片
- `getFallbackUrl(Product)`: 获取降级URL
- `buildImagePathInfo(Product)`: 构建完整图片信息

#### ProductDTO.java
位置：`backend/src/main/java/com/agri/store/dto/ProductDTO.java`

**新增字段**:
```java
private String primaryImageUrl;      // 主图URL（处理后）
private String primaryImageType;     // 路径类型
private String fallbackImageUrl;     // 降级URL
private List<String> allImageUrls;   // 所有图片URL
private String imagePathType;        // 当前类型代码
private String imagePathTypeDesc;    // 当前类型描述
```

### 2.2 路径类型枚举
```java
public enum ImagePathType {
    RELATIVE("relative", "相对路径"),
    ABSOLUTE("absolute", "绝对路径"),
    FALLBACK("fallback", "降级路径"),
    NONE("none", "无图片");
}
```

### 2.3 路径处理逻辑
```java
// 1. 优先使用 imageUrls（相对路径）
List<String> relativeUrls = parseImageUrls(product.getImageUrls());
if (!relativeUrls.isEmpty()) {
    String url = relativeUrls.get(0);
    // 确保相对路径以 / 开头
    if (!url.startsWith("http") && !url.startsWith("/")) {
        url = "/" + url;
    }
    return new ImagePathResult(url, ImagePathType.RELATIVE, relativeUrls);
}

// 2. 降级使用 imageUrl（绝对路径）
if (product.getImageUrl() != null && !product.getImageUrl().trim().isEmpty()) {
    return new ImagePathResult(
        product.getImageUrl(),
        ImagePathType.ABSOLUTE,
        Collections.singletonList(product.getImageUrl())
    );
}

// 3. 无图片
return new ImagePathResult(null, ImagePathType.NONE, Collections.emptyList());
```

## 3. 前端实现

### 3.1 核心文件

#### imageLoader.ts
位置：`frontend/src/utils/imageLoader.ts`

**主要函数**:
- `getPrimaryImageUrl(product)`: 获取主图URL
- `getAllImageUrls(product)`: 获取所有图片URL
- `getFallbackImageUrl(product)`: 获取降级URL
- `useProductImage(product)`: 组合式函数，管理加载状态

#### ProductImage.vue
位置：`frontend/src/components/ProductImage.vue`

**功能**:
- 自动应用双保底策略加载图片
- 显示加载状态（loading spinner）
- 加载失败时自动降级到备用URL
- 显示降级提示（开发环境）
- 支持重试机制

### 3.2 使用方式

**方式1：使用 ProductImage 组件（推荐）**
```vue
<template>
  <ProductImage 
    :product="product" 
    :alt="product.name"
    height="200"
    border-radius="8px"
  />
</template>

<script setup>
import ProductImage from '@/components/ProductImage.vue'
</script>
```

**方式2：使用工具函数**
```vue
<template>
  <img :src="imageUrl" @error="handleError" />
</template>

<script setup>
import { getPrimaryImageUrl, getFallbackImageUrl } from '@/utils/imageLoader'

const imageUrl = ref(getPrimaryImageUrl(product))
const fallbackUrl = getFallbackImageUrl(product)

const handleError = () => {
  if (fallbackUrl && fallbackUrl !== imageUrl.value) {
    imageUrl.value = fallbackUrl
  }
}
</script>
```

**方式3：使用组合式函数**
```vue
<template>
  <div v-if="isLoading">加载中...</div>
  <img v-else-if="!loadError" :src="currentImageUrl" @load="handleLoad" @error="handleError" />
  <div v-else>加载失败</div>
</template>

<script setup>
import { useProductImage } from '@/utils/imageLoader'

const { 
  currentImageUrl, 
  loadError, 
  isLoading,
  handleLoad, 
  handleError 
} = useProductImage(ref(product))
</script>
```

## 4. 测试计划

### 4.1 测试场景

#### 场景1：只有 imageUrl 数据（历史数据）
**数据状态**:
```json
{
  "imageUrl": "http://example.com/apple.jpg",
  "imageUrls": null
}
```

**预期结果**:
- `primaryImageUrl`: `http://example.com/apple.jpg`
- `primaryImageType`: `absolute`
- `fallbackImageUrl`: `http://example.com/apple.jpg`
- 前端正常显示图片

#### 场景2：只有 imageUrls 数据（新数据）
**数据状态**:
```json
{
  "imageUrl": null,
  "imageUrls": "/uploads/apple.jpg,/uploads/apple2.jpg"
}
```

**预期结果**:
- `primaryImageUrl`: `/uploads/apple.jpg`
- `primaryImageType`: `relative`
- `fallbackImageUrl`: null
- `allImageUrls`: `["/uploads/apple.jpg", "/uploads/apple2.jpg"]`
- 前端正常显示图片

#### 场景3：两种数据都存在（混合数据）
**数据状态**:
```json
{
  "imageUrl": "http://example.com/apple.jpg",
  "imageUrls": "/uploads/apple.jpg,/uploads/apple2.jpg"
}
```

**预期结果**:
- `primaryImageUrl`: `/uploads/apple.jpg`（优先相对路径）
- `primaryImageType`: `relative`
- `fallbackImageUrl`: `http://example.com/apple.jpg`
- 前端优先加载相对路径，失败时降级到绝对路径

#### 场景4：无图片数据
**数据状态**:
```json
{
  "imageUrl": null,
  "imageUrls": null
}
```

**预期结果**:
- `primaryImageUrl`: null
- `primaryImageType`: `none`
- 前端显示占位图

### 4.2 测试用例

#### 后端单元测试
```java
@Test
public void testGetPrimaryImage_WithRelativePath() {
    Product product = new Product();
    product.setImageUrls("/uploads/apple.jpg,/uploads/apple2.jpg");
    
    ImagePathResult result = ImageUrlUtil.getPrimaryImage(product);
    
    assertEquals("/uploads/apple.jpg", result.getUrl());
    assertEquals(ImagePathType.RELATIVE, result.getType());
}

@Test
public void testGetPrimaryImage_WithAbsolutePathFallback() {
    Product product = new Product();
    product.setImageUrl("http://example.com/apple.jpg");
    
    ImagePathResult result = ImageUrlUtil.getPrimaryImage(product);
    
    assertEquals("http://example.com/apple.jpg", result.getUrl());
    assertEquals(ImagePathType.ABSOLUTE, result.getType());
}

@Test
public void testGetPrimaryImage_PriorityRelativeOverAbsolute() {
    Product product = new Product();
    product.setImageUrl("http://example.com/apple.jpg");
    product.setImageUrls("/uploads/apple.jpg");
    
    ImagePathResult result = ImageUrlUtil.getPrimaryImage(product);
    
    // 应该优先使用相对路径
    assertEquals("/uploads/apple.jpg", result.getUrl());
    assertEquals(ImagePathType.RELATIVE, result.getType());
}

@Test
public void testGetPrimaryImage_NoImage() {
    Product product = new Product();
    
    ImagePathResult result = ImageUrlUtil.getPrimaryImage(product);
    
    assertNull(result.getUrl());
    assertEquals(ImagePathType.NONE, result.getType());
}

@Test
public void testParseImageUrls() {
    String imageUrls = "/uploads/apple.jpg, /uploads/banana.jpg ,/uploads/orange.jpg";
    List<String> result = ImageUrlUtil.parseImageUrls(imageUrls);
    
    assertEquals(3, result.size());
    assertEquals("/uploads/apple.jpg", result.get(0));
    assertEquals("/uploads/banana.jpg", result.get(1));
    assertEquals("/uploads/orange.jpg", result.get(2));
}
```

#### 前端单元测试
```typescript
// imageLoader.test.ts
import { describe, it, expect } from 'vitest'
import { getPrimaryImageUrl, getAllImageUrls, getFallbackImageUrl, parseImageUrls } from './imageLoader'

describe('imageLoader', () => {
  describe('getPrimaryImageUrl', () => {
    it('should return primaryImageUrl when available', () => {
      const product = { primaryImageUrl: '/uploads/apple.jpg' }
      expect(getPrimaryImageUrl(product)).toBe('/uploads/apple.jpg')
    })

    it('should parse imageUrls and return first relative path', () => {
      const product = { imageUrls: '/uploads/apple.jpg,/uploads/banana.jpg' }
      expect(getPrimaryImageUrl(product)).toBe('/uploads/apple.jpg')
    })

    it('should add leading slash to relative path', () => {
      const product = { imageUrls: 'uploads/apple.jpg' }
      expect(getPrimaryImageUrl(product)).toBe('/uploads/apple.jpg')
    })

    it('should fallback to imageUrl when imageUrls is empty', () => {
      const product = { 
        imageUrl: 'http://example.com/apple.jpg',
        imageUrls: ''
      }
      expect(getPrimaryImageUrl(product)).toBe('http://example.com/apple.jpg')
    })

    it('should return empty string when no images', () => {
      const product = {}
      expect(getPrimaryImageUrl(product)).toBe('')
    })
  })

  describe('getAllImageUrls', () => {
    it('should return allImageUrls when available', () => {
      const product = { allImageUrls: ['/uploads/1.jpg', '/uploads/2.jpg'] }
      expect(getAllImageUrls(product)).toEqual(['/uploads/1.jpg', '/uploads/2.jpg'])
    })

    it('should parse imageUrls and return all paths', () => {
      const product = { imageUrls: '/uploads/1.jpg,/uploads/2.jpg,/uploads/3.jpg' }
      expect(getAllImageUrls(product)).toEqual(['/uploads/1.jpg', '/uploads/2.jpg', '/uploads/3.jpg'])
    })

    it('should fallback to imageUrl when no imageUrls', () => {
      const product = { imageUrl: 'http://example.com/apple.jpg' }
      expect(getAllImageUrls(product)).toEqual(['http://example.com/apple.jpg'])
    })
  })

  describe('getFallbackImageUrl', () => {
    it('should return fallbackImageUrl when available', () => {
      const product = { fallbackImageUrl: 'http://example.com/fallback.jpg' }
      expect(getFallbackImageUrl(product)).toBe('http://example.com/fallback.jpg')
    })

    it('should return imageUrl as fallback', () => {
      const product = { imageUrl: 'http://example.com/apple.jpg' }
      expect(getFallbackImageUrl(product)).toBe('http://example.com/apple.jpg')
    })
  })

  describe('parseImageUrls', () => {
    it('should parse comma separated urls', () => {
      const result = parseImageUrls('/uploads/1.jpg, /uploads/2.jpg ,/uploads/3.jpg')
      expect(result).toEqual(['/uploads/1.jpg', '/uploads/2.jpg', '/uploads/3.jpg'])
    })

    it('should return empty array for null', () => {
      expect(parseImageUrls(null)).toEqual([])
    })

    it('should return empty array for empty string', () => {
      expect(parseImageUrls('')).toEqual([])
    })
  })
})
```

### 4.3 集成测试

#### 测试步骤
1. **准备测试数据**
   ```sql
   -- 场景1: 只有绝对路径
   INSERT INTO products (name, price, stock, image_url, active) 
   VALUES ('测试商品1', 10.00, 100, 'http://localhost:8080/uploads/test1.jpg', true);

   -- 场景2: 只有相对路径
   INSERT INTO products (name, price, stock, image_urls, active) 
   VALUES ('测试商品2', 20.00, 100, '/uploads/test2.jpg,/uploads/test2_2.jpg', true);

   -- 场景3: 两种路径都有
   INSERT INTO products (name, price, stock, image_url, image_urls, active) 
   VALUES ('测试商品3', 30.00, 100, 'http://localhost:8080/uploads/test3.jpg', '/uploads/test3.jpg', true);

   -- 场景4: 无图片
   INSERT INTO products (name, price, stock, active) 
   VALUES ('测试商品4', 40.00, 100, true);
   ```

2. **API响应验证**
   ```bash
   # 调用商品列表接口
   curl http://localhost:8080/api/products
   
   # 验证每个商品的响应字段
   # - primaryImageUrl 是否正确
   # - primaryImageType 是否正确
   # - fallbackImageUrl 是否正确
   ```

3. **前端页面验证**
   - 打开首页，检查商品图片是否正常显示
   - 打开商品详情页，检查图片轮播是否正常
   - 检查网络面板，确认图片加载顺序
   - 模拟网络错误，验证降级机制

### 4.4 性能测试

#### 测试目标
- 验证图片加载策略不会影响页面性能
- 验证降级机制不会导致重复请求

#### 测试方法
```typescript
// 性能测试示例
const startTime = performance.now()

// 加载100个商品的图片
for (const product of products) {
  const url = getPrimaryImageUrl(product)
  // 模拟图片加载
}

const endTime = performance.now()
console.log(`图片URL处理耗时: ${endTime - startTime}ms`)
```

#### 预期结果
- 图片URL处理耗时 < 10ms（100个商品）
- 内存占用无明显增加
- 不会触发重复的图片请求

### 4.5 网络环境测试

#### 测试场景
1. **正常网络**: 相对路径和绝对路径都能正常加载
2. **慢网络**: 验证加载状态显示
3. **断网**: 验证错误处理和占位图显示
4. **部分阻断**: 阻断相对路径，验证降级到绝对路径

#### 测试方法
使用 Chrome DevTools Network 面板：
1. 打开 Network 面板
2. 右键请求选择 "Block request URL" 阻断相对路径
3. 刷新页面，验证是否自动降级到绝对路径
4. 观察图片是否正常显示

## 5. 部署检查清单

### 5.1 后端检查
- [ ] ImageUrlUtil.java 已添加到项目
- [ ] ProductDTO.java 已更新
- [ ] 所有返回 Product 的接口已改为返回 ProductDTO
- [ ] 单元测试通过

### 5.2 前端检查
- [ ] imageLoader.ts 已添加到项目
- [ ] ProductImage.vue 组件已创建
- [ ] Product 类型定义已更新
- [ ] 所有使用图片的页面已更新
- [ ] 单元测试通过

### 5.3 数据兼容性检查
- [ ] 历史数据（只有 imageUrl）正常显示
- [ ] 新数据（只有 imageUrls）正常显示
- [ ] 混合数据正常显示
- [ ] 无图片数据正常显示占位图

## 6. 回滚方案

如果出现问题，可以快速回滚：

### 后端回滚
1. 恢复原有的 Product 返回逻辑（不使用 ProductDTO）
2. 或者直接使用原有的 imageUrl 字段

### 前端回滚
1. 恢复原有的图片加载逻辑
2. 直接使用 `product.imageUrl` 而不是新的工具函数

## 7. 监控与日志

### 7.1 后端监控
```java
// 记录路径类型分布
log.info("图片路径类型统计 - relative: {}, absolute: {}, none: {}", 
    relativeCount, absoluteCount, noneCount);
```

### 7.2 前端监控
```typescript
// 记录图片加载失败和降级
if (useFallback.value) {
  console.warn(`[图片降级] 商品ID: ${product.id}, 从相对路径降级到绝对路径`)
}

if (loadError.value) {
  console.error(`[图片加载失败] 商品ID: ${product.id}`)
}
```

## 8. 总结

图片双保底策略实现了以下目标：

1. ✅ **向后兼容**: 保持现有字段不变，历史数据无需迁移
2. ✅ **智能降级**: 优先使用相对路径，失败时自动降级到绝对路径
3. ✅ **统一接口**: 提供清晰的API响应格式，便于前端处理
4. ✅ **稳定可靠**: 在各种网络环境下都能正常显示图片
5. ✅ **易于测试**: 提供完整的测试覆盖，确保策略正确性

该策略可以平滑过渡到新的图片存储方式，同时保证系统的稳定性和兼容性。
