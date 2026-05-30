const { chromium } = require('playwright');

(async () => {
  const browser = await chromium.launch({
    headless: false,
    args: [
      '--disable-blink-features=AutomationControlled',
      '--no-sandbox'
    ]
  });

  const context = await browser.newContext({
    viewport: { width: 1280, height: 800 },
    userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36'
  });

  const page = await context.newPage();

  // Remove webdriver flag
  await page.addInitScript(() => {
    Object.defineProperty(navigator, 'webdriver', { get: () => undefined });
  });

  await page.goto('https://www.baidu.com', { waitUntil: 'networkidle' });
  await page.waitForTimeout(2000);

  const searchBox = page.locator('#chat-textarea');
  await searchBox.click();
  await page.waitForTimeout(300);
  await searchBox.fill('Playwright MCP');
  await page.waitForTimeout(500);

  await searchBox.press('Enter');
  await page.waitForTimeout(5000);

  await page.screenshot({ path: 'baidu-search-result.png', fullPage: false });
  console.log('Screenshot saved: baidu-search-result.png');

  await browser.close();
})();
