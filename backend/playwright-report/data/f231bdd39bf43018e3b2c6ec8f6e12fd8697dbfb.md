# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: address.spec.js >> 地址管理模块 - API测试 >> 9. 数据一致性测试 >> TC019: 更新后数据一致性验证
- Location: tests\address.spec.js:369:5

# Error details

```
TypeError: Cannot read properties of null (reading 'rcvrName')
```

# Test source

```ts
  293 | 
  294 |   test.describe('6. 获取默认地址', () => {
  295 | 
  296 |     test('TC015: 获取默认地址成功', async ({ request }) => {
  297 |       const response = await request.get(`${BASE_URL}/address/default`, {
  298 |         headers: getAuthHeaders(),
  299 |       });
  300 | 
  301 |       expect(response.ok()).toBeTruthy();
  302 |       const body = await response.json();
  303 |       expect(body.code).toBe(200);
  304 |       expect(body.data).toBeDefined();
  305 |       expect(body.data.dftIndc).toBe('Y');
  306 |     });
  307 |   });
  308 | 
  309 |   test.describe('7. 删除地址', () => {
  310 | 
  311 |     test('TC016: 删除地址成功', async ({ request }) => {
  312 |       // 先创建一个待删除的地址
  313 |       const createResponse = await request.post(`${BASE_URL}/address/add`, {
  314 |         headers: getAuthHeaders(),
  315 |         data: {
  316 |           ...testAddress,
  317 |           rcvrName: '待删除用户',
  318 |           dftIndc: 'N',
  319 |         },
  320 |       });
  321 | 
  322 |       const createBody = await createResponse.json();
  323 |       const addressToDelete = createBody.data.id;
  324 | 
  325 |       // 删除地址
  326 |       const response = await request.delete(`${BASE_URL}/address/${addressToDelete}`, {
  327 |         headers: getAuthHeaders(),
  328 |       });
  329 | 
  330 |       expect(response.ok()).toBeTruthy();
  331 |       const body = await response.json();
  332 |       expect(body.code).toBe(200);
  333 |       expect(body.message).toBe('删除成功');
  334 |     });
  335 | 
  336 |     test('TC017: 删除不存在的地址', async ({ request }) => {
  337 |       const response = await request.delete(`${BASE_URL}/address/not-exist-id`, {
  338 |         headers: getAuthHeaders(),
  339 |       });
  340 | 
  341 |       expect(response.status()).toBe(404);
  342 |     });
  343 |   });
  344 | 
  345 |   test.describe('8. 性能测试', () => {
  346 | 
  347 |     test('TC018: 接口响应时间测试', async ({ request }) => {
  348 |       const iterations = 10;
  349 |       const responseTimes = [];
  350 | 
  351 |       for (let i = 0; i < iterations; i++) {
  352 |         const start = Date.now();
  353 |         await request.get(`${BASE_URL}/address/list`, {
  354 |           headers: getAuthHeaders(),
  355 |         });
  356 |         responseTimes.push(Date.now() - start);
  357 |       }
  358 | 
  359 |       const avgResponseTime = responseTimes.reduce((a, b) => a + b, 0) / iterations;
  360 |       console.log(`平均响应时间: ${avgResponseTime}ms`);
  361 | 
  362 |       // 验证平均响应时间小于100ms
  363 |       expect(avgResponseTime).toBeLessThan(100);
  364 |     });
  365 |   });
  366 | 
  367 |   test.describe('9. 数据一致性测试', () => {
  368 | 
  369 |     test('TC019: 更新后数据一致性验证', async ({ request }) => {
  370 |       // 更新地址
  371 |       const updateData = {
  372 |         id: createdAddressId,
  373 |         rcvrName: '一致性测试用户',
  374 |         rcvrTel: '13600136000',
  375 |         prvcCde: '440000',
  376 |         cityCde: '440100',
  377 |         dstrctCde: '440106',
  378 |         dtlAddr: '珠江新城 A座 1001室',
  379 |         dftIndc: 'Y',
  380 |       };
  381 | 
  382 |       await request.put(`${BASE_URL}/address/update`, {
  383 |         headers: getAuthHeaders(),
  384 |         data: updateData,
  385 |       });
  386 | 
  387 |       // 获取地址详情验证
  388 |       const response = await request.get(`${BASE_URL}/address/${createdAddressId}`, {
  389 |         headers: getAuthHeaders(),
  390 |       });
  391 | 
  392 |       const body = await response.json();
> 393 |       expect(body.data.rcvrName).toBe(updateData.rcvrName);
      |                        ^ TypeError: Cannot read properties of null (reading 'rcvrName')
  394 |       expect(body.data.rcvrTel).toBe(updateData.rcvrTel);
  395 |     });
  396 | 
  397 |     test('TC020: 删除后列表验证', async ({ request }) => {
  398 |       // 创建临时地址
  399 |       const createResponse = await request.post(`${BASE_URL}/address/add`, {
  400 |         headers: getAuthHeaders(),
  401 |         data: {
  402 |           ...testAddress,
  403 |           rcvrName: '临时用户',
  404 |           dftIndc: 'N',
  405 |         },
  406 |       });
  407 | 
  408 |       const createBody = await createResponse.json();
  409 |       const tempAddressId = createBody.data.id;
  410 | 
  411 |       // 获取当前列表数量
  412 |       const listBefore = await request.get(`${BASE_URL}/address/list`, {
  413 |         headers: getAuthHeaders(),
  414 |       });
  415 |       const countBefore = (await listBefore.json()).data.length;
  416 | 
  417 |       // 删除地址
  418 |       await request.delete(`${BASE_URL}/address/${tempAddressId}`, {
  419 |         headers: getAuthHeaders(),
  420 |       });
  421 | 
  422 |       // 验证列表数量减少
  423 |       const listAfter = await request.get(`${BASE_URL}/address/list`, {
  424 |         headers: getAuthHeaders(),
  425 |       });
  426 |       const countAfter = (await listAfter.json()).data.length;
  427 | 
  428 |       expect(countAfter).toBe(countBefore - 1);
  429 |     });
  430 |   });
  431 | });
  432 | 
```