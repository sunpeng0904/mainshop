# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: address-optimization.spec.js >> 收货地址优化验证 >> 省份下拉框有选项
- Location: tests\address-optimization.spec.js:94:3

# Error details

```
Test timeout of 30000ms exceeded while running "beforeEach" hook.
```

# Test source

```ts
  1   | const { test, expect } = require('@playwright/test')
  2   | 
  3   | test.describe('收货地址优化验证', () => {
> 4   |   test.beforeEach(async ({ page }) => {
      |        ^ Test timeout of 30000ms exceeded while running "beforeEach" hook.
  5   |     // 模拟登录状态
  6   |     await page.goto('/')
  7   |     await page.evaluate(() => {
  8   |       localStorage.setItem('token', 'test-token')
  9   |       localStorage.setItem('user', JSON.stringify({ id: 1, username: 'test' }))
  10  |     })
  11  |   })
  12  | 
  13  |   test('地址表单省市区下拉框渲染', async ({ page }) => {
  14  |     await page.goto('/user/center')
  15  |     await page.waitForLoadState('networkidle')
  16  | 
  17  |     // 点击新增地址按钮
  18  |     const addBtn = page.locator('button:has-text("新增地址")')
  19  |     if (await addBtn.isVisible()) {
  20  |       await addBtn.click()
  21  |       await page.waitForTimeout(500)
  22  | 
  23  |       // 验证省市区下拉框存在
  24  |       const selects = page.locator('.el-select')
  25  |       const selectCount = await selects.count()
  26  |       expect(selectCount).toBeGreaterThanOrEqual(3)
  27  |     }
  28  |   })
  29  | 
  30  |   test('手机号输入自动格式化', async ({ page }) => {
  31  |     await page.goto('/user/center')
  32  |     await page.waitForLoadState('networkidle')
  33  | 
  34  |     // 点击新增地址按钮
  35  |     const addBtn = page.locator('button:has-text("新增地址")')
  36  |     if (await addBtn.isVisible()) {
  37  |       await addBtn.click()
  38  |       await page.waitForTimeout(500)
  39  | 
  40  |       // 找到手机号输入框
  41  |       const phoneInput = page.locator('input[placeholder="请输入手机号"]')
  42  |       if (await phoneInput.isVisible()) {
  43  |         // 输入11位手机号
  44  |         await phoneInput.fill('13812345678')
  45  |         await page.waitForTimeout(300)
  46  | 
  47  |         // 验证格式化后的值
  48  |         const value = await phoneInput.inputValue()
  49  |         expect(value).toBe('138 1234 5678')
  50  |       }
  51  |     }
  52  |   })
  53  | 
  54  |   test('手机号格式化 - 7位数字', async ({ page }) => {
  55  |     await page.goto('/user/center')
  56  |     await page.waitForLoadState('networkidle')
  57  | 
  58  |     const addBtn = page.locator('button:has-text("新增地址")')
  59  |     if (await addBtn.isVisible()) {
  60  |       await addBtn.click()
  61  |       await page.waitForTimeout(500)
  62  | 
  63  |       const phoneInput = page.locator('input[placeholder="请输入手机号"]')
  64  |       if (await phoneInput.isVisible()) {
  65  |         await phoneInput.fill('1381234')
  66  |         await page.waitForTimeout(300)
  67  | 
  68  |         const value = await phoneInput.inputValue()
  69  |         expect(value).toBe('138 1234')
  70  |       }
  71  |     }
  72  |   })
  73  | 
  74  |   test('手机号格式化 - 3位数字', async ({ page }) => {
  75  |     await page.goto('/user/center')
  76  |     await page.waitForLoadState('networkidle')
  77  | 
  78  |     const addBtn = page.locator('button:has-text("新增地址")')
  79  |     if (await addBtn.isVisible()) {
  80  |       await addBtn.click()
  81  |       await page.waitForTimeout(500)
  82  | 
  83  |       const phoneInput = page.locator('input[placeholder="请输入手机号"]')
  84  |       if (await phoneInput.isVisible()) {
  85  |         await phoneInput.fill('138')
  86  |         await page.waitForTimeout(300)
  87  | 
  88  |         const value = await phoneInput.inputValue()
  89  |         expect(value).toBe('138')
  90  |       }
  91  |     }
  92  |   })
  93  | 
  94  |   test('省份下拉框有选项', async ({ page }) => {
  95  |     await page.goto('/user/center')
  96  |     await page.waitForLoadState('networkidle')
  97  | 
  98  |     const addBtn = page.locator('button:has-text("新增地址")')
  99  |     if (await addBtn.isVisible()) {
  100 |       await addBtn.click()
  101 |       await page.waitForTimeout(1000)
  102 | 
  103 |       // 找到省份下拉框（第一个 el-select）
  104 |       const provinceSelect = page.locator('.el-select').first()
```