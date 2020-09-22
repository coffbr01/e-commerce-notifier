package me.bcoffield.ecn.retailer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class MicroCenter extends AbstractRetailer {

  @Override
  protected By getListSelector() {
    return By.id("productGrid");
  }

  @Override
  protected By getListItemSelector() {
    return By.className("detail_wrapper");
  }

  @Override
  protected String getItemUrl(WebElement itemElement) {
    return itemElement
        .findElement(By.className("pDescription"))
        .findElement(By.tagName("a"))
        .getAttribute("href");
  }

  @Override
  protected boolean isItemInStock(WebElement itemElement) {
    String text =
        itemElement
            .findElement(By.className("stock"))
            .findElement(By.tagName("strong"))
            .findElement(By.tagName("span"))
            .getText();
    return text.equalsIgnoreCase("IN STOCK");
  }

  @Override
  protected boolean canPurchaseProduct(WebDriver driver) {
    return false;
  }
}
