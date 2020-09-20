package me.bcoffield.ecn.retailer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class Nvidia extends AbstractRetailer {
  @Override
  protected String getProductListUrl() {
    return "https://www.nvidia.com/en-us/shop/geforce/gpu/?page=1&limit=9&locale=en-us&category=GPU&gpu=RTX%203080&manufacturer=NVIDIA&manufacturer_filter=NVIDIA~1,ASUS~1,EVGA~2,GIGABYTE~2,MSI~1,PNY~0,ZOTAC~0";
  }

  @Override
  protected By getListSelector() {
    return By.className("product-container");
  }

  @Override
  protected By getListItemSelector() {
    return By.className("item-cell");
  }

  @Override
  protected String getItemUrl(WebElement itemElement) {
    // Special case for Nvidia because their shop doesn't have a landing page for the 3080, you can
    // only add to cart from the list page (I think)
    return getProductListUrl();
  }

  @Override
  protected boolean isItemInStock(WebElement itemElement) {
    String text = itemElement.findElement(By.className("featured-buy-link")).getText();
    return !text.equalsIgnoreCase("OUT OF STOCK");
  }
}
