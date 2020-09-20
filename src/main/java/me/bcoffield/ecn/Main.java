package me.bcoffield.ecn;

import me.bcoffield.ecn.notifier.INotifier;
import me.bcoffield.ecn.notifier.NotifierType;
import me.bcoffield.ecn.notifier.PrintlnNotifier;
import me.bcoffield.ecn.notifier.TwilioNotifier;
import me.bcoffield.ecn.retailer.IRetailer;
import me.bcoffield.ecn.retailer.Retailer;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
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
    INotifier notifier = getNotifier();

    List<IRetailer> retailers = getRetailers();

    while (true) {
      retailers.forEach(
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

  private INotifier getNotifier() {
    if (NOTIFIER_TYPE == NotifierType.PRINTLN) {
      return new PrintlnNotifier();
    }
    return new TwilioNotifier();
  }

  private List<IRetailer> getRetailers() {
    ClassPathScanningCandidateComponentProvider scanner =
        new ClassPathScanningCandidateComponentProvider(true);

    scanner.addIncludeFilter(new AnnotationTypeFilter(Retailer.class));

    List<IRetailer> result = new ArrayList<>();
    for (BeanDefinition bd : scanner.findCandidateComponents(IRetailer.class.getPackageName())) {
      try {
        Class clazz = Class.forName(bd.getBeanClassName());
        result.add((IRetailer) clazz.getConstructor().newInstance());
      } catch (ClassNotFoundException
          | NoSuchMethodException
          | IllegalAccessException
          | InstantiationException
          | InvocationTargetException e) {
        e.printStackTrace();
      }
    }
    return result;
  }
}
