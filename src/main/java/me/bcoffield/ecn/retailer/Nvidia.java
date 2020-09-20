package me.bcoffield.ecn.retailer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Nvidia implements IRetailer {
  private static final String RTX_3080 =
      "https://www.nvidia.com/en-us/shop/geforce/gpu/?page=1&limit=9&locale=en-us&category=GPU&gpu=RTX%203080&manufacturer=NVIDIA&manufacturer_filter=NVIDIA~1,ASUS~1,EVGA~2,GIGABYTE~2,MSI~1,PNY~0,ZOTAC~0";

  @Override
  public List<String> findInStockUrls() {
    WebDriver driver = new FirefoxDriver();
    WebDriverWait wait = new WebDriverWait(driver, 10);
    try {
      driver.get(RTX_3080);
      wait.until(ExpectedConditions.presenceOfElementLocated(By.className("product-container")));
      Thread.sleep(3000);
      final List<String> result = new ArrayList<>();
      driver
          .findElements(By.className("item-cell"))
          .forEach(
              skuItem -> {
                String itemUrl =
                    "https://www.nvidia.com/en-us/shop/geforce/gpu/?page=1&limit=9&locale=en-us&category=GPU&gpu=RTX%203080&manufacturer=NVIDIA&manufacturer_filter=NVIDIA~1,ASUS~1,EVGA~2,GIGABYTE~2,MSI~1,PNY~0,ZOTAC~0";
                String inStock = skuItem.findElement(By.className("featured-buy-link")).getText();
                if (!inStock.equalsIgnoreCase("OUT OF STOCK")) {
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
