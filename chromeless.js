// https://developers.google.com/web/updates/2017/04/headless-chrome
// const puppeteer = require('puppeteer');
// const TARGET = "http://ec2-35-183-239-72.ca-central-1.compute.amazonaws.com/p1.html";

// function sleep(ms) {
//     return new Promise(resolve => setTimeout(resolve, ms));
// }

// (async () => {


//     for (let i = 1; i <= 100; i++) {
//         const browser = await puppeteer.launch();
//         const page = await browser.newPage();
//         var response = await page.goto(TARGET, { waitUntil: 'networkidle0' });
//         console.log(response.status());
//         await page.waitForFunction('typeof gtag === "function"');
//         console.log(i);
//         await browser.close();
//     }


// })().catch((e) => {
//     console.log(e);
// });






// https://developer.mozilla.org/en-US/docs/Mozilla/Firefox/Headless_mode
// oline users will go up!!!
const TARGET = "http://ec2-35-183-239-72.ca-central-1.compute.amazonaws.com/p1.html";
var webdriver = require('selenium-webdriver'),
    By = webdriver.By,
    until = webdriver.until;

var firefox = require('selenium-webdriver/firefox');

var options = new firefox.Options();
options.addArguments("-headless");

(async () => {
    for (let i = 1; i <= 20; i++) {
        var driver = new webdriver.Builder()
            .forBrowser('firefox')
            .setFirefoxOptions(options)
            .build();
        await driver.get(TARGET);
        await driver.quit();
        console.log(i);
    }
})();
