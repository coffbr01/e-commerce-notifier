package me.bcoffield.ecn.notifier;

import lombok.extern.slf4j.Slf4j;
import me.bcoffield.ecn.config.StartupConfig;
import me.bcoffield.ecn.persistence.SaveFile;
import me.bcoffield.ecn.persistence.SaveFileMgmt;

import java.util.Map;

@Slf4j
public abstract class AbstractNotifier implements INotifier {
  abstract void sendNotification(String note);

  abstract NotifierType getNotifierType();

  @Override
  public final void notify(String note) {
    SaveFile saveFile = SaveFileMgmt.get();
    if (isAllowedToNotify(note, saveFile)) {
      log.info(note);
      sendNotification(note);
      NotificationSummary notificationSummary = saveFile.getNotificationSummaries().get(note);
      Map<NotifierType, Integer> counts = notificationSummary.getCounts();
      notificationSummary.setTimestamp(System.currentTimeMillis());
      if (counts.containsKey(getNotifierType())) {
        counts.put(getNotifierType(), counts.get(getNotifierType()) + 1);
      } else {
        counts.put(getNotifierType(), 1);
      }
    }
  }

  private boolean isAllowedToNotify(String note, SaveFile saveFile) {
    if (StartupConfig.get().getNotificationBlacklist().contains(note)) {
      return false;
    }
    if (!saveFile.getNotificationSummaries().containsKey(note)) {
      saveFile.getNotificationSummaries().put(note, new NotificationSummary(note));
      return true;
    }
    NotificationSummary notificationSummary = saveFile.getNotificationSummaries().get(note);
    if (System.currentTimeMillis() - notificationSummary.getTimestamp()
        >= StartupConfig.get().getNotifiers().stream()
        .filter(notifier -> notifier.getType().equals(getNotifierType()))
        .findFirst().orElseThrow(() -> new RuntimeException(getNotifierType() + " notifier not found in config"))
        .getMinimumInterval()) {
      return true;
    }
    return notificationSummary.getCounts().getOrDefault(getNotifierType(), 0) < 1;
  }
}
