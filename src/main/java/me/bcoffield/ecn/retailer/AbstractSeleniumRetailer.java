package me.bcoffield.ecn.retailer;

import lombok.extern.slf4j.Slf4j;
import me.bcoffield.ecn.config.StartupConfig;
import me.bcoffield.ecn.persistence.ErrorStatistic;
import me.bcoffield.ecn.persistence.SaveFileMgmt;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractSeleniumRetailer implements IRetailer {
  /**
   * Selenium By that tells this class how to find the product list
   */
  protected abstract By getListSelector();

  /**
   * Selenium By that tells this class how to find items in the product list
   */
  protected abstract By getListItemSelector();

  /**
   * Given a list item, find its product URL
   *
   * @param itemElement The item's web element
   */
  protected abstract String getItemUrl(WebElement itemElement);

  /**
   * Given a list item, determine whether it's in stock
   *
   * @param itemElement The item's web element
   */
  abstract boolean isItemInStock(WebElement itemElement);

  protected abstract boolean canPurchaseProduct(WebDriver driver);

  @Override
  public List<String> findInStockUrls(String url) {
    log.info("Inspecting list page {}", url);
    WebDriver driver = null;
    try {
      driver = openWebDriver(url);
      WebDriverWait wait = new WebDriverWait(driver, 5);
      wait.until(ExpectedConditions.presenceOfElementLocated(getListSelector()));
      List<WebElement> listItems = driver.findElements(getListItemSelector());
      List<String> inStockUrls =
          listItems.stream()
              .filter(webElement -> {
                try {
                  return isItemInStock(webElement);
                } catch (NoSuchElementException e) {
                  log.debug("Could not find element", e);
                  return false;
                }
              })
              .map(this::getItemUrl)
              .collect(Collectors.toList());
      if (!inStockUrls.isEmpty()) {
        takeScreenshot(driver, "success");
      }
      log.info(
          "Out of stock count for {}: {}",
          new URL(url).getHost(),
          listItems.size() - inStockUrls.size());
      return inStockUrls;
    } catch (Exception e) {
      takeScreenshot(driver, "failList");
      logErrorStatistic(url);
      log.warn("Failed to check products", e);
    } finally {
      closeDriver(driver);
    }
    return Collections.emptyList();
  }

  @Override
  public boolean isProductInStock(String url) {
    WebDriver driver = null;
    try {
      driver = openWebDriver(url);
      Thread.sleep(5000);
      boolean inStock = canPurchaseProduct(driver);
      log.info("in stock: {} for {}", inStock, url);
      return inStock;
    } catch (Exception e) {
      takeScreenshot(driver, "failSingle");
      logErrorStatistic(url);
      log.warn("Failed to check product", e);
    } finally {
      closeDriver(driver);
    }
    return false;
  }

  private void logErrorStatistic(String url) {
    Map<String, ErrorStatistic> errorStatistics = SaveFileMgmt.get().getErrorStatistics();
    if (!errorStatistics.containsKey(url)) {
      errorStatistics.put(url, new ErrorStatistic());
    }
    errorStatistics.get(url).getOccurrences().add(System.currentTimeMillis());
  }

  protected void takeScreenshot(WebDriver driver, String dir) {
    try {
      TakesScreenshot ts = (TakesScreenshot) driver;
      File source = ts.getScreenshotAs(OutputType.FILE);
      FileUtils.copyFile(source, new File("./screenshots/" + dir + "/" + System.currentTimeMillis() + ".png"));
      log.info("Screenshot taken");
    } catch (Exception e) {
      log.error("Exception while taking screenshot", e);
    }
  }

  private WebDriver openWebDriver(String url) {
    FirefoxOptions firefoxOptions = new FirefoxOptions();
    firefoxOptions.setHeadless(true);
    firefoxOptions.setLogLevel(FirefoxDriverLogLevel.ERROR);
    firefoxOptions.addPreference("permissions.default.image", 2);
    WebDriver driver = new FirefoxDriver(firefoxOptions);
    driver.manage().window().setPosition(new Point(0, 0));
    driver.manage().window().setSize(new Dimension(3840, 2160));
    Thread t = new Thread(() -> driver.get(Thread.currentThread().getName()), url);
    t.start();
    try {
      t.join(StartupConfig.get().getPageTimeoutMs());
    } catch (InterruptedException e) { // ignore
      log.debug("Thread interrupted ".concat(url), e);
    }
    if (t.isAlive()) { // Thread still alive, we need to abort
      log.warn("Timeout on loading page " + url);
      t.interrupt();
    }
    return driver;
  }

  private void closeDriver(WebDriver driver) {
    if (driver != null) {
      try {
        driver.close();
      } catch (Exception e) {
        log.error("Couldn't close the web driver", e);
      }
    }
  }
}
