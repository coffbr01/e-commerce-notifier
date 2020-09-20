package me.bcoffield.ecn.retailer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MicroCenter implements IRetailer {

  private static final String RTX_3080 =
      "https://www.microcenter.com/search/search_results.aspx?N=&cat=&Ntt=3080&searchButton=search&storeId=045";

  @Override
  public List<String> findInStockUrls() {
    WebDriver driver = new FirefoxDriver();
    WebDriverWait wait = new WebDriverWait(driver, 10);
    try {
      driver.get(RTX_3080);
      wait.until(ExpectedConditions.presenceOfElementLocated(By.id("productGrid")));
      Thread.sleep(3000);
      final List<String> result = new ArrayList<>();
      driver
          .findElements(By.className("detail_wrapper"))
          .forEach(
              skuItem -> {
                String itemUrl =
                    skuItem
                        .findElement(By.className("pDescription"))
                        .findElement(By.tagName("a"))
                        .getAttribute("href");
                String inStock =
                    skuItem
                        .findElement(By.className("stock"))
                        .findElement(By.tagName("strong"))
                        .findElement(By.tagName("span"))
                        .getText();
                if (inStock.equalsIgnoreCase("IN STOCK")) {
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
