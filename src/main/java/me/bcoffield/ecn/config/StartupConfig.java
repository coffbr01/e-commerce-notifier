package me.bcoffield.ecn.config;

import lombok.Getter;
import lombok.Setter;
import me.bcoffield.ecn.notifier.INotifier;

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
  private List<NotifierConfig> notifiers = Collections.singletonList(new NotifierConfig());
  private List<RetailerUrl> productListUrls = Collections.emptyList();
  private List<RetailerUrl> productUrls = Collections.emptyList();
  private int threadCount = 2;
  private List<String> notificationBlacklist = Collections.emptyList();
  private long pageTimeoutMs = 15000;

  public static void set(StartupConfig startupConfig) {
    config = startupConfig;
  }

  public static StartupConfig get() {
    return config;
  }
}
