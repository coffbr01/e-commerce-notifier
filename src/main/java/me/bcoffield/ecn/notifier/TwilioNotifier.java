package me.bcoffield.ecn.notifier;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import me.bcoffield.ecn.config.StartupConfig;
import me.bcoffield.ecn.persistence.SaveFile;
import me.bcoffield.ecn.persistence.SaveFileMgmt;

@Slf4j
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
    log.info(note);
    // TODO this SaveFile impl should go into an abstract class
    SaveFile saveFile = SaveFileMgmt.get();
    if (isAllowedToNotify(note, saveFile)) {
      PhoneNumber from = new PhoneNumber(StartupConfig.get().getFromPhoneNumber());
      StartupConfig.get()
          .getToPhoneNumbers()
          .forEach(to -> Message.creator(new PhoneNumber(to), from, note).create());
      NotificationSummary notificationSummary = saveFile.getNotificationSummaries().get(note);
      notificationSummary.setCount(notificationSummary.getCount() + 1);
      notificationSummary.setTimestamp(System.currentTimeMillis());
    }
  }

  private boolean isAllowedToNotify(String note, SaveFile saveFile) {
    if (!saveFile.getNotificationSummaries().containsKey(note)) {
      saveFile.getNotificationSummaries().put(note, new NotificationSummary(note));
      return true;
    }
    NotificationSummary notificationSummary = saveFile.getNotificationSummaries().get(note);
    if (System.currentTimeMillis() - notificationSummary.getTimestamp()
        >= StartupConfig.get().getNotifier().getMinimumInterval()) {
      return true;
    }
    return notificationSummary.getCount() < 1;
  }
}
