# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: address.spec.js >> 地址管理模块 - API测试 >> 1. 添加地址 >> TC001: 添加非默认地址
- Location: tests\address.spec.js:102:5

# Error details

```
Error: expect(received).toBeTruthy()

Received: false
```

# Test source

```ts
  8   |   password: 'password123'
  9   | };
  10  | 
  11  | // 存储token和地址ID
  12  | let authToken = null;
  13  | let createdAddressId = null;
  14  | let defaultAddressId = null;
  15  | 
  16  | // 测试数据
  17  | const testAddress = {
  18  |   rcvrName: '测试用户',
  19  |   rcvrTel: '13800138000',
  20  |   prvcCde: '110000',
  21  |   cityCde: '110100',
  22  |   dstrctCde: '110105',
  23  |   dtlAddr: '望京SOHO T1 1001室',
  24  |   dftIndc: 'N',
  25  | };
  26  | 
  27  | const defaultAddress = {
  28  |   rcvrName: '默认用户',
  29  |   rcvrTel: '13900139000',
  30  |   prvcCde: '310000',
  31  |   cityCde: '310100',
  32  |   dstrctCde: '310101',
  33  |   dtlAddr: '陆家嘴金融中心 A座 2001室',
  34  |   dftIndc: 'Y',
  35  | };
  36  | 
  37  | /**
  38  |  * 获取认证头
  39  |  */
  40  | function getAuthHeaders() {
  41  |   return {
  42  |     'Authorization': `Bearer ${authToken}`,
  43  |     'Content-Type': 'application/json',
  44  |   };
  45  | }
  46  | 
  47  | test.describe('地址管理模块 - API测试', () => {
  48  | 
  49  |   test.beforeAll(async ({ request }) => {
  50  |     console.log('开始地址管理模块测试');
  51  | 
  52  |     // 尝试登录获取token
  53  |     try {
  54  |       const loginResponse = await request.post(`${BASE_URL}/user/login`, {
  55  |         data: {
  56  |           username: TEST_USER.username,
  57  |           password: TEST_USER.password,
  58  |         },
  59  |       });
  60  | 
  61  |       if (loginResponse.ok()) {
  62  |         const loginBody = await loginResponse.json();
  63  |         authToken = loginBody.data || loginBody.data?.token || loginBody.token;
  64  |         console.log('登录成功，获取到token');
  65  |       } else {
  66  |         console.log('登录失败，将使用mock模式测试');
  67  |         authToken = 'mock-token-for-testing';
  68  |       }
  69  |     } catch (e) {
  70  |       console.log('登录请求失败，将使用mock模式测试');
  71  |       authToken = 'mock-token-for-testing';
  72  |     }
  73  |   });
  74  | 
  75  |   test.afterAll(async ({ request }) => {
  76  |     // 清理测试数据
  77  |     if (authToken && authToken !== 'mock-token-for-testing') {
  78  |       if (createdAddressId) {
  79  |         try {
  80  |           await request.delete(`${BASE_URL}/address/${createdAddressId}`, {
  81  |             headers: getAuthHeaders(),
  82  |           });
  83  |         } catch (e) {
  84  |           // 忽略清理错误
  85  |         }
  86  |       }
  87  |       if (defaultAddressId) {
  88  |         try {
  89  |           await request.delete(`${BASE_URL}/address/${defaultAddressId}`, {
  90  |             headers: getAuthHeaders(),
  91  |           });
  92  |         } catch (e) {
  93  |           // 忽略清理错误
  94  |         }
  95  |       }
  96  |     }
  97  |     console.log('地址管理模块测试完成');
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
> 108 |       expect(response.ok()).toBeTruthy();
      |                             ^ Error: expect(received).toBeTruthy()
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
```