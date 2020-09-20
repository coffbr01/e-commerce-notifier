package me.bcoffield.ecn.retailer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class Nvidia extends AbstractRetailer {
  @Override
  protected By getListSelector() {
    return By.id("mainCont");
  }

  @Override
  protected By getListItemSelector() {
    return By.className("product-container");
  }

  @Override
  protected String getItemUrl(WebElement itemElement) {
    // Special case for Nvidia because their list page doesn't have a link to each product, so hard code FE
    return "https://www.nvidia.com/en-us/geforce/graphics-cards/30-series/rtx-3080/";
  }

  @Override
  protected boolean isItemInStock(WebElement itemElement) {
    String text = itemElement.findElement(By.className("featured-buy-link")).getText();
    return !text.equalsIgnoreCase("OUT OF STOCK");
  }
}
