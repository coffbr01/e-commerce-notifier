package me.bcoffield.ecn.retailer;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@Slf4j
public class Nvidia extends AbstractRetailer {
  @Override
  protected By getListSelector() {
    return By.id("mainCont");
  }

  @Override
  protected By getListItemSelector() {
    return By.tagName("product-details");
  }

  @Override
  protected String getItemUrl(WebElement itemElement) {
    // Special case for Nvidia because their list page doesn't have a link to each product, so hard
    // code FE
    return "https://www.nvidia.com/en-us/geforce/graphics-cards/30-series/rtx-3080/";
  }

  @Override
  protected boolean isItemInStock(WebElement itemElement) {
    String text = itemElement.findElement(By.className("featured-buy-link")).getText();
    return !text.equalsIgnoreCase("OUT OF STOCK");
  }

  @Override
  protected boolean canPurchaseProduct(WebDriver driver) {
    try {
      WebDriverWait wait = new WebDriverWait(driver, 15);
      WebElement buyButton =
          wait.until(ExpectedConditions.presenceOfElementLocated(By.className("cta-button")));
      return buyButton.isDisplayed() && buyButton.getText().equalsIgnoreCase("ADD TO CART");
    } catch (Exception e) {
      log.debug("Buy button not found", e);
    }
    return false;
  }
}
