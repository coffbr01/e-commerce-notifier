package me.bcoffield.ecn.config;

import lombok.Getter;
import lombok.Setter;
import me.bcoffield.ecn.notifier.NotifierType;

@Getter
@Setter
public class NotifierConfig {
  private NotifierType type = NotifierType.TWILIO;
  private long minimumInterval = 86400000;
}
