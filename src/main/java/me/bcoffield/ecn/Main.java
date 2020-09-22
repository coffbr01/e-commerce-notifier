package me.bcoffield.ecn;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;
import me.bcoffield.ecn.config.RetailerUrl;
import me.bcoffield.ecn.config.StartupConfig;
import me.bcoffield.ecn.notifier.INotifier;
import me.bcoffield.ecn.notifier.NotifierType;
import me.bcoffield.ecn.notifier.PrintlnNotifier;
import me.bcoffield.ecn.notifier.TwilioNotifier;
import me.bcoffield.ecn.retailer.RetailerFactory;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
public class Main {

  private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
  private INotifier notifier;

  public static void main(String[] args) throws IOException, ParseException {
    initConfig(args);
    new Main().start();
  }

  private static void initConfig(String[] args) throws IOException, ParseException {
    Options options = new Options();
    options.addOption("config", true, "Location of yaml config file");
    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = parser.parse(options, args);
    StartupConfig.set(
        mapper.readValue(new File(cmd.getOptionValue("config")), StartupConfig.class));
    System.setProperty("webdriver.gecko.driver", StartupConfig.get().getGeckoDriver());
  }

  private void start() {
    notifier = getNotifier();

    while (true) {
      List<RetailerUrl> productListUrls = StartupConfig.get().getProductListUrls();
      List<RetailerUrl> productUrls = StartupConfig.get().getProductUrls();

      List<Runnable> tasks =
          productUrls.stream()
              .map(url -> createRetailerProductRunnable(url.getUrl()))
              .collect(Collectors.toList());

      tasks.addAll(
          productListUrls.stream()
              .map(url -> createRetailerListRunnable(url.getUrl()))
              .collect(Collectors.toList()));

      ExecutorService executor = Executors.newFixedThreadPool(tasks.size());

      CompletableFuture<?>[] futures =
          tasks.stream()
              .map(task -> CompletableFuture.runAsync(task, executor))
              .toArray(CompletableFuture[]::new);
      CompletableFuture.allOf(futures).join();
      executor.shutdown();

      // TODO implement StartupConfig.get().getProductUrls()
      delay();
    }
  }

  private Runnable createRetailerProductRunnable(String url) {
    return () -> {
      boolean inStock = RetailerFactory.getRetailer(url).isProductInStock(url);
      if (inStock) {
        notifier.notify("Available: ".concat(url));
      }
    };
  }

  private Runnable createRetailerListRunnable(String url) {
    return () ->
        RetailerFactory.getRetailer(url)
            .findInStockUrls(url)
            .forEach(
                inStockUrl -> {
                  notifier.notify("Available: ".concat(inStockUrl));
                });
  }

  private void delay() {
    Random random = new Random();
    double randomDouble = random.nextDouble();
    if (random.nextBoolean()) {
      randomDouble = randomDouble * -1;
    }
    long avgDelay = (StartupConfig.get().getMinDelay() + StartupConfig.get().getMaxDelay()) / 2;
    long distanceToBoundary = StartupConfig.get().getMaxDelay() - avgDelay;
    double adjustment = distanceToBoundary * randomDouble;
    long sleepFor = (long) (avgDelay + adjustment);
    try {
      log.info("Sleeping for {}", toHumanReadableTime(sleepFor));
      Thread.sleep(sleepFor);
      log.info("Slept for {}", toHumanReadableTime(sleepFor));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private String toHumanReadableTime(long durationInMillis) {
    long second = (durationInMillis / 1000) % 60;
    long minute = (durationInMillis / (1000 * 60)) % 60;
    long hour = (durationInMillis / (1000 * 60 * 60)) % 24;

    return String.format("%02d:%02d:%02d", hour, minute, second);
  }

  private INotifier getNotifier() {
    if (StartupConfig.get().getNotifierType() == NotifierType.PRINTLN) {
      return new PrintlnNotifier();
    }
    return new TwilioNotifier();
  }
}
