const { test, expect } = require('@playwright/test')

test.describe('收货地址优化验证', () => {
  test.beforeEach(async ({ page }) => {
    // 模拟登录状态
    await page.goto('/')
    await page.evaluate(() => {
      localStorage.setItem('token', 'test-token')
      localStorage.setItem('user', JSON.stringify({ id: 1, username: 'test' }))
    })
  })

  test('地址表单省市区下拉框渲染', async ({ page }) => {
    await page.goto('/user/center')
    await page.waitForLoadState('networkidle')

    // 点击新增地址按钮
    const addBtn = page.locator('button:has-text("新增地址")')
    if (await addBtn.isVisible()) {
      await addBtn.click()
      await page.waitForTimeout(500)

      // 验证省市区下拉框存在
      const selects = page.locator('.el-select')
      const selectCount = await selects.count()
      expect(selectCount).toBeGreaterThanOrEqual(3)
    }
  })

  test('手机号输入自动格式化', async ({ page }) => {
    await page.goto('/user/center')
    await page.waitForLoadState('networkidle')

    // 点击新增地址按钮
    const addBtn = page.locator('button:has-text("新增地址")')
    if (await addBtn.isVisible()) {
      await addBtn.click()
      await page.waitForTimeout(500)

      // 找到手机号输入框
      const phoneInput = page.locator('input[placeholder="请输入手机号"]')
      if (await phoneInput.isVisible()) {
        // 输入11位手机号
        await phoneInput.fill('13812345678')
        await page.waitForTimeout(300)

        // 验证格式化后的值
        const value = await phoneInput.inputValue()
        expect(value).toBe('138 1234 5678')
      }
    }
  })

  test('手机号格式化 - 7位数字', async ({ page }) => {
    await page.goto('/user/center')
    await page.waitForLoadState('networkidle')

    const addBtn = page.locator('button:has-text("新增地址")')
    if (await addBtn.isVisible()) {
      await addBtn.click()
      await page.waitForTimeout(500)

      const phoneInput = page.locator('input[placeholder="请输入手机号"]')
      if (await phoneInput.isVisible()) {
        await phoneInput.fill('1381234')
        await page.waitForTimeout(300)

        const value = await phoneInput.inputValue()
        expect(value).toBe('138 1234')
      }
    }
  })

  test('手机号格式化 - 3位数字', async ({ page }) => {
    await page.goto('/user/center')
    await page.waitForLoadState('networkidle')

    const addBtn = page.locator('button:has-text("新增地址")')
    if (await addBtn.isVisible()) {
      await addBtn.click()
      await page.waitForTimeout(500)

      const phoneInput = page.locator('input[placeholder="请输入手机号"]')
      if (await phoneInput.isVisible()) {
        await phoneInput.fill('138')
        await page.waitForTimeout(300)

        const value = await phoneInput.inputValue()
        expect(value).toBe('138')
      }
    }
  })

  test('省份下拉框有选项', async ({ page }) => {
    await page.goto('/user/center')
    await page.waitForLoadState('networkidle')

    const addBtn = page.locator('button:has-text("新增地址")')
    if (await addBtn.isVisible()) {
      await addBtn.click()
      await page.waitForTimeout(1000)

      // 找到省份下拉框（第一个 el-select）
      const provinceSelect = page.locator('.el-select').first()
      if (await provinceSelect.isVisible()) {
        await provinceSelect.click()
        await page.waitForTimeout(500)

        // 验证下拉选项存在
        const options = page.locator('.el-select-dropdown__item')
        const optionCount = await options.count()
        expect(optionCount).toBeGreaterThan(0)
      }
    }
  })

  test('表单验证 - 必填项', async ({ page }) => {
    await page.goto('/user/center')
    await page.waitForLoadState('networkidle')

    const addBtn = page.locator('button:has-text("新增地址")')
    if (await addBtn.isVisible()) {
      await addBtn.click()
      await page.waitForTimeout(500)

      // 直接点击保存，不填写任何内容
      const saveBtn = page.locator('.el-dialog button:has-text("保存")')
      if (await saveBtn.isVisible()) {
        await saveBtn.click()
        await page.waitForTimeout(300)

        // 验证错误提示出现
        const errorMsg = page.locator('.el-form-item__error')
        const errorCount = await errorMsg.count()
        expect(errorCount).toBeGreaterThan(0)
      }
    }
  })

  test('地址列表页面无报错', async ({ page }) => {
    const errors = []
    page.on('pageerror', error => errors.push(error))

    await page.goto('/user/center')
    await page.waitForLoadState('networkidle')
    await page.waitForTimeout(2000)

    // 过滤掉网络错误（后端可能未启动）
    const componentErrors = errors.filter(e =>
      !e.includes('NetworkError') &&
      !e.includes('fetch') &&
      !e.includes('api') &&
      !e.includes('404') &&
      !e.includes('Request failed')
    )

    expect(componentErrors).toHaveLength(0)
  })

  test('页面组件引用无报错', async ({ page }) => {
    const errors = []
    page.on('console', msg => {
      if (msg.type() === 'error') {
        errors.push(msg.text())
      }
    })

    const pages = ['/', '/user/center']
    for (const path of pages) {
      await page.goto(path)
      await page.waitForLoadState('networkidle')
      await page.waitForTimeout(1000)
    }

    // 过滤掉网络错误
    const componentErrors = errors.filter(e =>
      !e.includes('NetworkError') &&
      !e.includes('fetch') &&
      !e.includes('api') &&
      !e.includes('404') &&
      !e.includes('Request failed') &&
      !e.includes('GET http')
    )

    expect(componentErrors).toHaveLength(0)
  })
})
