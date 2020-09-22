package me.bcoffield.ecn.retailer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class BestBuy extends AbstractRetailer {

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

  @Override
  protected boolean canPurchaseProduct(WebDriver driver) {
    return false;
  }
}
