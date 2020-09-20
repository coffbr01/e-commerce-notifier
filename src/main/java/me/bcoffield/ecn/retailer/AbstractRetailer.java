package me.bcoffield.ecn.retailer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractRetailer implements IRetailer {
  /** The website's list page to be scraped */
  protected abstract String getProductListUrl();

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
  public List<String> findInStockUrls() {
    FirefoxOptions firefoxOptions = new FirefoxOptions();
    firefoxOptions.setHeadless(true);
    WebDriver driver = new FirefoxDriver(firefoxOptions);
    WebDriverWait wait = new WebDriverWait(driver, 10);
    try {
      String productListUrl = getProductListUrl();
      System.out.println("Looking for products at: " + productListUrl);
      driver.get(productListUrl);
      wait.until(ExpectedConditions.presenceOfElementLocated(getListSelector()));
      Thread.sleep(3000);
      List<WebElement> listItems = driver.findElements(getListItemSelector());
      List<String> inStockUrls =
          listItems.stream()
              .filter(this::isItemInStock)
              .map(this::getItemUrl)
              .collect(Collectors.toList());
      System.out.println("Out of stock count: " + (listItems.size() - inStockUrls.size()));
      return inStockUrls;
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      driver.close();
    }
    return Collections.emptyList();
  }
}
