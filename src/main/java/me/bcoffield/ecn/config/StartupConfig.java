package me.bcoffield.ecn.config;

import lombok.Getter;
import lombok.Setter;
import me.bcoffield.ecn.notifier.NotifierType;

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
  private Long msDelayBetweenRuns = 600000L;
  private NotifierType notifierType = NotifierType.TWILIO;
  private List<RetailerUrl> productListUrls;
  private List<RetailerUrl> productUrls;

  public static void set(StartupConfig startupConfig) {
    config = startupConfig;
  }

  public static StartupConfig get() {
    return config;
  }
}
