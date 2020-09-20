package me.bcoffield.ecn;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
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

public class Main {

  private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

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
    INotifier notifier = getNotifier();

    while (true) {
      List<RetailerUrl> productListUrls = StartupConfig.get().getProductListUrls();
      productListUrls.forEach(
          productListUrl -> {
            RetailerFactory.getRetailer(productListUrl.getUrl())
                .findInStockUrls(productListUrl.getUrl())
                .forEach(url -> notifier.notify("Available: ".concat(url)));
          });
      // TODO implement StartupConfig.get().getProductUrls()
      try {
        Thread.sleep(StartupConfig.get().getMsDelayBetweenRuns());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private INotifier getNotifier() {
    if (StartupConfig.get().getNotifierType() == NotifierType.PRINTLN) {
      return new PrintlnNotifier();
    }
    return new TwilioNotifier();
  }
}
