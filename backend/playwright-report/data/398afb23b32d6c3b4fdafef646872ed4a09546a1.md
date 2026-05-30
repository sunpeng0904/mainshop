# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: address.spec.js >> 地址管理模块 - API测试 >> 5. 设置默认地址 >> TC012: 设置默认地址成功
- Location: tests\address.spec.js:265:5

# Error details

```
Error: expect(received).toBeTruthy()

Received: false
```

# Test source

```ts
  170 |         headers: getAuthHeaders(),
  171 |       });
  172 | 
  173 |       expect(response.ok()).toBeTruthy();
  174 |       const body = await response.json();
  175 |       expect(body.code).toBe(200);
  176 |       expect(Array.isArray(body.data)).toBeTruthy();
  177 |     });
  178 | 
  179 |     test('TC007: 验证地址列表包含新添加的地址', async ({ request }) => {
  180 |       const response = await request.get(`${BASE_URL}/address/list`, {
  181 |         headers: getAuthHeaders(),
  182 |       });
  183 | 
  184 |       const body = await response.json();
  185 |       const addressIds = body.data.map(addr => addr.id);
  186 |       expect(addressIds).toContain(createdAddressId);
  187 |       expect(addressIds).toContain(defaultAddressId);
  188 |     });
  189 |   });
  190 | 
  191 |   test.describe('3. 获取地址详情', () => {
  192 | 
  193 |     test('TC008: 获取地址详情成功', async ({ request }) => {
  194 |       const response = await request.get(`${BASE_URL}/address/${createdAddressId}`, {
  195 |         headers: getAuthHeaders(),
  196 |       });
  197 | 
  198 |       expect(response.ok()).toBeTruthy();
  199 |       const body = await response.json();
  200 |       expect(body.code).toBe(200);
  201 |       expect(body.data.id).toBe(createdAddressId);
  202 |       expect(body.data.rcvrName).toBe(testAddress.rcvrName);
  203 |       expect(body.data.rcvrTel).toBe(testAddress.rcvrTel);
  204 |     });
  205 | 
  206 |     test('TC009: 获取不存在的地址', async ({ request }) => {
  207 |       const response = await request.get(`${BASE_URL}/address/not-exist-id`, {
  208 |         headers: getAuthHeaders(),
  209 |       });
  210 | 
  211 |       expect(response.status()).toBe(404);
  212 |     });
  213 |   });
  214 | 
  215 |   test.describe('4. 更新地址', () => {
  216 | 
  217 |     test('TC010: 更新地址成功', async ({ request }) => {
  218 |       const updateData = {
  219 |         id: createdAddressId,
  220 |         rcvrName: '更新后用户',
  221 |         rcvrTel: '13700137000',
  222 |         prvcCde: '440000',
  223 |         cityCde: '440100',
  224 |         dstrctCde: '440106',
  225 |         dtlAddr: '天河城广场 B座 3001室',
  226 |         dftIndc: 'N',
  227 |       };
  228 | 
  229 |       const response = await request.put(`${BASE_URL}/address/update`, {
  230 |         headers: getAuthHeaders(),
  231 |         data: updateData,
  232 |       });
  233 | 
  234 |       expect(response.ok()).toBeTruthy();
  235 |       const body = await response.json();
  236 |       expect(body.code).toBe(200);
  237 |       expect(body.message).toBe('更新成功');
  238 |       expect(body.data.rcvrName).toBe(updateData.rcvrName);
  239 |       expect(body.data.rcvrTel).toBe(updateData.rcvrTel);
  240 |     });
  241 | 
  242 |     test('TC011: 更新不存在的地址', async ({ request }) => {
  243 |       const updateData = {
  244 |         id: 'not-exist-id',
  245 |         rcvrName: '测试',
  246 |         rcvrTel: '13800138000',
  247 |         prvcCde: '110000',
  248 |         cityCde: '110100',
  249 |         dstrctCde: '110105',
  250 |         dtlAddr: '测试地址',
  251 |         dftIndc: 'N',
  252 |       };
  253 | 
  254 |       const response = await request.put(`${BASE_URL}/address/update`, {
  255 |         headers: getAuthHeaders(),
  256 |         data: updateData,
  257 |       });
  258 | 
  259 |       expect(response.status()).toBe(404);
  260 |     });
  261 |   });
  262 | 
  263 |   test.describe('5. 设置默认地址', () => {
  264 | 
  265 |     test('TC012: 设置默认地址成功', async ({ request }) => {
  266 |       const response = await request.put(`${BASE_URL}/address/default/${createdAddressId}`, {
  267 |         headers: getAuthHeaders(),
  268 |       });
  269 | 
> 270 |       expect(response.ok()).toBeTruthy();
      |                             ^ Error: expect(received).toBeTruthy()
  271 |       const body = await response.json();
  272 |       expect(body.code).toBe(200);
  273 |       expect(body.message).toBe('设置成功');
  274 |     });
  275 | 
  276 |     test('TC013: 验证默认地址已更新', async ({ request }) => {
  277 |       const response = await request.get(`${BASE_URL}/address/${createdAddressId}`, {
  278 |         headers: getAuthHeaders(),
  279 |       });
  280 | 
  281 |       const body = await response.json();
  282 |       expect(body.data.dftIndc).toBe('Y');
  283 |     });
  284 | 
  285 |     test('TC014: 设置不存在的地址为默认', async ({ request }) => {
  286 |       const response = await request.put(`${BASE_URL}/address/default/not-exist-id`, {
  287 |         headers: getAuthHeaders(),
  288 |       });
  289 | 
  290 |       expect(response.status()).toBe(404);
  291 |     });
  292 |   });
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
```