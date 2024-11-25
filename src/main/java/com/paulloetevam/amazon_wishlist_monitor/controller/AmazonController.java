package com.paulloetevam.amazon_wishlist_monitor.controller;


import com.paulloetevam.amazon_wishlist_monitor.service.AmazonService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AmazonController {

    private final AmazonService amazonService;

    public AmazonController(AmazonService amazonService) {
        this.amazonService = amazonService;
    }

    @GetMapping("/amazon/wishlist")
    public void getWishlist() {
        amazonService.fetchWishlist();
    }
}
