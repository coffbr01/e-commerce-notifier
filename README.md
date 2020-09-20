# E-Commerce Notifier

This script scrapes e-commerce websites like Micro Center for a configurable set of products,
 then sends a notification when stock is found.

1. Download and extract the [Gecko Driver](https://github.com/mozilla/geckodriver/releases)
2. Set up a [Twilio](https://www.twilio.com/) account and create an API key in the
 [Programmable Messaging](https://www.twilio.com/console/sms/dashboard) section.
   * Or set `notifierType: PRINTLN` (see step 3 below) to print out the URLs to stdout without sending SMS
3. Create a file `config.yaml` with the following fields
```yaml
# Path to geckodriver.exe from step 1
geckoDriver: C:\\Users\\Me\\Downloads\\geckodriver-v0.27.0-win64\\geckodriver.exe

# Twilio information from step 2
fromPhoneNumber: +15555555555
twilioAccountSid: <ENTER_ACCOUNT_SID>
twilioAuthToken: <ENTER_AUTH_TOKEN>

# The phone number(s) you want to send the text messages to
toPhoneNumbers: 
  - +15555555555
  - +15555555555

# (Optional) Amount of time to wait (in milliseconds) between each round of website scrapes. Default 600000
msDelayBetweenRuns: 600000

# (Optional) Notifier type, e.g. TWILIO, PRINTLN. Default TWILIO
notifierType: TWILIO
```
4. Run `mvn clean install` to build the project (install [maven](https://maven.apache.org/install.html)
 if you don't already have it)
5. Run `java -jar target/e-commerce-notifier-1.0-SNAPSHOT-jar-with-dependencies.jar -config <yaml_from_step_3>`

Good luck!