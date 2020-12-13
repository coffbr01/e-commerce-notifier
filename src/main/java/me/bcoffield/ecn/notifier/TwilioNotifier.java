package me.bcoffield.ecn.notifier;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import me.bcoffield.ecn.config.StartupConfig;

import java.util.List;

@Slf4j
public class TwilioNotifier extends AbstractNotifier {

  private static boolean initialized = false;

  public TwilioNotifier() {
    if (!initialized) {
      Twilio.init(
          StartupConfig.get().getTwilioAccountSid(), StartupConfig.get().getTwilioAuthToken());
      initialized = true;
    }
  }

  @Override
  void sendNotification(String note) {
    PhoneNumber from = new PhoneNumber(StartupConfig.get().getFromPhoneNumber());
    List<String> toPhoneNumbers = StartupConfig.get().getToPhoneNumbers();
    if (toPhoneNumbers != null) {
      toPhoneNumbers.forEach(to -> Message.creator(new PhoneNumber(to), from, note).create());
    }
  }

  @Override
  NotifierType getNotifierType() {
    return NotifierType.TWILIO;
  }
}
