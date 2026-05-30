// @ts-check
const { test, expect } = require('@playwright/test');

// 测试配置
const BASE_URL = 'http://localhost:8082/api';
const TEST_USER = {
  username: 'testuser',
  password: 'password123'
};

// 存储token和地址ID
let authToken = null;
let createdAddressId = null;
let defaultAddressId = null;

// 测试数据
const testAddress = {
  rcvrName: '测试用户',
  rcvrTel: '13800138000',
  prvcCde: '110000',
  cityCde: '110100',
  dstrctCde: '110105',
  dtlAddr: '望京SOHO T1 1001室',
  dftIndc: 'N',
};

const defaultAddress = {
  rcvrName: '默认用户',
  rcvrTel: '13900139000',
  prvcCde: '310000',
  cityCde: '310100',
  dstrctCde: '310101',
  dtlAddr: '陆家嘴金融中心 A座 2001室',
  dftIndc: 'Y',
};

/**
 * 获取认证头
 */
function getAuthHeaders() {
  return {
    'Authorization': `Bearer ${authToken}`,
    'Content-Type': 'application/json',
  };
}

test.describe('地址管理模块 - API测试', () => {

  test.beforeAll(async ({ request }) => {
    console.log('开始地址管理模块测试');

    // 尝试登录获取token
    try {
      const loginResponse = await request.post(`${BASE_URL}/user/login`, {
        data: {
          username: TEST_USER.username,
          password: TEST_USER.password,
        },
      });

      if (loginResponse.ok()) {
        const loginBody = await loginResponse.json();
        authToken = loginBody.data || loginBody.data?.token || loginBody.token;
        console.log('登录成功，获取到token');
      } else {
        console.log('登录失败，将使用mock模式测试');
        authToken = 'mock-token-for-testing';
      }
    } catch (e) {
      console.log('登录请求失败，将使用mock模式测试');
      authToken = 'mock-token-for-testing';
    }
  });

  test.afterAll(async ({ request }) => {
    // 清理测试数据
    if (authToken && authToken !== 'mock-token-for-testing') {
      if (createdAddressId) {
        try {
          await request.delete(`${BASE_URL}/address/${createdAddressId}`, {
            headers: getAuthHeaders(),
          });
        } catch (e) {
          // 忽略清理错误
        }
      }
      if (defaultAddressId) {
        try {
          await request.delete(`${BASE_URL}/address/${defaultAddressId}`, {
            headers: getAuthHeaders(),
          });
        } catch (e) {
          // 忽略清理错误
        }
      }
    }
    console.log('地址管理模块测试完成');
  });

  test.describe('1. 添加地址', () => {

    test('TC001: 添加非默认地址', async ({ request }) => {
      const response = await request.post(`${BASE_URL}/address/add`, {
        headers: getAuthHeaders(),
        data: testAddress,
      });

      expect(response.ok()).toBeTruthy();
      const body = await response.json();
      expect(body.code).toBe(200);
      expect(body.message).toBe('添加成功');
      expect(body.data).toBeDefined();
      expect(body.data.id).toBeDefined();
      expect(body.data.rcvrName).toBe(testAddress.rcvrName);
      expect(body.data.rcvrTel).toBe(testAddress.rcvrTel);
      expect(body.data.dftIndc).toBe('N');

      createdAddressId = body.data.id;
    });

    test('TC002: 添加默认地址', async ({ request }) => {
      const response = await request.post(`${BASE_URL}/address/add`, {
        headers: getAuthHeaders(),
        data: defaultAddress,
      });

      expect(response.ok()).toBeTruthy();
      const body = await response.json();
      expect(body.code).toBe(200);
      expect(body.data.dftIndc).toBe('Y');

      defaultAddressId = body.data.id;
    });

    test('TC003: 手机号格式验证失败', async ({ request }) => {
      const invalidAddress = { ...testAddress, rcvrTel: '12345678901' };
      const response = await request.post(`${BASE_URL}/address/add`, {
        headers: getAuthHeaders(),
        data: invalidAddress,
      });

      expect(response.status()).toBe(400);
    });

    test('TC004: 收货人姓名为空验证', async ({ request }) => {
      const invalidAddress = { ...testAddress, rcvrName: '' };
      const response = await request.post(`${BASE_URL}/address/add`, {
        headers: getAuthHeaders(),
        data: invalidAddress,
      });

      expect(response.status()).toBe(400);
    });

    test('TC005: 省份编码为空验证', async ({ request }) => {
      const invalidAddress = { ...testAddress, prvcCde: '' };
      const response = await request.post(`${BASE_URL}/address/add`, {
        headers: getAuthHeaders(),
        data: invalidAddress,
      });

      expect(response.status()).toBe(400);
    });
  });

  test.describe('2. 获取地址列表', () => {

    test('TC006: 获取地址列表成功', async ({ request }) => {
      const response = await request.get(`${BASE_URL}/address/list`, {
        headers: getAuthHeaders(),
      });

      expect(response.ok()).toBeTruthy();
      const body = await response.json();
      expect(body.code).toBe(200);
      expect(Array.isArray(body.data)).toBeTruthy();
    });

    test('TC007: 验证地址列表包含新添加的地址', async ({ request }) => {
      const response = await request.get(`${BASE_URL}/address/list`, {
        headers: getAuthHeaders(),
      });

      const body = await response.json();
      const addressIds = body.data.map(addr => addr.id);
      expect(addressIds).toContain(createdAddressId);
      expect(addressIds).toContain(defaultAddressId);
    });
  });

  test.describe('3. 获取地址详情', () => {

    test('TC008: 获取地址详情成功', async ({ request }) => {
      const response = await request.get(`${BASE_URL}/address/${createdAddressId}`, {
        headers: getAuthHeaders(),
      });

      expect(response.ok()).toBeTruthy();
      const body = await response.json();
      expect(body.code).toBe(200);
      expect(body.data.id).toBe(createdAddressId);
      expect(body.data.rcvrName).toBe(testAddress.rcvrName);
      expect(body.data.rcvrTel).toBe(testAddress.rcvrTel);
    });

    test('TC009: 获取不存在的地址', async ({ request }) => {
      const response = await request.get(`${BASE_URL}/address/not-exist-id`, {
        headers: getAuthHeaders(),
      });

      expect(response.status()).toBe(404);
    });
  });

  test.describe('4. 更新地址', () => {

    test('TC010: 更新地址成功', async ({ request }) => {
      const updateData = {
        id: createdAddressId,
        rcvrName: '更新后用户',
        rcvrTel: '13700137000',
        prvcCde: '440000',
        cityCde: '440100',
        dstrctCde: '440106',
        dtlAddr: '天河城广场 B座 3001室',
        dftIndc: 'N',
      };

      const response = await request.put(`${BASE_URL}/address/update`, {
        headers: getAuthHeaders(),
        data: updateData,
      });

      expect(response.ok()).toBeTruthy();
      const body = await response.json();
      expect(body.code).toBe(200);
      expect(body.message).toBe('更新成功');
      expect(body.data.rcvrName).toBe(updateData.rcvrName);
      expect(body.data.rcvrTel).toBe(updateData.rcvrTel);
    });

    test('TC011: 更新不存在的地址', async ({ request }) => {
      const updateData = {
        id: 'not-exist-id',
        rcvrName: '测试',
        rcvrTel: '13800138000',
        prvcCde: '110000',
        cityCde: '110100',
        dstrctCde: '110105',
        dtlAddr: '测试地址',
        dftIndc: 'N',
      };

      const response = await request.put(`${BASE_URL}/address/update`, {
        headers: getAuthHeaders(),
        data: updateData,
      });

      expect(response.status()).toBe(404);
    });
  });

  test.describe('5. 设置默认地址', () => {

    test('TC012: 设置默认地址成功', async ({ request }) => {
      const response = await request.put(`${BASE_URL}/address/default/${createdAddressId}`, {
        headers: getAuthHeaders(),
      });

      expect(response.ok()).toBeTruthy();
      const body = await response.json();
      expect(body.code).toBe(200);
      expect(body.message).toBe('设置成功');
    });

    test('TC013: 验证默认地址已更新', async ({ request }) => {
      const response = await request.get(`${BASE_URL}/address/${createdAddressId}`, {
        headers: getAuthHeaders(),
      });

      const body = await response.json();
      expect(body.data.dftIndc).toBe('Y');
    });

    test('TC014: 设置不存在的地址为默认', async ({ request }) => {
      const response = await request.put(`${BASE_URL}/address/default/not-exist-id`, {
        headers: getAuthHeaders(),
      });

      expect(response.status()).toBe(404);
    });
  });

  test.describe('6. 获取默认地址', () => {

    test('TC015: 获取默认地址成功', async ({ request }) => {
      const response = await request.get(`${BASE_URL}/address/default`, {
        headers: getAuthHeaders(),
      });

      expect(response.ok()).toBeTruthy();
      const body = await response.json();
      expect(body.code).toBe(200);
      expect(body.data).toBeDefined();
      expect(body.data.dftIndc).toBe('Y');
    });
  });

  test.describe('7. 删除地址', () => {

    test('TC016: 删除地址成功', async ({ request }) => {
      // 先创建一个待删除的地址
      const createResponse = await request.post(`${BASE_URL}/address/add`, {
        headers: getAuthHeaders(),
        data: {
          ...testAddress,
          rcvrName: '待删除用户',
          dftIndc: 'N',
        },
      });

      const createBody = await createResponse.json();
      const addressToDelete = createBody.data.id;

      // 删除地址
      const response = await request.delete(`${BASE_URL}/address/${addressToDelete}`, {
        headers: getAuthHeaders(),
      });

      expect(response.ok()).toBeTruthy();
      const body = await response.json();
      expect(body.code).toBe(200);
      expect(body.message).toBe('删除成功');
    });

    test('TC017: 删除不存在的地址', async ({ request }) => {
      const response = await request.delete(`${BASE_URL}/address/not-exist-id`, {
        headers: getAuthHeaders(),
      });

      expect(response.status()).toBe(404);
    });
  });

  test.describe('8. 性能测试', () => {

    test('TC018: 接口响应时间测试', async ({ request }) => {
      const iterations = 10;
      const responseTimes = [];

      for (let i = 0; i < iterations; i++) {
        const start = Date.now();
        await request.get(`${BASE_URL}/address/list`, {
          headers: getAuthHeaders(),
        });
        responseTimes.push(Date.now() - start);
      }

      const avgResponseTime = responseTimes.reduce((a, b) => a + b, 0) / iterations;
      console.log(`平均响应时间: ${avgResponseTime}ms`);

      // 验证平均响应时间小于100ms
      expect(avgResponseTime).toBeLessThan(100);
    });
  });

  test.describe('9. 数据一致性测试', () => {

    test('TC019: 更新后数据一致性验证', async ({ request }) => {
      // 更新地址
      const updateData = {
        id: createdAddressId,
        rcvrName: '一致性测试用户',
        rcvrTel: '13600136000',
        prvcCde: '440000',
        cityCde: '440100',
        dstrctCde: '440106',
        dtlAddr: '珠江新城 A座 1001室',
        dftIndc: 'Y',
      };

      await request.put(`${BASE_URL}/address/update`, {
        headers: getAuthHeaders(),
        data: updateData,
      });

      // 获取地址详情验证
      const response = await request.get(`${BASE_URL}/address/${createdAddressId}`, {
        headers: getAuthHeaders(),
      });

      const body = await response.json();
      expect(body.data.rcvrName).toBe(updateData.rcvrName);
      expect(body.data.rcvrTel).toBe(updateData.rcvrTel);
    });

    test('TC020: 删除后列表验证', async ({ request }) => {
      // 创建临时地址
      const createResponse = await request.post(`${BASE_URL}/address/add`, {
        headers: getAuthHeaders(),
        data: {
          ...testAddress,
          rcvrName: '临时用户',
          dftIndc: 'N',
        },
      });

      const createBody = await createResponse.json();
      const tempAddressId = createBody.data.id;

      // 获取当前列表数量
      const listBefore = await request.get(`${BASE_URL}/address/list`, {
        headers: getAuthHeaders(),
      });
      const countBefore = (await listBefore.json()).data.length;

      // 删除地址
      await request.delete(`${BASE_URL}/address/${tempAddressId}`, {
        headers: getAuthHeaders(),
      });

      // 验证列表数量减少
      const listAfter = await request.get(`${BASE_URL}/address/list`, {
        headers: getAuthHeaders(),
      });
      const countAfter = (await listAfter.json()).data.length;

      expect(countAfter).toBe(countBefore - 1);
    });
  });
});
