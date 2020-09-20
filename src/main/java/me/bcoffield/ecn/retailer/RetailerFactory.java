package me.bcoffield.ecn.retailer;

import java.net.MalformedURLException;
import java.net.URL;

public class RetailerFactory {
  public static IRetailer getRetailer(String urlStr) {
    try {
      URL url = new URL(urlStr);
      return switch (url.getHost()) {
        case "www.bestbuy.com" -> new BestBuy();
        case "www.microcenter.com" -> new MicroCenter();
        case "www.newegg.com" -> new Newegg();
        case "www.nvidia.com" -> new Nvidia();
        default -> throw new RuntimeException("Unknown site " + url.getHost());
      };
    } catch (MalformedURLException e) {
      e.printStackTrace();
      throw new RuntimeException("Malformed URL " + urlStr);
    }
  }
}
