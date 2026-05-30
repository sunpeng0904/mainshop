// @ts-check

/**
 * 测试辅助工具
 */

/**
 * 生成随机手机号
 * @returns {string}
 */
function generateRandomPhone() {
  const prefixes = ['130', '131', '132', '133', '135', '136', '137', '138', '139', '150', '151', '152', '153', '155', '156', '157', '158', '159', '170', '176', '177', '178', '180', '181', '182', '183', '184', '185', '186', '187', '188', '189'];
  const prefix = prefixes[Math.floor(Math.random() * prefixes.length)];
  const suffix = String(Math.floor(Math.random() * 100000000)).padStart(8, '0');
  return prefix + suffix;
}

/**
 * 生成随机字符串
 * @param {number} length
 * @returns {string}
 */
function generateRandomString(length = 10) {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  let result = '';
  for (let i = 0; i < length; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return result;
}

/**
 * 生成测试地址数据
 * @param {object} overrides
 * @returns {object}
 */
function generateTestAddress(overrides = {}) {
  return {
    rcvrName: `测试用户${generateRandomString(3)}`,
    rcvrTel: generateRandomPhone(),
    prvcCde: '110000',
    cityCde: '110100',
    dstrctCde: '110105',
    dtlAddr: `望京SOHO ${generateRandomString(5)}室`,
    dftIndc: 'N',
    ...overrides,
  };
}

/**
 * 等待指定时间
 * @param {number} ms
 * @returns {Promise<void>}
 */
function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

/**
 * 重试执行函数
 * @param {Function} fn
 * @param {number} maxRetries
 * @param {number} delay
 * @returns {Promise<any>}
 */
async function retry(fn, maxRetries = 3, delay = 1000) {
  for (let i = 0; i < maxRetries; i++) {
    try {
      return await fn();
    } catch (error) {
      if (i === maxRetries - 1) throw error;
      await sleep(delay);
    }
  }
}

module.exports = {
  generateRandomPhone,
  generateRandomString,
  generateTestAddress,
  sleep,
  retry,
};
