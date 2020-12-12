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

# (Optional) Range of time to wait (in milliseconds) between each round of website scrapes. Default 60000-900000
minDelay: 60000
maxDelay: 900000

# (Optional) Notifier type, e.g. TWILIO, PRINTLN. Default TWILIO
notifierType:
  type: TWILIO
  # The time between notifying for a particular item
  minimumInterval: 86400000

# (Optional) Number of Firefox threads to spawn at once. Defaults to 2. Give -1 to have the same number of threads as URLs
threadCount: 2

# The product list pages to scrape. In this example, we're looking across 4 retailers for any RTX 3080 availability
productListUrls:
  - url: https://www.bestbuy.com/site/searchpage.jsp?_dyncharset=UTF-8&browsedCategory=abcat0507002&id=pcat17071&iht=n&ks=960&list=y&qp=gpusv_facet%3DGraphics%20Processing%20Unit%20(GPU)~NVIDIA%20GeForce%20RTX%203080&sc=Global&st=categoryid%24abcat0507002&type=page&usc=All%20Categories
  - url: https://www.microcenter.com/search/search_results.aspx?N=&cat=&Ntt=3080&searchButton=search&storeId=045
  - url: https://www.newegg.com/p/pl?N=100007709%20601357247
  - url: https://www.nvidia.com/en-us/shop/geforce/gpu/?page=1&limit=9&locale=en-us&category=GPU&gpu=RTX%203080&manufacturer=NVIDIA&manufacturer_filter=NVIDIA~1,ASUS~1,EVGA~2,GIGABYTE~2,MSI~1,PNY~0,ZOTAC~0
  - url: https://www.bhphotovideo.com/c/search?q=rtx%203080&filters=fct_category%3Agraphic_cards_6567

# The single product pages to scrape. Useful if a retailer's list page is terrible. That's NVidia...they're terrible.
productUrls:
  - url: https://www.nvidia.com/en-us/geforce/graphics-cards/30-series/rtx-3080/
  - url: https://www.nvidia.com/en-us/geforce/graphics-cards/30-series/rtx-3090/
```
4. Run `mvn clean install` to build the project (install [maven](https://maven.apache.org/install.html)
 if you don't already have it)
5. Run `java -jar target/e-commerce-notifier-1.0-SNAPSHOT-jar-with-dependencies.jar -config <yaml_from_step_3>`

Currently implemented retailers are:
  * Best Buy
  * Micro Center
  * Newegg
  * Nvidia
  * B&H Photo Video

Good luck, and happy purchasing!

Worst case........millions.
