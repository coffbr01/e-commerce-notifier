package me.bcoffield.ecn.notifier;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class NotificationSummary {
  private String note;
  private long timestamp = System.currentTimeMillis();
  private Map<NotifierType, Integer> counts = new HashMap<>();

  public NotificationSummary(String note) {
    this.note = note;
  }
}
