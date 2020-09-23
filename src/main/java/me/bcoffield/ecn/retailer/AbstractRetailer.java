package me.bcoffield.ecn.retailer;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractRetailer implements IRetailer {
  /** Selenium By that tells this class how to find the product list */
  protected abstract By getListSelector();

  /** Selenium By that tells this class how to find items in the product list */
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
              .filter(this::isItemInStock)
              .map(this::getItemUrl)
              .collect(Collectors.toList());
      log.info(
          "Out of stock count for {}: {}",
          new URL(url).getHost(),
          listItems.size() - inStockUrls.size());
      return inStockUrls;
    } catch (Exception e) {
      takeScreenshot(driver);
      e.printStackTrace();
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
      boolean inStock = canPurchaseProduct(driver);
      log.info("in stock: {} for {}", inStock, url);
      return inStock;
    } catch (Exception e) {
      takeScreenshot(driver);
      e.printStackTrace();
    } finally {
      closeDriver(driver);
    }
    return false;
  }

  protected void takeScreenshot(WebDriver driver) {
    try {
      TakesScreenshot ts = (TakesScreenshot) driver;
      File source = ts.getScreenshotAs(OutputType.FILE);
      Instant instant = Instant.ofEpochMilli(System.currentTimeMillis());
      LocalDateTime date = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
      String fileName =
          date.getYear()
              + "-"
              + date.getMonthValue()
              + "-"
              + date.getDayOfMonth()
              + "_"
              + date.getHour()
              + "-"
              + date.getMinute()
              + "-"
              + date.getSecond();
      log.info(fileName);
      FileUtils.copyFile(source, new File("./screenshots/" + fileName + ".png"));
      log.info("Screenshot taken");
    } catch (Exception e) {
      log.error("Exception while taking screenshot", e);
    }
  }

  private WebDriver openWebDriver(String url) {
    FirefoxOptions firefoxOptions = new FirefoxOptions();
    //      firefoxOptions.setHeadless(true);
    firefoxOptions.setLogLevel(FirefoxDriverLogLevel.ERROR);
    WebDriver driver = new FirefoxDriver(firefoxOptions);
    driver.get(url);
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
