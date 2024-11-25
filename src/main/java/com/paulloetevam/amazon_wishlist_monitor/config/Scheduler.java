package com.paulloetevam.amazon_wishlist_monitor.config;

import com.paulloetevam.amazon_wishlist_monitor.service.AmazonService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

    private final AmazonService amazonService;

    @Value("${scheduler.time}")
    private String time;

    public Scheduler(AmazonService amazonService) {
        this.amazonService = amazonService;
    }

    // @EventListener(ApplicationReadyEvent.class)
    //    public void fetchWishlistOnStartup() {
    //        amazonService.fetchWishlist();
    //    }

    @Scheduled(cron = "${scheduler.time}")
    public void fetchWishlistPeriodically() {
        amazonService.fetchWishlist();
    }
}
