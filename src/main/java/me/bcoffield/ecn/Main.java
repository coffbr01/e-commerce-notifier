package me.bcoffield.ecn;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import me.bcoffield.ecn.retailer.BestBuy;
import me.bcoffield.ecn.retailer.MicroCenter;
import me.bcoffield.ecn.retailer.Newegg;
import me.bcoffield.ecn.retailer.Nvidia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static me.bcoffield.ecn.Constants.*;

public class Main {
  // The Twilio number to send the text message from
  private static String FROM_PHONE_NUMBER;

  // The end users' phone numbers to send the text message to
  private static List<String> TO_PHONE_NUMBERS;

  // Amount of time to wait (in milliseconds) between each round of website scrapes
  private static long DELAY_BETWEEN_RUNS_MS;

  public static void main(String[] args) throws IOException {
    initConfig();
    new Main().start();
  }

  private static void initConfig() throws IOException {
    Properties properties = new Properties();
    properties.load(Main.class.getClassLoader().getResourceAsStream("secret.properties"));
    FROM_PHONE_NUMBER = properties.getProperty("FROM_PHONE_NUMBER");
    TO_PHONE_NUMBERS = Arrays.asList((properties.getProperty("TO_PHONE_NUMBERS")).split(","));
    DELAY_BETWEEN_RUNS_MS =
        Long.parseLong(properties.getProperty("DELAY_BETWEEN_RUNS_MS", "600000"));
    System.setProperty("webdriver.gecko.driver", properties.getProperty("GECKO_DRIVER"));

    // Twilio API keys
    String TWILIO_ACCOUNT_SID = properties.getProperty("TWILIO_ACCOUNT_SID");
    String TWILIO_AUTH_TOKEN = properties.getProperty("TWILIO_AUTH_TOKEN");
    Twilio.init(TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN);
  }

  private void start() {
    while (true) {
      List<String> allAvailableProducts = new ArrayList<>();
      allAvailableProducts.addAll(new BestBuy().findInStockUrls(BEST_BUY_3080));
      allAvailableProducts.addAll(new MicroCenter().findInStockUrls(MICRO_CENTER_3080));
      allAvailableProducts.addAll(new Newegg().findInStockUrls(NEWEGG_3080));
      allAvailableProducts.addAll(new Nvidia().findInStockUrls(NVIDIA_3080));
      allAvailableProducts.forEach(
          url -> {
            String body = "Available: ".concat(url);
            PhoneNumber from = new PhoneNumber(FROM_PHONE_NUMBER);
            TO_PHONE_NUMBERS.forEach(
                to -> {
                  Message.creator(new PhoneNumber(to), from, body).create();
                });
          });
      try {
        Thread.sleep(DELAY_BETWEEN_RUNS_MS);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
