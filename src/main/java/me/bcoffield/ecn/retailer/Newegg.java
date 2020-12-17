package me.bcoffield.ecn.retailer;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class Newegg extends AbstractHttpClientRetailer {

  @Override
  protected List<String> getProductUrlsFromListPage(Document document) {
    Element grid = document.body().selectFirst(".items-grid-view");
    Elements gridItems = grid.select(".item-container");
    return gridItems.stream()
        .map(element -> element.selectFirst(".item-img").attr("href"))
        .collect(Collectors.toList());
  }

  @Override
  protected boolean hasBuyButtonOnProductPage(Document document) {
    Element productBuy = document.selectFirst(".product-buy");
    String buttonText;
    if (productBuy != null) {
      buttonText = productBuy.select(".btn").text();
    } else {
      buttonText = document.select(".atnPrimary").text();
    }
    return "add to cart".equalsIgnoreCase(buttonText);
  }
}
