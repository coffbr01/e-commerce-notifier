package me.bcoffield.ecn.retailer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class BHPhotoVideo extends AbstractRetailer {

  @Override
  protected By getListSelector() {
    return By.xpath("//div[contains(@data-selenium, 'listingProductDetailSection')]");
  }

  @Override
  protected By getListItemSelector() {
    return By.xpath("//div[contains(@data-selenium, 'miniProductPageProduct')]");
  }

  @Override
  protected String getItemUrl(WebElement itemElement) {
    return itemElement
        .findElement(By.xpath("//div[contains(@data-selenium, 'miniProductPageProductNameLink')]"))
        .getAttribute("href");
  }

  @Override
  protected boolean isItemInStock(WebElement itemElement) {
    String text =
        itemElement
            .findElement(By.xpath("//span[contains(@data-selenium, 'stockStatus')]"))
            .getText();
    return !text.equalsIgnoreCase("More on the Way");
  }

  @Override
  protected boolean canPurchaseProduct(WebDriver driver) {
    return driver.findElement(By.xpath("//button[contains(@data-selenium, 'addToCartButton')]")).isDisplayed();
  }
}
