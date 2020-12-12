package me.bcoffield.ecn;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import me.bcoffield.ecn.config.NotifierConfig;
import me.bcoffield.ecn.config.RetailerUrl;
import me.bcoffield.ecn.config.StartupConfig;
import me.bcoffield.ecn.notifier.*;
import me.bcoffield.ecn.persistence.ErrorStatistic;
import me.bcoffield.ecn.persistence.SaveFileMgmt;
import me.bcoffield.ecn.retailer.RetailerFactory;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
public class Main {

  private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
  public static FirebaseApp firebaseApp;
  private List<INotifier> notifiers;

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
    File logDir = new File("logs");
    if (logDir.mkdir()) {
      System.out.println("Created log dir");
    }
    if (new File(logDir, "e-commerce-notifier.log").createNewFile()) {
      log.info("Created log file");
    }
    System.setProperty("webdriver.gecko.driver", StartupConfig.get().getGeckoDriver());
    FirebaseOptions firebaseOptions = FirebaseOptions.builder().setCredentials(GoogleCredentials.getApplicationDefault()).build();
    firebaseApp = FirebaseApp.initializeApp(firebaseOptions);
  }

  private void start() {
    notifiers = getNotifiers(StartupConfig.get().getNotifiers());

    while (true) {
      List<RetailerUrl> productListUrls =
          filterHighErrorRate(StartupConfig.get().getProductListUrls());
      List<RetailerUrl> productUrls = filterHighErrorRate(StartupConfig.get().getProductUrls());

      List<Runnable> tasks =
          productUrls.stream()
              .map(url -> createRetailerProductRunnable(url.getUrl()))
              .collect(Collectors.toList());

      tasks.addAll(
          productListUrls.stream()
              .map(url -> createRetailerListRunnable(url.getUrl()))
              .collect(Collectors.toList()));

      int threads = StartupConfig.get().getThreadCount();
      if (threads == -1) {
        threads = tasks.size();
      }
      ExecutorService executor = Executors.newFixedThreadPool(threads);

      CompletableFuture<?>[] futures =
          tasks.stream()
              .map(task -> CompletableFuture.runAsync(task, executor))
              .toArray(CompletableFuture[]::new);
      CompletableFuture.allOf(futures).join();
      executor.shutdown();

      SaveFileMgmt.save();
      try {
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
          Runtime.getRuntime().exec("taskkill /F /IM geckodriver.exe");
        } else if (System.getProperty("os.name").toLowerCase().startsWith("linux")) {
          Runtime.getRuntime().exec("killall geckodriver");
          Runtime.getRuntime().exec("killall firefox");
        }
      } catch (IOException e) {
        log.error("Could not kill geckodriver", e);
      }

      delay();
    }
  }

  /**
   * Factory method of sorts. FML. IDC. This is a script.
   *
   * @param notifiers The list of notifier configs in config.yaml
   * @return A list of concrete notifier instances based off the config notifiers
   */
  private List<INotifier> getNotifiers(List<NotifierConfig> notifiers) {
    return notifiers.stream().map(notifierConfig -> {
      if (NotifierType.TWILIO.equals(notifierConfig.getType())) {
        return new TwilioNotifier();
      } else if (NotifierType.ANDROID.equals(notifierConfig.getType())) {
        return new AndroidNotifier();
      } else {
        return new PrintlnNotifier();
      }
    }).collect(Collectors.toList());
  }

  private List<RetailerUrl> filterHighErrorRate(List<RetailerUrl> urls) {
    final Map<String, ErrorStatistic> errorStatistics = SaveFileMgmt.get().getErrorStatistics();
    return urls.stream()
        .filter(
            url -> {
              if (!errorStatistics.containsKey(url.getUrl())) {
                return true;
              }
              ErrorStatistic errorStatistic = errorStatistics.get(url.getUrl());
              long startTime = System.currentTimeMillis() - (StartupConfig.get().getMaxDelay() * 4);
              long occurrencesInWindow =
                  errorStatistic.getOccurrences().stream()
                      .filter(timestamp -> timestamp > startTime)
                      .count();
              boolean happyUrl = occurrencesInWindow <= 2;
              if (!happyUrl) {
                log.info(
                    "Skipping due to {} recent errors {}", occurrencesInWindow, url.getUrl());
              }
              return happyUrl;
            })
        .collect(Collectors.toList());
  }

  private Runnable createRetailerProductRunnable(String url) {
    return () -> {
      if (RetailerFactory.getRetailer(url).isProductInStock(url)) {
        notifiers.forEach(notifier -> notifier.notify(url));
      }
    };
  }

  private Runnable createRetailerListRunnable(String url) {
    return () ->
        RetailerFactory.getRetailer(url)
            .findInStockUrls(url)
            .forEach(inStockUrl -> notifiers.forEach(notifier -> notifier.notify(inStockUrl)));
  }

  private void delay() {
    long leftLimit = StartupConfig.get().getMinDelay();
    long rightLimit = StartupConfig.get().getMaxDelay();
    long sleepFor = leftLimit + (long) (Math.random() * (rightLimit - leftLimit));
    try {
      log.info("Sleeping for {}", toHumanReadableTime(sleepFor));
      Thread.sleep(sleepFor);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private String toHumanReadableTime(long durationInMillis) {
    long second = (durationInMillis / 1000) % 60;
    long minute = (durationInMillis / (1000 * 60)) % 60;
    long hour = (durationInMillis / (1000 * 60 * 60)) % 60;

    return String.format("%02d:%02d:%02d", hour, minute, second);
  }
}
