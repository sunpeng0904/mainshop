import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createStore } from 'vuex'
import { createRouter, createWebHistory } from 'vue-router'
import Home from './index.vue'

// Mock store
const mockStore = createStore({
  modules: {
    product: {
      namespaced: true,
      actions: {
        getCategories: vi.fn().mockResolvedValue([]),
        getHotProducts: vi.fn().mockResolvedValue([]),
        getNewProducts: vi.fn().mockResolvedValue([])
      }
    },
    cart: {
      namespaced: true,
      actions: {
        addToCart: vi.fn().mockResolvedValue(true)
      }
    },
    user: {
      namespaced: true,
      actions: {
        addFavorite: vi.fn().mockResolvedValue(true),
        removeFavorite: vi.fn().mockResolvedValue(true)
      }
    }
  }
})

// Mock router
const mockRouter = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: Home },
    { path: '/products', component: { template: '<div>Products</div>' } }
  ]
})

// Mock i18n
const mockI18n = {
  t: (key) => key,
  locale: { value: 'zh-CN' }
}

describe('Home Page', () => {
  let wrapper

  beforeEach(() => {
    wrapper = mount(Home, {
      global: {
        plugins: [mockStore, mockRouter],
        mocks: {
          $t: mockI18n.t
        },
        stubs: {
          'el-carousel': true,
          'el-carousel-item': true,
          'el-input': true,
          'el-button': true,
          'el-icon': true,
          'el-empty': true,
          'el-backtop': true,
          'el-skeleton': true,
          'ProductCard': true,
          'ProductPreview': true
        }
      }
    })
  })

  it('renders correctly', () => {
    expect(wrapper.exists()).toBe(true)
  })

  it('has search section', () => {
    expect(wrapper.find('.search-section').exists()).toBe(true)
  })

  it('has hero section with carousel', () => {
    expect(wrapper.find('.hero-section').exists()).toBe(true)
  })

  it('has stats section', () => {
    expect(wrapper.find('.stats-section').exists()).toBe(true)
  })

  it('has categories section', () => {
    expect(wrapper.find('.categories-section').exists()).toBe(true)
  })

  it('has hot products section', () => {
    expect(wrapper.find('.hot-section').exists()).toBe(true)
  })

  it('has new products section', () => {
    expect(wrapper.find('.new-section').exists()).toBe(true)
  })

  it('has brand story section', () => {
    expect(wrapper.find('.brand-story-section').exists()).toBe(true)
  })

  it('handles search correctly', async () => {
    const searchInput = wrapper.find('.search-input')
    await searchInput.setValue('test keyword')
    
    const searchButton = wrapper.find('.search-input .el-input-group__append .el-button')
    await searchButton.trigger('click')
    
    expect(mockRouter.currentRoute.value.query.keyword).toBe('test keyword')
  })

  it('shows loading skeletons when data is loading', () => {
    expect(wrapper.find('.category-skeleton').exists()).toBe(true)
    expect(wrapper.find('.product-skeleton').exists()).toBe(true)
    expect(wrapper.find('.new-product-skeleton').exists()).toBe(true)
  })

  it('renders categories after loading', async () => {
    // Mock categories data
    mockStore.dispatch = vi.fn().mockResolvedValue([
      { id: 1, name: 'Category 1', icon: 'Folder' },
      { id: 2, name: 'Category 2', icon: 'Folder' }
    ])

    // Trigger data fetch
    await wrapper.vm.fetchData()
    await wrapper.vm.$nextTick()

    expect(wrapper.findAll('.category-card')).toHaveLength(2)
  })

  it('renders hot products after loading', async () => {
    // Mock hot products data
    mockStore.dispatch = vi.fn().mockResolvedValue([
      { id: 1, name: 'Product 1', price: 100, image: 'test.jpg' },
      { id: 2, name: 'Product 2', price: 200, image: 'test.jpg' }
    ])

    // Trigger data fetch
    await wrapper.vm.fetchData()
    await wrapper.vm.$nextTick()

    expect(wrapper.findAll('.product-card-wrapper')).toHaveLength(2)
  })

  it('renders new products after loading', async () => {
    // Mock new products data
    mockStore.dispatch = vi.fn().mockResolvedValue([
      { id: 1, name: 'New Product 1', price: 150, image: 'test.jpg' },
      { id: 2, name: 'New Product 2', price: 250, image: 'test.jpg' }
    ])

    // Trigger data fetch
    await wrapper.vm.fetchData()
    await wrapper.vm.$nextTick()

    expect(wrapper.findAll('.new-product-card')).toHaveLength(2)
  })

  it('handles add to cart correctly', async () => {
    const product = { id: 1, name: 'Test Product', price: 100 }
    
    await wrapper.vm.addToCart(product)
    
    expect(mockStore.dispatch).toHaveBeenCalledWith('cart/addToCart', {
      productId: 1,
      quantity: 1
    })
  })

  it('handles toggle favorite correctly', async () => {
    const product = { id: 1, name: 'Test Product' }
    
    await wrapper.vm.toggleFavorite(product)
    
    expect(mockStore.dispatch).toHaveBeenCalledWith('user/addFavorite', 1)
  })

  it('shows product preview when preview is triggered', async () => {
    const product = { id: 1, name: 'Test Product', price: 100 }
    
    await wrapper.vm.showProductPreview(product)
    
    expect(wrapper.vm.previewVisible).toBe(true)
    expect(wrapper.vm.previewProduct).toEqual(product)
  })
})
