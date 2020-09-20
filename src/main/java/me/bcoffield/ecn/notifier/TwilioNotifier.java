package me.bcoffield.ecn.notifier;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import me.bcoffield.ecn.config.StartupConfig;

public class TwilioNotifier implements INotifier {

  private static boolean initialized = false;

  public TwilioNotifier() {
    if (!initialized) {
      Twilio.init(
          StartupConfig.get().getTwilioAccountSid(), StartupConfig.get().getTwilioAuthToken());
      initialized = true;
    }
  }

  @Override
  public void notify(String note) {
    PhoneNumber from = new PhoneNumber(StartupConfig.get().getFromPhoneNumber());
    StartupConfig.get()
        .getToPhoneNumbers()
        .forEach(to -> Message.creator(new PhoneNumber(to), from, note).create());
  }
}
