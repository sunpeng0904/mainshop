# 设计文档 — 收货地址优化

## 1. 设计概述

### 1.1 设计目标
将收货地址表单的省市区文本输入改为三级联动下拉选择，并增加手机号格式化功能。

### 1.2 设计原则
- 复用已有的 Region API
- 最小化改动，只修改地址表单部分
- 保持现有地址列表和 CRUD 功能不变

## 2. 技术方案

### 2.1 省市三级联动

**方案选择**：使用 Element Plus 的 `el-select` 组件实现三级联动

**实现逻辑**：
```
页面加载 → 获取省份列表 → 填充省下拉框
选择省份 → 调用 /region/cities/{code} → 填充市下拉框
选择城市 → 调用 /region/districts/{code} → 填充区下拉框
```

**数据结构**：
```javascript
const provinces = ref([])   // 省份列表
const cities = ref([])      // 城市列表
const districts = ref([])   // 区县列表

const addressForm = reactive({
  provinceCode: '',   // 省份编码
  cityCode: '',       // 城市编码
  districtCode: '',   // 区县编码
  // ... 其他字段
})
```

**联动逻辑**：
```javascript
// 选择省份
const handleProvinceChange = async (provinceCode) => {
  addressForm.cityCode = ''
  addressForm.districtCode = ''
  cities.value = []
  districts.value = []
  if (provinceCode) {
    const res = await getCities(provinceCode)
    cities.value = res.data
  }
}

// 选择城市
const handleCityChange = async (cityCode) => {
  addressForm.districtCode = ''
  districts.value = []
  if (cityCode) {
    const res = await getDistricts(cityCode)
    districts.value = res.data
  }
}
```

### 2.2 手机号格式化

**实现方式**：使用计算属性或输入事件格式化手机号

**显示格式**：`138 1234 5678`（3-4-4 分隔）

**实现逻辑**：
```javascript
const formatPhone = (value) => {
  const cleaned = value.replace(/\D/g, '')
  const limited = cleaned.slice(0, 11)
  if (limited.length <= 3) return limited
  if (limited.length <= 7) return `${limited.slice(0, 3)} ${limited.slice(3)}`
  return `${limited.slice(0, 3)} ${limited.slice(3, 7)} ${limited.slice(7)}`
}
```

## 3. 前端改动

### 3.1 文件改动
| 文件 | 改动内容 |
|------|----------|
| `src/views/user/center.vue` | 地址表单改为三级联动，手机号格式化 |
| `src/api/address.js` | 新增获取省市列表的 API 调用方法 |

### 3.2 新增 API 方法
```javascript
// 获取省份列表
export function getProvinces() {
  return request.get('/region/provinces')
}

// 获取城市列表
export function getCities(provinceCode) {
  return request.get(`/region/cities/${provinceCode}`)
}

// 获取区县列表
export function getDistricts(cityCode) {
  return request.get(`/region/districts/${cityCode}`)
}
```

### 3.3 表单模板改动

**改动前**：
```html
<el-input v-model="addressForm.province" placeholder="省" />
<el-input v-model="addressForm.city" placeholder="市" />
<el-input v-model="addressForm.district" placeholder="区" />
```

**改动后**：
```html
<el-select v-model="addressForm.provinceCode" placeholder="请选择省" @change="handleProvinceChange">
  <el-option v-for="p in provinces" :key="p.cde" :label="p.name" :value="p.cde" />
</el-select>
<el-select v-model="addressForm.cityCode" placeholder="请选择市" @change="handleCityChange">
  <el-option v-for="c in cities" :key="c.cde" :label="c.name" :value="c.cde" />
</el-select>
<el-select v-model="addressForm.districtCode" placeholder="请选择区">
  <el-option v-for="d in districts" :key="d.cde" :label="d.name" :value="d.cde" />
</el-select>
```

## 4. 字段映射处理

### 4.1 提交时转换
```javascript
// 前端 → 后端
const submitData = {
  rcvrName: addressForm.receiverName,
  rcvrTel: addressForm.receiverPhone.replace(/\s/g, ''),
  prvcCde: addressForm.provinceCode,
  cityCde: addressForm.cityCode,
  dstrctCde: addressForm.districtCode,
  dtlAddr: addressForm.detailAddress,
  dftIndc: addressForm.isDefault ? 'Y' : 'N'
}
```

### 4.2 编辑时转换
```javascript
// 后端 → 前端
const fillForm = (data) => {
  addressForm.receiverName = data.rcvrName
  addressForm.receiverPhone = data.rcvrTel
  addressForm.provinceCode = data.prvcCde
  addressForm.cityCode = data.cityCde
  addressForm.districtCode = data.dstrctCde
  addressForm.detailAddress = data.dtlAddr
  addressForm.isDefault = data.dftIndc === 'Y' ? 1 : 0
}
```

## 5. 风险与应对

| 风险 | 应对措施 |
|------|----------|
| 省市数据加载慢 | 页面加载时预获取省份数据 |
| 编辑回显时城市/区县数据未加载 | 先加载省份→城市→区县，再回显选中值 |
| 字段映射不匹配 | 统一在提交和回显时做转换 |

## 6. 验收标准

- [ ] 省市区使用下拉选择
- [ ] 三级联动正常工作
- [ ] 编辑时正确回显
- [ ] 手机号格式化显示
- [ ] 地址 CRUD 功能正常
- [ ] 构建成功
