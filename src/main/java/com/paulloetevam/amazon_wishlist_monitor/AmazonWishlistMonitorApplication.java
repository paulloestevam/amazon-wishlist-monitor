package com.paulloetevam.amazon_wishlist_monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AmazonWishlistMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmazonWishlistMonitorApplication.class, args);
	}

}
