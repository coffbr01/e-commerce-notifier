package me.bcoffield.ecn.notifier;

// No need to extend AbstractNotifier because println has no cost for notification and can just fire every time
public class PrintlnNotifier implements INotifier {
  @Override
  public void notify(String note) {
    System.out.println(note);
  }
}
