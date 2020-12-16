package me.bcoffield.ecn.retailer;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@Slf4j
public class Newegg extends AbstractRetailer {

  @Override
  protected By getListSelector() {
    return By.className("items-grid-view");
  }

  @Override
  protected By getListItemSelector() {
    return By.className("item-container");
  }

  @Override
  protected String getItemUrl(WebElement itemElement) {
    return itemElement.findElement(By.className("item-img")).getAttribute("href");
  }

  @Override
  protected boolean isItemInStock(WebElement itemElement) {
    String text = itemElement.findElement(By.className("btn")).getText();

    return text.equalsIgnoreCase("ADD TO CART");
  }

  @Override
  protected boolean canPurchaseProduct(WebDriver driver) {
    return driver.findElement(By.className("product-inventory")).getText().toLowerCase().contains("in stock");
  }
}
