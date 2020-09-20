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
  private static final String RTX_3080 =
      "https://www.bestbuy.com/site/searchpage.jsp?_dyncharset=UTF-8&browsedCategory=abcat0507002&id=pcat17071&iht=n&ks=960&list=y&qp=gpusv_facet%3DGraphics%20Processing%20Unit%20(GPU)~NVIDIA%20GeForce%20RTX%203080&sc=Global&st=categoryid%24abcat0507002&type=page&usc=All%20Categories";

  @Override
  public List<String> findInStockUrls() {
    WebDriver driver = new FirefoxDriver();
    WebDriverWait wait = new WebDriverWait(driver, 10);
    try {
      driver.get(RTX_3080);
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
