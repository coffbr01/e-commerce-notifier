# E-Commerce Notifier

This script scrapes e-commerce websites like Micro Center for a configurable set of products,
 then sends a text message when stock is found.

1. Download and extract the [Gecko Driver](https://github.com/mozilla/geckodriver/releases)
2. Set up a [Twilio](https://www.twilio.com/) account and create an API key in the
 [Programmable Messaging](https://www.twilio.com/console/sms/dashboard) section
3. Create a file `src/main/resources/secret.properties` with the following fields
```properties
# Path to geckodriver.exe from step 1
GECKO_DRIVER=C:\\Users\\Me\\Downloads\\geckodriver-v0.27.0-win64\\geckodriver.exe

#Twilio information from step 2
FROM_PHONE_NUMBER=+15555555555
TWILIO_ACCOUNT_SID=<ENTER_ACCOUNT_SID>
TWILIO_AUTH_TOKEN=<ENTER_AUTH_TOKEN>

# The phone number(s) you want to send the text messages to, comma delimited
TO_PHONE_NUMBERS=+15555555555,+15555555555
```
4. Run [Main.java](src/main/java/me/bcoffield/ecn/Main.java) using [java 14](https://jdk.java.net/)

The website URLs can be found in [Constants.java](src/main/java/me/bcoffield/ecn/Constants.java)

Good luck!