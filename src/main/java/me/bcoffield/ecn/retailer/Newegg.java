package me.bcoffield.ecn.retailer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Newegg implements IRetailer {
  private static final String RTX_3080 = "https://www.newegg.com/p/pl?N=100007709%20601357247";

  @Override
  public List<String> findInStockUrls() {
    WebDriver driver = new FirefoxDriver();
    WebDriverWait wait = new WebDriverWait(driver, 10);
    try {
      driver.get(RTX_3080);
      wait.until(ExpectedConditions.presenceOfElementLocated(By.className("items-grid-view")));
      Thread.sleep(3000);
      final List<String> result = new ArrayList<>();
      driver
          .findElements(By.className("item-cell"))
          .forEach(
              skuItem -> {
                String itemUrl = skuItem.findElement(By.className("item-img")).getAttribute("href");
                String inStock =
                    skuItem
                        .findElement(By.className("item-button-area"))
                        .findElement(By.tagName("button"))
                        .getText();
                if (inStock.equalsIgnoreCase("ADD TO CART")) {
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
