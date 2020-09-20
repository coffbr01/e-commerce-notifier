package me.bcoffield.ecn.retailer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class BestBuy extends AbstractRetailer {

  @Override
  protected String getProductListUrl() {
    return "https://www.bestbuy.com/site/searchpage.jsp?_dyncharset=UTF-8&browsedCategory=abcat0507002&id=pcat17071&iht=n&ks=960&list=y&qp=gpusv_facet%3DGraphics%20Processing%20Unit%20(GPU)~NVIDIA%20GeForce%20RTX%203080&sc=Global&st=categoryid%24abcat0507002&type=page&usc=All%20Categories";
  }

  @Override
  protected By getListSelector() {
    return By.className("sku-item-list");
  }

  @Override
  protected By getListItemSelector() {
    return By.className("sku-item");
  }

  @Override
  protected String getItemUrl(WebElement itemElement) {
    return itemElement
        .findElement(By.className("sku-header"))
        .findElement(By.tagName("a"))
        .getAttribute("href");
  }

  @Override
  protected boolean isItemInStock(WebElement itemElement) {
    return itemElement
        .findElement(By.className("add-to-cart-button"))
        .getText()
        .equalsIgnoreCase("Add to Cart");
  }
}
