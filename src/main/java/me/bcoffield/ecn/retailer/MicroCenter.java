package me.bcoffield.ecn.retailer;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MicroCenter extends AbstractHttpClientRetailer {

  private static final String BASE_URL = "https://www.microcenter.com";
  private static final String QUERY_STRING = "?storeId=045";

  @Override
  protected List<String> getProductUrlsFromListPage(Document document) {
    return document.selectFirst("#productGrid").select(".detail_wrapper").stream()
        .map(element -> BASE_URL + element.selectFirst(".pDescription").getElementsByTag("a").get(0).attr("href") + QUERY_STRING)
        .collect(Collectors.toList());
  }

  @Override

  protected boolean hasBuyButtonOnProductPage(Document document) {
    Element inventory = document.body().selectFirst(".inventory");
    Element inventoryCnt = inventory.selectFirst(".inventoryCnt");
    return Objects.requireNonNullElse(inventoryCnt, inventory).text().matches("\\d.* in stock");
  }
}
