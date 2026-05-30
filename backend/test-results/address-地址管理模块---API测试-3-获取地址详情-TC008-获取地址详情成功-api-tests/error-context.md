# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: address.spec.js >> 地址管理模块 - API测试 >> 3. 获取地址详情 >> TC008: 获取地址详情成功
- Location: tests\address.spec.js:193:5

# Error details

```
Error: expect(received).toBeTruthy()

Received: false
```

# Test source

```ts
  98  |   });
  99  | 
  100 |   test.describe('1. 添加地址', () => {
  101 | 
  102 |     test('TC001: 添加非默认地址', async ({ request }) => {
  103 |       const response = await request.post(`${BASE_URL}/address/add`, {
  104 |         headers: getAuthHeaders(),
  105 |         data: testAddress,
  106 |       });
  107 | 
  108 |       expect(response.ok()).toBeTruthy();
  109 |       const body = await response.json();
  110 |       expect(body.code).toBe(200);
  111 |       expect(body.message).toBe('添加成功');
  112 |       expect(body.data).toBeDefined();
  113 |       expect(body.data.id).toBeDefined();
  114 |       expect(body.data.rcvrName).toBe(testAddress.rcvrName);
  115 |       expect(body.data.rcvrTel).toBe(testAddress.rcvrTel);
  116 |       expect(body.data.dftIndc).toBe('N');
  117 | 
  118 |       createdAddressId = body.data.id;
  119 |     });
  120 | 
  121 |     test('TC002: 添加默认地址', async ({ request }) => {
  122 |       const response = await request.post(`${BASE_URL}/address/add`, {
  123 |         headers: getAuthHeaders(),
  124 |         data: defaultAddress,
  125 |       });
  126 | 
  127 |       expect(response.ok()).toBeTruthy();
  128 |       const body = await response.json();
  129 |       expect(body.code).toBe(200);
  130 |       expect(body.data.dftIndc).toBe('Y');
  131 | 
  132 |       defaultAddressId = body.data.id;
  133 |     });
  134 | 
  135 |     test('TC003: 手机号格式验证失败', async ({ request }) => {
  136 |       const invalidAddress = { ...testAddress, rcvrTel: '12345678901' };
  137 |       const response = await request.post(`${BASE_URL}/address/add`, {
  138 |         headers: getAuthHeaders(),
  139 |         data: invalidAddress,
  140 |       });
  141 | 
  142 |       expect(response.status()).toBe(400);
  143 |     });
  144 | 
  145 |     test('TC004: 收货人姓名为空验证', async ({ request }) => {
  146 |       const invalidAddress = { ...testAddress, rcvrName: '' };
  147 |       const response = await request.post(`${BASE_URL}/address/add`, {
  148 |         headers: getAuthHeaders(),
  149 |         data: invalidAddress,
  150 |       });
  151 | 
  152 |       expect(response.status()).toBe(400);
  153 |     });
  154 | 
  155 |     test('TC005: 省份编码为空验证', async ({ request }) => {
  156 |       const invalidAddress = { ...testAddress, prvcCde: '' };
  157 |       const response = await request.post(`${BASE_URL}/address/add`, {
  158 |         headers: getAuthHeaders(),
  159 |         data: invalidAddress,
  160 |       });
  161 | 
  162 |       expect(response.status()).toBe(400);
  163 |     });
  164 |   });
  165 | 
  166 |   test.describe('2. 获取地址列表', () => {
  167 | 
  168 |     test('TC006: 获取地址列表成功', async ({ request }) => {
  169 |       const response = await request.get(`${BASE_URL}/address/list`, {
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
> 198 |       expect(response.ok()).toBeTruthy();
      |                             ^ Error: expect(received).toBeTruthy()
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
  270 |       expect(response.ok()).toBeTruthy();
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
```