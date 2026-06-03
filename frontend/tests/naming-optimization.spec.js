const { test, expect } = require('@playwright/test')

test.describe('前端命名规范优化验证', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/')
  })

  test('首页加载正常', async ({ page }) => {
    // 等待页面加载
    await page.waitForLoadState('networkidle')

    // 验证页面标题
    await expect(page).toHaveTitle(/online-mall|商城/)

    // 验证页面没有报错
    const errors = []
    page.on('pageerror', error => errors.push(error))
    await page.waitForTimeout(2000)
    expect(errors).toHaveLength(0)
  })

  test('商品卡片组件正常渲染', async ({ page }) => {
    await page.waitForLoadState('networkidle')

    // 验证商品卡片组件存在
    const productCards = page.locator('.product-card, .product-card-wrapper')
    await expect(productCards.first()).toBeVisible({ timeout: 10000 })
  })

  test('路由导航正常', async ({ page }) => {
    await page.waitForLoadState('networkidle')

    // 测试导航到商品列表页
    await page.goto('/products')
    await page.waitForLoadState('networkidle')

    // 验证页面加载
    await expect(page.locator('.product-list-container')).toBeVisible({ timeout: 10000 })
  })

  test('组件引用无报错', async ({ page }) => {
    const errors = []
    page.on('pageerror', error => errors.push(error))
    page.on('console', msg => {
      if (msg.type() === 'error') {
        errors.push(msg.text())
      }
    })

    // 访问多个页面
    const pages = ['/', '/products', '/hiking']
    for (const path of pages) {
      await page.goto(path)
      await page.waitForLoadState('networkidle')
      await page.waitForTimeout(1000)
    }

    // 过滤掉网络错误（后端未启动）
    const componentErrors = errors.filter(e =>
      !e.includes('NetworkError') &&
      !e.includes('fetch') &&
      !e.includes('api') &&
      !e.includes('404')
    )

    expect(componentErrors).toHaveLength(0)
  })
})
