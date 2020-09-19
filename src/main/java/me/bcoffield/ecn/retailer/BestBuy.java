package me.bcoffield.ecn.retailer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BestBuy implements IRetailer {
  @Override
  public List<String> findInStockUrls(String productListPageUrl) {
    WebDriver driver = new FirefoxDriver();
    WebDriverWait wait = new WebDriverWait(driver, 10);
    try {
      driver.get(productListPageUrl);
      wait.until(ExpectedConditions.presenceOfElementLocated(By.className("sku-item-list")));
      Thread.sleep(3000);
      final List<String> result = new ArrayList<>();
      driver
          .findElements(By.className("sku-item"))
          .forEach(
              skuItem -> {
                String itemUrl =
                    skuItem
                        .findElement(By.className("sku-header"))
                        .findElement(By.tagName("a"))
                        .getAttribute("href");
                if (skuItem
                    .findElement(By.className("add-to-cart-button"))
                    .getText()
                    .equals("Add to Cart")) {
                  result.add(itemUrl);
                }
              });
      return result;
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      driver.close();
    }
    return Collections.emptyList();
  }
}
