package me.bcoffield.ecn.retailer;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Slf4j
public abstract class AbstractHttpClientRetailer implements IRetailer {
  protected abstract List<String> getProductUrlsFromListPage(Document document);

  protected abstract boolean hasBuyButtonOnProductPage(Document document);

  @Override
  public List<String> findInStockUrls(String url) {
    log.info("Finding stock for list page {}", url);
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpGet request = new HttpGet(url);
      try (CloseableHttpResponse response = httpClient.execute(request)) {
        if (response.getStatusLine().getStatusCode() == 200) {
          Document document = Jsoup.parse(EntityUtils.toString(response.getEntity()));
          List<String> productUrls = getProductUrlsFromListPage(document);
          ExecutorService executor = Executors.newFixedThreadPool(productUrls.size());
          List<Supplier<String>> tasks = productUrls.stream()
              .map(this::getListItemCallable)
              .collect(Collectors.toList());

          List<String> inStockProductUrls = tasks.stream()
              .map(task -> CompletableFuture.supplyAsync(task, executor))
              .map(CompletableFuture::join)
              .filter(productUrl -> !isNull(productUrl))
              .collect(Collectors.toList());
          log.info("{} of {} in stock for {}", inStockProductUrls.size(), productUrls.size(), url);
          executor.shutdown();
          return inStockProductUrls;
        } else {
          log.error("{} code for {}. Message={}", response.getStatusLine().getStatusCode(), url, response.getStatusLine().getReasonPhrase());
          log.debug("body: {}", EntityUtils.toString(response.getEntity()));
        }
      }
    } catch (IOException e) {
      log.error("problem getting list page for ".concat(url), e);
    }

    return Collections.emptyList();
  }

  private Supplier<String> getListItemCallable(String productUrl) {
    return () -> isProductInStock(productUrl) ? productUrl : null;
  }

  private String cleanUrl(String productUrl) {
    if (productUrl.contains(" ")) {
      return productUrl.replaceAll(" ", "%20");
    }
    return productUrl;
  }

  @Override
  public boolean isProductInStock(String url) {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      String cleanedUrl = cleanUrl(url);
      HttpGet productRequest = new HttpGet(cleanedUrl);
      try (CloseableHttpResponse productResponse = httpClient.execute(productRequest)) {
        Document productDocument = Jsoup.parse(EntityUtils.toString(productResponse.getEntity()));
        boolean hasStock = hasBuyButtonOnProductPage(productDocument);
        if (hasStock) {
          log.info("Found stock for {}", url);
        }
        return hasStock;
      }
    } catch (IOException e) {
      log.error("problem getting product page ".concat(url), e);
    }
    return false;
  }

}
