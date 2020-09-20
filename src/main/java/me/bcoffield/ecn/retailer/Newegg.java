package me.bcoffield.ecn.retailer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@Retailer
public class Newegg extends AbstractRetailer {

  @Override
  protected String getProductListUrl() {
    return "https://www.newegg.com/p/pl?N=100007709%20601357247";
  }

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
    return itemElement
        .findElement(By.className("item-button-area"))
        .findElement(By.tagName("button"))
        .getText()
        .equalsIgnoreCase("ADD TO CART");
  }
}
