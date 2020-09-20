package me.bcoffield.ecn.notifier;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import me.bcoffield.ecn.Main;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class TwilioNotifier implements INotifier {
  // The Twilio number to send the text message from
  private static String FROM_PHONE_NUMBER;

  // The end users' phone numbers to send the text message to
  private static List<String> TO_PHONE_NUMBERS;

  private static boolean initialized = false;

  public TwilioNotifier() {
    if (!initialized) {
      Properties properties = new Properties();
      try {
        properties.load(Main.class.getClassLoader().getResourceAsStream("secret.properties"));
      } catch (IOException e) {
        e.printStackTrace();
      }

      FROM_PHONE_NUMBER = properties.getProperty("FROM_PHONE_NUMBER");
      TO_PHONE_NUMBERS = Arrays.asList((properties.getProperty("TO_PHONE_NUMBERS")).split(","));
      // Twilio API keys
      String TWILIO_ACCOUNT_SID = properties.getProperty("TWILIO_ACCOUNT_SID");
      String TWILIO_AUTH_TOKEN = properties.getProperty("TWILIO_AUTH_TOKEN");
      Twilio.init(TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN);
      initialized = true;
    }
  }

  @Override
  public void notify(String note) {
    PhoneNumber from = new PhoneNumber(FROM_PHONE_NUMBER);
    TO_PHONE_NUMBERS.forEach(to -> Message.creator(new PhoneNumber(to), from, note).create());
  }
}
