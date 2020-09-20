package me.bcoffield.ecn.retailer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@Retailer
public class MicroCenter extends AbstractRetailer {

  @Override
  protected String getProductListUrl() {
    return "https://www.microcenter.com/search/search_results.aspx?N=&cat=&Ntt=3080&searchButton=search&storeId=045";
  }

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
}
