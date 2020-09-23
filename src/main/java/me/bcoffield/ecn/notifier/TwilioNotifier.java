package me.bcoffield.ecn.notifier;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import me.bcoffield.ecn.config.StartupConfig;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TwilioNotifier implements INotifier {

  private static final long INTERVAL = 86400000;
  private static final long AMOUNT = 1;
  private static final Map<String, NotificationSummary> sentNotifications = new HashMap<>();
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
    log.info(note);
    if (isAllowedToNotify(note)) {
      PhoneNumber from = new PhoneNumber(StartupConfig.get().getFromPhoneNumber());
      StartupConfig.get()
          .getToPhoneNumbers()
          .forEach(to -> Message.creator(new PhoneNumber(to), from, note).create());
      NotificationSummary notificationSummary = sentNotifications.get(note);
      notificationSummary.setCount(notificationSummary.getCount() + 1);
    }
  }

  private boolean isAllowedToNotify(String note) {
    if (!sentNotifications.containsKey(note)) {
      sentNotifications.put(note, new NotificationSummary(note));
      return true;
    }
    NotificationSummary notificationSummary = sentNotifications.get(note);
    if (System.currentTimeMillis() - notificationSummary.getTimestamp() >= INTERVAL) {
      return true;
    }
    return notificationSummary.getCount() < AMOUNT;
  }
}
