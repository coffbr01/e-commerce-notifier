package me.bcoffield.ecn.persistence;

import lombok.Getter;
import lombok.Setter;
import me.bcoffield.ecn.notifier.NotificationSummary;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class SaveFile {
  private Map<String, NotificationSummary> notificationSummaries = new HashMap<>();
}
