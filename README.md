# E-Commerce Notifier

This script scrapes e-commerce websites like Micro Center for a configurable set of products,
 then sends a text message when stock is found.

1. Download and extract the [Gecko Driver](https://github.com/mozilla/geckodriver/releases)
2. Set up a [Twilio](https://www.twilio.com/) account and create an API key in the
 [Programmable Messaging](https://www.twilio.com/console/sms/dashboard) section
3. Configure [Main.java](src/main/java/me/bcoffield/ecn/Main.java) with the values from steps 1 and 2, and add the appropriate to/from phone numbers
4. Run [Main.java](src/main/java/me/bcoffield/ecn/Main.java) using [java 14](https://jdk.java.net/)

Good luck!