// @ts-check
const { test, expect } = require('@playwright/test');

const BASE_URL = '/api/address';
const AUTH_HEADERS = {
  'Authorization': 'Bearer test-token',
  'X-User-Id': 'user-001',
};

test.describe('安全测试', () => {

  test('TC-S01: SQL注入防护 - 地址ID参数', async ({ request }) => {
    const sqlInjectionPayloads = [
      "' OR '1'='1",
      "'; DROP TABLE user_addr_tb; --",
      "1' UNION SELECT * FROM user_addr_tb --",
      "admin'--",
    ];

    for (const payload of sqlInjectionPayloads) {
      const response = await request.get(`${BASE_URL}/${encodeURIComponent(payload)}`, {
        headers: AUTH_HEADERS,
      });

      // 应该返回400或404，而不是500
      expect(response.status()).toBeLessThan(500);
    }
  });

  test('TC-S02: SQL注入防护 - 请求体参数', async ({ request }) => {
    const sqlInjectionPayloads = [
      { rcvrName: "' OR '1'='1" },
      { rcvrTel: "'; DROP TABLE user_addr_tb; --" },
      { dtlAddr: "1' UNION SELECT * FROM user_addr_tb --" },
    ];

    for (const payload of sqlInjectionPayloads) {
      const response = await request.post(`${BASE_URL}/add`, {
        headers: AUTH_HEADERS,
        data: {
          rcvrName: '测试用户',
          rcvrTel: '13800138000',
          prvcCde: '110000',
          cityCde: '110100',
          dstrctCde: '110105',
          dtlAddr: '测试地址',
          dftIndc: 'N',
          ...payload,
        },
      });

      // 应该返回400或成功，而不是500
      expect(response.status()).toBeLessThan(500);
    }
  });

  test('TC-S03: XSS防护', async ({ request }) => {
    const xssPayloads = [
      '<script>alert("xss")</script>',
      '<img src="x" onerror="alert(1)">',
      'javascript:alert(1)',
      '<svg onload="alert(1)">',
    ];

    for (const payload of xssPayloads) {
      const response = await request.post(`${BASE_URL}/add`, {
        headers: AUTH_HEADERS,
        data: {
          rcvrName: payload,
          rcvrTel: '13800138000',
          prvcCde: '110000',
          cityCde: '110100',
          dstrctCde: '110105',
          dtlAddr: '测试地址',
          dftIndc: 'N',
        },
      });

      if (response.ok()) {
        const body = await response.json();
        // 验证返回的数据不包含原始XSS payload
        expect(body.data.rcvrName).not.toBe(payload);
      }
    }
  });

  test('TC-S04: 超长输入防护', async ({ request }) => {
    const longString = 'A'.repeat(10000);

    const response = await request.post(`${BASE_URL}/add`, {
      headers: AUTH_HEADERS,
      data: {
        rcvrName: longString,
        rcvrTel: '13800138000',
        prvcCde: '110000',
        cityCde: '110100',
        dstrctCde: '110105',
        dtlAddr: longString,
        dftIndc: 'N',
      },
    });

    // 应该返回400验证错误
    expect(response.status()).toBe(400);
  });

  test('TC-S05: 未授权访问防护', async ({ request }) => {
    const response = await request.get(`${BASE_URL}/list`);

    // 应该返回401未授权
    expect(response.status()).toBe(401);
  });

  test('TC-S06: 越权访问防护', async ({ request }) => {
    // 创建一个地址
    const createResponse = await request.post(`${BASE_URL}/add`, {
      headers: AUTH_HEADERS,
      data: {
        rcvrName: '测试用户',
        rcvrTel: '13800138000',
        prvcCde: '110000',
        cityCde: '110100',
        dstrctCde: '110105',
        dtlAddr: '测试地址',
        dftIndc: 'N',
      },
    });

    if (createResponse.ok()) {
      const createBody = await createResponse.json();
      const addressId = createBody.data.id;

      // 使用其他用户尝试访问
      const otherUserHeaders = {
        'Authorization': 'Bearer test-token',
        'X-User-Id': 'user-002',
      };

      const response = await request.get(`${BASE_URL}/${addressId}`, {
        headers: otherUserHeaders,
      });

      // 应该返回404（不允许访问其他用户的地址）
      expect(response.status()).toBe(404);
    }
  });

  test('TC-S07: 参数类型验证', async ({ request }) => {
    const invalidData = {
      rcvrName: 123,  // 应该是字符串
      rcvrTel: true,  // 应该是字符串
      prvcCde: [],    // 应该是字符串
      cityCde: {},    // 应该是字符串
      dstrctCde: null,
      dtlAddr: undefined,
      dftIndc: 'N',
    };

    const response = await request.post(`${BASE_URL}/add`, {
      headers: AUTH_HEADERS,
      data: invalidData,
    });

    // 应该返回400验证错误
    expect(response.status()).toBe(400);
  });
});
