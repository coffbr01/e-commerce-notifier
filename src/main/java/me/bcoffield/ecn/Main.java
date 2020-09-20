package me.bcoffield.ecn;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import me.bcoffield.ecn.config.StartupConfig;
import me.bcoffield.ecn.notifier.INotifier;
import me.bcoffield.ecn.notifier.NotifierType;
import me.bcoffield.ecn.notifier.PrintlnNotifier;
import me.bcoffield.ecn.notifier.TwilioNotifier;
import me.bcoffield.ecn.retailer.IRetailer;
import me.bcoffield.ecn.retailer.Retailer;
import org.apache.commons.cli.*;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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

    List<IRetailer> retailers = getRetailers();

    while (true) {
      retailers.forEach(
          retailer ->
              retailer
                  .findInStockUrls()
                  .forEach(url -> notifier.notify("Available: ".concat(url))));
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
