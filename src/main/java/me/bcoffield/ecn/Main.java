package me.bcoffield.ecn;

import me.bcoffield.ecn.notifier.INotifier;
import me.bcoffield.ecn.notifier.NotifierType;
import me.bcoffield.ecn.notifier.PrintlnNotifier;
import me.bcoffield.ecn.notifier.TwilioNotifier;
import me.bcoffield.ecn.retailer.BestBuy;
import me.bcoffield.ecn.retailer.MicroCenter;
import me.bcoffield.ecn.retailer.Newegg;
import me.bcoffield.ecn.retailer.Nvidia;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class Main {

  // Amount of time to wait (in milliseconds) between each round of website scrapes
  private static long DELAY_BETWEEN_RUNS_MS;

  private static NotifierType NOTIFIER_TYPE;

  public static void main(String[] args) throws IOException {
    initConfig();
    new Main().start();
  }

  private static void initConfig() throws IOException {
    Properties properties = new Properties();
    properties.load(Main.class.getClassLoader().getResourceAsStream("secret.properties"));

    DELAY_BETWEEN_RUNS_MS =
        Long.parseLong(properties.getProperty("DELAY_BETWEEN_RUNS_MS", "600000"));
    NOTIFIER_TYPE = NotifierType.valueOf(properties.getProperty("NOTIFIER_TYPE", "TWILIO"));

    System.setProperty("webdriver.gecko.driver", properties.getProperty("GECKO_DRIVER"));
  }

  private void start() {
    INotifier notifier;
    switch (NOTIFIER_TYPE) {
      case PRINTLN:
        notifier = new PrintlnNotifier();
        break;
      case TWILIO:
      default:
        notifier = new TwilioNotifier();
    }
    while (true) {
      Arrays.asList(new BestBuy(), new MicroCenter(), new Newegg(), new Nvidia())
          .forEach(
              retailer ->
                  retailer
                      .findInStockUrls()
                      .forEach(url -> notifier.notify("Available: ".concat(url))));
      try {
        Thread.sleep(DELAY_BETWEEN_RUNS_MS);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
