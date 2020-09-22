package me.bcoffield.ecn.retailer;

import java.util.List;

public interface IRetailer {
  /**
   * Opens the list page and scrapes stock data, returning the product URLs that are in stock
   *
   * @return The product URLs that are in stock
   */
  List<String> findInStockUrls(String url);
  boolean isProductInStock(String url);
}
