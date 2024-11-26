
amazon-wishlist-monitor - Monitors and notifies you about product offers from your Amazon wish lists.


- [DESCRIPTION](#description)
- [HOW IT WORKS](#how-it-works)
- [INSTALLATION](#installation)
- [CONFIGURATION](#configuration)

# DESCRIPTION
You can use this application to receive daily emails if your products, from your wishlists on the Amazon website, go on sale.

- Allows you to monitor more than one list/url
- Allows you to configure a discount limit. Example: only notifies products that are discounted by more than 70%
- Allows you to configure a destination email for notifications
- Allows you to choose when the check/notification should be sent through a cron expression (any time, day of the week)

Technologies: Java 21, Spring Boot 3.3.6, Gradle, Selenium, Webdriver.

It has only been tested with Windows 11. I will soon test it on Ubuntu on a Raspberry Pi 5.

# HOW IT WORKS
This application works as a daemon. It is ideal to be placed on a server, or on a machine that is turned on 24 hours a day. When launched, it checks when it should execute the call to the wishlist URL, in the cron expression. For example, at 3:30 pm every day. At that time, a call is made to the Amazon page, where the content of the page is downloaded and all the products that are eligible for a discount are categorized. After that, the application sends a summary email to the recipient with the list of products, prices and discounts.

# INSTALLATION


## WebDriver

Download the webdriver according to your Chrome version at https://developer.chrome.com/docs/chromedriver/downloads, then unzip it into any folder and point the address of the chromedriver.exe file in the application.yaml file:
    webdriver:
        chrome:
        driver-path: "C:\\Users\\paull\\AppData\\Roaming\\chrome-win64\\chromedriver.exe"

### Why is it necessary?
The Amazon wishlist page has a feature called page lazy loading, which loads the list items as you scroll the mouse/page. This prevents the amazon-wishlist-monitor application from reading all the list items.

That's why it's necessary to use a Selenium library, used for page testing, which basically opens a hidden window with the wishlist and scrolls the page with the mouse... This is called WebDriver. Without it, the application won't work.

# CONFIGURATION
## SMTP Email Sending
You need to configure application.yaml with your username (insert email) and the Gmail "app password" that you get from the page https://support.google.com/accounts/answer/185833?hl=en-US

This password has 16 digits, separated by spaces in groups of 4, for example "aaaa aaaa aaaa aaaa "
You may need to enable IMAP in your Gmail settings.
    
    mail:
        host: smtp.gmail.com
        port: 587
        username: paulloestevam@gmail.com
        password: asdz gggg rere aaaa

You must also enter your email in:
    
    email:
        sender: "paulloestevam@gmail.com"


## Wishlists URLs
You must access the Amazon page, log in to your account, open the wishlist and copy the URL.
Your wishlist must be set to Public.

Add your wishlist urls to:

    wishlist:
        urls:
            - "https://www.amazon.com.br/hz/wishlist/ls/19HAJAKMPA1AR?ref_=list_d_wl_lfu_nav_2"
            - "https://www.amazon.com.br/hz/wishlist/ls/19JASKDJPA1ZZ?ref_=list_d_wl_lfu_nav_3"

You must access the Amazon page, log in to your account, open the wishlist and copy and paste the URL.
Your wishlist must be set to Public.

## Scheduling with Cron expression
You can configure the execution/notification for any desired day/time, through cron expressions in the application.yaml file    
    
    scheduler:
        time: "0 30 15 * * *" # 15:30 everyday

https://docs.oracle.com/cd/E12058_01/doc/doc.1014/e12030/cron_expressions.htm
https://www.freeformatter.com/cron-expression-generator-quartz.html

## Discount Percentage
Set the minimum discount percentage you want to be notified about in the application.yaml file.
In this example, only products with a discount greater than 45% will be sent in the email.

    discountPercentage: 45

