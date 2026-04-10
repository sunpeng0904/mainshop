/**
 * Mock 数据
 * 用于后端接口未实现时的前端开发
 */

// Mock 商品数据
export const mockProducts = [
  {
    id: 1,
    name: 'iPhone 15 Pro',
    categoryId: 4,
    price: 8999.00,
    originalPrice: 9999.00,
    stock: 100,
    sales: 1580,
    images: ['https://picsum.photos/400/400?random=1', 'https://picsum.photos/400/400?random=2'],
    description: '苹果最新旗舰手机，A17 Pro芯片，钛金属设计',
    detail: '<p>iPhone 15 Pro 采用钛金属设计，配备 A17 Pro 芯片，是目前最强大的 iPhone。</p>',
    status: 1
  },
  {
    id: 2,
    name: '华为 Mate 60 Pro',
    categoryId: 4,
    price: 6999.00,
    originalPrice: 7999.00,
    stock: 150,
    sales: 2300,
    images: ['https://picsum.photos/400/400?random=3', 'https://picsum.photos/400/400?random=4'],
    description: '华为旗舰手机，麒麟芯片回归',
    detail: '<p>华为 Mate 60 Pro 搭载麒麟9000S芯片，支持卫星通信。</p>',
    status: 1
  },
  {
    id: 3,
    name: 'MacBook Pro 16寸',
    categoryId: 5,
    price: 18999.00,
    originalPrice: 19999.00,
    stock: 50,
    sales: 890,
    images: ['https://picsum.photos/400/400?random=5', 'https://picsum.photos/400/400?random=6'],
    description: '苹果专业笔记本电脑，M3 Pro/Max芯片',
    detail: '<p>MacBook Pro 16寸搭载 M3 Pro 或 M3 Max 芯片，为专业人士打造。</p>',
    status: 1
  },
  {
    id: 4,
    name: '联想 ThinkPad X1 Carbon',
    categoryId: 5,
    price: 12999.00,
    originalPrice: 13999.00,
    stock: 80,
    sales: 650,
    images: ['https://picsum.photos/400/400?random=7', 'https://picsum.photos/400/400?random=8'],
    description: '商务笔记本电脑，轻薄便携',
    detail: '<p>ThinkPad X1 Carbon 是商务人士的首选，轻薄且性能强大。</p>',
    status: 1
  },
  {
    id: 5,
    name: '男士休闲衬衫',
    categoryId: 6,
    price: 299.00,
    originalPrice: 399.00,
    stock: 200,
    sales: 3500,
    images: ['https://picsum.photos/400/400?random=9', 'https://picsum.photos/400/400?random=10'],
    description: '纯棉男士衬衫，舒适透气',
    detail: '<p>100%纯棉材质，舒适透气，适合日常穿着。</p>',
    status: 1
  },
  {
    id: 6,
    name: '女士连衣裙',
    categoryId: 7,
    price: 499.00,
    originalPrice: 599.00,
    stock: 150,
    sales: 4200,
    images: ['https://picsum.photos/400/400?random=11', 'https://picsum.photos/400/400?random=12'],
    description: '夏季新款连衣裙，优雅大方',
    detail: '<p>夏季新款，多种颜色可选，优雅大方。</p>',
    status: 1
  }
]

// Mock 分类数据
export const mockCategories = [
  { id: 1, name: '电子产品', parentId: 0, level: 1, icon: 'Monitor', children: [
    { id: 4, name: '手机', parentId: 1, level: 2 },
    { id: 5, name: '笔记本电脑', parentId: 1, level: 2 }
  ]},
  { id: 2, name: '服装鞋帽', parentId: 0, level: 1, icon: 'Shirt', children: [
    { id: 6, name: '男装', parentId: 2, level: 2 },
    { id: 7, name: '女装', parentId: 2, level: 2 }
  ]},
  { id: 3, name: '家居生活', parentId: 0, level: 1, icon: 'House', children: [] }
]

// Mock 购物车数据
export const mockCarts = [
  {
    id: 1,
    productId: 1,
    productName: 'iPhone 15 Pro',
    productImage: 'https://picsum.photos/100/100?random=1',
    price: 8999.00,
    quantity: 1,
    selected: 1,
    stock: 100
  },
  {
    id: 2,
    productId: 3,
    productName: 'MacBook Pro 16寸',
    productImage: 'https://picsum.photos/100/100?random=5',
    price: 18999.00,
    quantity: 1,
    selected: 1,
    stock: 50
  }
]

// Mock 订单数据
export const mockOrders = [
  {
    id: 1,
    orderNo: '202604080001',
    totalAmount: 8999.00,
    payAmount: 8999.00,
    status: 0,
    statusText: '待付款',
    receiverName: '张三',
    receiverPhone: '13800138000',
    receiverAddress: '北京市朝阳区建国路88号',
    createTime: '2026-04-08 10:30:00',
    items: [
      {
        productId: 1,
        productName: 'iPhone 15 Pro',
        productImage: 'https://picsum.photos/100/100?random=1',
        price: 8999.00,
        quantity: 1,
        totalAmount: 8999.00
      }
    ]
  },
  {
    id: 2,
    orderNo: '202604070002',
    totalAmount: 19298.00,
    payAmount: 19298.00,
    status: 2,
    statusText: '已发货',
    receiverName: '张三',
    receiverPhone: '13800138000',
    receiverAddress: '北京市朝阳区建国路88号',
    createTime: '2026-04-07 14:20:00',
    deliveryTime: '2026-04-08 09:00:00',
    items: [
      {
        productId: 5,
        productName: '男士休闲衬衫',
        productImage: 'https://picsum.photos/100/100?random=9',
        price: 299.00,
        quantity: 3,
        totalAmount: 897.00
      },
      {
        productId: 6,
        productName: '女士连衣裙',
        productImage: 'https://picsum.photos/100/100?random=11',
        price: 499.00,
        quantity: 2,
        totalAmount: 998.00
      }
    ]
  }
]

// Mock 地址数据
export const mockAddresses = [
  {
    id: 1,
    receiverName: '张三',
    receiverPhone: '13800138000',
    province: '北京市',
    city: '北京市',
    district: '朝阳区',
    detailAddress: '建国路88号',
    isDefault: 1
  },
  {
    id: 2,
    receiverName: '李四',
    receiverPhone: '13800138001',
    province: '上海市',
    city: '上海市',
    district: '浦东新区',
    detailAddress: '张江高科技园区',
    isDefault: 0
  }
]

// 订单状态映射
export const orderStatusMap = {
  0: { text: '待付款', type: 'warning' },
  1: { text: '待发货', type: 'primary' },
  2: { text: '已发货', type: 'info' },
  3: { text: '已完成', type: 'success' },
  4: { text: '已取消', type: 'danger' }
}

/**
 * 模拟延迟返回数据
 */
export function mockDelay(data, delay = 300) {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve({
        code: 200,
        message: 'success',
        data,
        timestamp: Date.now()
      })
    }, delay)
  })
}
