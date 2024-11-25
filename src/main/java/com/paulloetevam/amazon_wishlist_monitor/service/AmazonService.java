package com.paulloetevam.amazon_wishlist_monitor.service;

import com.paulloetevam.amazon_wishlist_monitor.config.WishlistConfig;
import com.paulloetevam.amazon_wishlist_monitor.model.Product;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AmazonService {

    private final JavaMailSender mailSender;
    private final WishlistConfig wishlistConfig;

    @Value("${webdriver.chrome.driver-path}")
    private String chromeDriverPath;

    @Value("${email.sender}")
    private String emailSender;

    private int totalProducts;
    private int totalDeals;

    public AmazonService(JavaMailSender mailSender, WishlistConfig wishlistConfig) {
        this.mailSender = mailSender;
        this.wishlistConfig = wishlistConfig;
    }

    public void fetchWishlist() {
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);

        try {
            List<String> wishlistUrls = wishlistConfig.getUrls();
            StringBuilder wishlistSummary = new StringBuilder("<html><body><pre>");
            String emailTitle = "Wishlist Amazon - Ofertas do dia";

            // Itera na lista de URLs de wishlist do arquivo application.yaml
            for (int urlIndex = 0; urlIndex < wishlistUrls.size(); urlIndex++) {
                String wishlistUrl = wishlistUrls.get(urlIndex);
                driver.get(wishlistUrl);
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

                // Scroll para carregar todos os itens  (amazon page lazy loading)
                long lastHeight = (long) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("return document.body.scrollHeight");
                while (true) {
                    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
                    Thread.sleep(2000);
                    long newHeight = (long) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("return document.body.scrollHeight");
                    if (newHeight == lastHeight) break;
                    lastHeight = newHeight;
                }

                // Lê o html e extrai as marcações onde se encontram os preços e dados dos produtos
                Document document = Jsoup.parse(driver.getPageSource());
                List<Element> items = document.select(".g-item-sortable");
                List<Product> products = new ArrayList<>();
                for (Element item : items) {
                    String title = item.select(".a-size-base .a-link-normal").text();
                    String price = item.select(".a-price .a-offscreen").text();
                    String priceDrop = item.select(".itemPriceDrop .a-text-bold").text();
                    String deal = item.select(".wl-deal-rich-badge-label").text();
                    products.add(new Product(title, price, priceDrop, deal));
                    totalProducts++;
                }

                // Ordena os produtos pelo maior desconto (priceDrop ou deal)
                List<Product> productsOnSale = products.stream()
                        .filter(Product::isOnSale) // Filtra apenas produtos que estão em promoção
                        .sorted((product1, product2) -> {
                            // Obtém o maior valor de desconto entre priceDrop e deal para cada produto
                            int discount1 = Math.max(Product.getIntegerValue(product1.getPriceDrop()), Product.getIntegerValue(product1.getDeal()));
                            int discount2 = Math.max(Product.getIntegerValue(product2.getPriceDrop()), Product.getIntegerValue(product2.getDeal()));
                            return Integer.compare(discount2, discount1); // Ordena do maior para o menor desconto
                        })
                        .collect(Collectors.toList());

                if (urlIndex > 0) {
                    wishlistSummary.append("--<br><br>");
                }

                int count = 0;
                for (Product product : productsOnSale) {
                    String discountOrDeal = (Product.getIntegerValue(product.getPriceDrop()) > 0)
                            ? product.getPriceDrop()
                            : product.getDeal();

                    String formattedDiscountOrDeal = Product.getIntegerValue(discountOrDeal) == getMaxDiscount(productsOnSale)
                            ? "<b>" + Product.getIntegerValue(discountOrDeal) + "%  </b>"
                            : Product.getIntegerValue(discountOrDeal) + "%";

                    wishlistSummary.append(String.format(
                            "%-2d %-10s %-5s %-22s <br>",
                            ++count,
                            product.getPrice(),
                            formattedDiscountOrDeal,
                            product.getTitle().length() > 21
                                    ? product.getTitle().substring(0, 21)
                                    : product.getTitle()
                    ));
                    totalDeals++;
                }
            }

            wishlistSummary.append("</pre></body></html>");
            log.info(emailTitle + " - " + wishlistSummary.toString());
            emailTitle += " - " + totalDeals + "/" + totalProducts;
            sendEmail("paulloestevam@gmail.com", emailTitle, wishlistSummary.toString());
        } catch (Exception e) {
            log.error("Erro ao buscar a wishlist: " + e.getMessage());
        } finally {
            driver.quit();
        }
    }

    // Método para encontrar o maior desconto/deal
    private int getMaxDiscount(List<Product> products) {
        return products.stream()
                .mapToInt(product -> Product.getIntegerValue(product.getPriceDrop().isEmpty() ? product.getDeal() : product.getPriceDrop()))
                .max()
                .orElse(0);
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(emailSender);
            helper.setText(body, true);
            mailSender.send(message);
            log.info("E-mail enviado com sucesso!");
        } catch (MessagingException e) {
            log.error("Erro ao enviar e-mail: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

