package me.bcoffield.ecn.config;

import lombok.Getter;
import lombok.Setter;
import me.bcoffield.ecn.notifier.NotifierType;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class StartupConfig {
  private static StartupConfig config;
  private String geckoDriver;
  private String fromPhoneNumber;
  private String twilioAccountSid;
  private String twilioAuthToken;
  private List<String> toPhoneNumbers;
  private Long minDelay = 60000L;
  private Long maxDelay = 900000L;
  private NotifierType notifierType = NotifierType.TWILIO;
  private List<RetailerUrl> productListUrls = Collections.emptyList();
  private List<RetailerUrl> productUrls = Collections.emptyList();

  public static void set(StartupConfig startupConfig) {
    config = startupConfig;
  }

  public static StartupConfig get() {
    return config;
  }
}
