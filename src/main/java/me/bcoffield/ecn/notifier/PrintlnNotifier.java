package me.bcoffield.ecn.notifier;

public class PrintlnNotifier implements INotifier {
  @Override
  public void notify(String note) {
    System.out.println(note);
  }
}
