package me.bcoffield.ecn;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import me.bcoffield.ecn.retailer.BestBuy;
import me.bcoffield.ecn.retailer.MicroCenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.bcoffield.ecn.Constants.BEST_BUY_3080;
import static me.bcoffield.ecn.Constants.MICRO_CENTER_3080;

public class Main {
  private static final String FROM_PHONE_NUMBER = "+15555555555";
  private static final List<String> TO_PHONE_NUMBERS = Arrays.asList("+15555555555");
  private static final String ACCOUNT_SID = "<ENTER_ACCOUNT_SID>";
  private static final String AUTH_TOKEN = "<ENTER_ACCOUNT_TOKEN>";
  private static final String GECKO_DRIVER = "<ENTER_PATH_TO_GECKO_DRIVER>";

  public static void main(String[] args) {
    System.setProperty("webdriver.gecko.driver", GECKO_DRIVER);
    Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    new Main().start();
  }

  private void start() {
    while (true) {
      List<String> allAvailableProducts = new ArrayList<>();
      allAvailableProducts.addAll(new BestBuy().findInStockUrls(BEST_BUY_3080));
      allAvailableProducts.addAll(new MicroCenter().findInStockUrls(MICRO_CENTER_3080));
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
        Thread.sleep(600000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
