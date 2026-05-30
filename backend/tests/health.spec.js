// @ts-check
const { test, expect } = require('@playwright/test');

test.describe('健康检查', () => {

  test('应用服务可访问', async ({ request }) => {
    const response = await request.get('/actuator/health');
    expect(response.ok()).toBeTruthy();
    const body = await response.json();
    expect(body.status).toBe('UP');
  });

  test('Swagger文档可访问', async ({ request }) => {
    const response = await request.get('/swagger-ui.html');
    expect(response.ok()).toBeTruthy();
  });
});
