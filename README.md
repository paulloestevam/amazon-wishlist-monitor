
amazon-wishlist-monitor - Monitors and notifies you about product offers from your Amazon wish lists.

<img src="https://github.com/user-attachments/assets/6f6e2f4b-dbbd-49b2-ba77-8edfde5333ab" alt="Screenshot 1" width="100" style="margin-right:100px;">

<img src="https://github.com/user-attachments/assets/03c057d8-ae10-44cc-acb5-1ead45f8b60e" alt="Screenshot 2" width="400">
<br><br>


- [DESCRIPTION](#description)
- [HOW IT WORKS](#how-it-works)
- [RUN EXECUTABLE](#run-executable)
- [REQUIREMENTS](#requirements)
- [MANUAL CONFIGURATION](#manual-configuration)

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

# RUN EXECUTABLE
Just download the [release](https://github.com/paulloestevam/amazon-wishlist-monitor/releases/tag/1.0.0) (.jar file) and run the command in Windows cmd, replacing the fields:

- spring.mail.username (your gmail email)
- spring.mail.password (your app password, go to the [SMTP Email Sending](#smtp-email-sending) to find out how to get it)
- webdriver.chrome.driver-path (the path to your chromedriver.exe file, go to the [WebDriver](#webdriver) section to download it)
- wishlist.urls (the url of your public wishlist, if you have more than one, separate them with a comma)
- scheduler.time (the desired time for automatic execution)

Cmd:

    java -jar amazon-wishlist-monitor-1.0.0.jar --spring.mail.username=insiraseuemail@gmail.com --spring.mail.password="aaaa eeee iiii oooo" --email.sender=insiraseuemail@gmail.com --webdriver.chrome.driver-path="C:\\Users\\paull\\AppData\\Roaming\\chrome-win64\\chromedriver.exe" --wishlist.urls="https://www.amazon.com.br/hz/wishlist/ls/19BUDH5MAAAAA?ref_=list_d_wl_lfu_nav_2,https://www.amazon.com.br/hz/wishlist/ls/1QSH2OXCEEEEE?ref_=list_d_wl_lfu_nav_1" --scheduler.time="0 30 15 * * *" --discountPercentage=10 --productLineWidth=200


Powershell

    java -jar amazon-wishlist-monitor-1.0.0.jar `
      --spring.mail.username="insiraseuemail@gmail.com" `
    --spring.mail.password="aaaa eeee iiii oooo" `
      --email.sender="insiraseuemail@gmail.com" `
    --webdriver.chrome.driver-path="C:\Users\paull\AppData\Roaming\chrome-win64\chromedriver.exe" `
      --wishlist.urls="https://www.amazon.com.br/hz/wishlist/ls/19BUDH5MAAAAA?ref_=list_d_wl_lfu_nav_2,https://www.amazon.com.br/hz/wishlist/ls/1QSH2OXCEEEEE?ref_=list_d_wl_lfu_nav_1" `
    --scheduler.time="0 30 15 * * *" `
      --discountPercentage=10 `
    --productLineWidth=200

At the scheduled time indicated by the scheduler.time attribute, the application will execute the process and send the email.

You can also manually trigger the notification by sending a request to the application's 'amazon/wishlist' endpoint. Open your browser and type http://localhost:8080/amazon/wishlist and press enter. If the application is up, the notification will be sent.
# REQUIREMENTS

## Java
To run the "java -jar" command you need to have Java installed. Download the version appropriate for your operating system from: https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html

## WebDriver

Download the webdriver according to your Chrome version at https://developer.chrome.com/docs/chromedriver/downloads, then unzip it into any folder and point the address of the chromedriver.exe file in the application.yaml file:
    webdriver:
        chrome:
        driver-path: "C:\\Users\\paull\\AppData\\Roaming\\chrome-win64\\chromedriver.exe"

### Why is it necessary?
The Amazon wishlist page utilizes a feature called lazy loading, which loads list items dynamically as the user scrolls through the page. This technique only loads content when it becomes visible, causing issues for the application, as it needs to access the entire wishlist.

That's why it's necessary to use a Selenium library, used for page testing, which basically opens a hidden window with the wishlist and scrolls the page with the mouse... This is called WebDriver. Without it, the application won't work.

# MANUAL CONFIGURATION
## SMTP Email Sending
You need to configure application.yaml with your username (insert email) and the Gmail "app password" that you get from the page https://support.google.com/accounts/answer/185833?hl=en-US

Google does not allow you to use your Gmail password directly, so for security, you should create an "app password".

This password consists of 16 characters, separated by spaces into groups of 4, for example "aaaa aaaa aaaa aaaa "
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

