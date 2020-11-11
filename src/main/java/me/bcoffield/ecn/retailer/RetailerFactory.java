package me.bcoffield.ecn.retailer;

import java.net.MalformedURLException;
import java.net.URL;

public class RetailerFactory {
  public static IRetailer getRetailer(String urlStr) {
    try {
      URL url = new URL(urlStr);
      switch (url.getHost()) {
        case "www.bestbuy.com":
          return new BestBuy();
        case "www.microcenter.com":
          return new MicroCenter();
        case "www.newegg.com":
          return new Newegg();
        case "www.nvidia.com":
          return new Nvidia();
        case "www.bhphotovideo.com":
          return new BHPhotoVideo();
        default:
          throw new RuntimeException("Unknown site " + url.getHost());
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
      throw new RuntimeException("Malformed URL " + urlStr);
    }
  }
}
