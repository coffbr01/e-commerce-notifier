package me.bcoffield.ecn.retailer;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
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

  @Override
  public List<String> findInStockUrls(String url) {
    FirefoxOptions firefoxOptions = new FirefoxOptions();
    firefoxOptions.setHeadless(true);
    firefoxOptions.setLogLevel(FirefoxDriverLogLevel.ERROR);
    WebDriver driver = new FirefoxDriver(firefoxOptions);
    WebDriverWait wait = new WebDriverWait(driver, 10);
    try {
      driver.get(url);
      wait.until(ExpectedConditions.presenceOfElementLocated(getListSelector()));
      Thread.sleep(3000);
      List<WebElement> listItems = driver.findElements(getListItemSelector());
      List<String> inStockUrls =
          listItems.stream()
              .filter(this::isItemInStock)
              .map(this::getItemUrl)
              .collect(Collectors.toList());
      log.info("Out of stock count for {}: {}", new URL(url).getHost(), listItems.size() - inStockUrls.size());
      return inStockUrls;
    } catch (InterruptedException | MalformedURLException e) {
      e.printStackTrace();
    } finally {
      driver.close();
    }
    return Collections.emptyList();
  }
}
