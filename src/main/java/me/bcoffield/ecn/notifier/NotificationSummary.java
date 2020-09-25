package me.bcoffield.ecn.notifier;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotificationSummary {
  private String note;
  private long timestamp = System.currentTimeMillis();
  private int count = 0;

  public NotificationSummary(String note) {
    this.note = note;
  }
}
