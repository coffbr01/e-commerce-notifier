package me.bcoffield.ecn.retailer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Newegg extends AbstractRetailer {

  @Override
  protected By getListSelector() {
    return By.className("items-grid-view");
  }

  @Override
  protected By getListItemSelector() {
    return By.className("item-cell");
  }

  @Override
  protected String getItemUrl(WebElement itemElement) {
    return itemElement.findElement(By.className("item-img")).getAttribute("href");
  }

  @Override
  protected boolean isItemInStock(WebElement itemElement) {
    return !itemElement
        .findElement(By.className("item-promo"))
        .getText()
        .equalsIgnoreCase("OUT OF STOCK");
  }

  @Override
  protected boolean canPurchaseProduct(WebDriver driver) {
    return false;
  }
}
