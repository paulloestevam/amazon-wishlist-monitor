package com.paulloetevam.amazon_wishlist_monitor.model;

import lombok.Data;

@Data
public class Product {
    private String title;
    private String price;
    private String priceDrop;
    private String deal;

    public Product(String title, String price, String priceDrop, String deal) {
        this.title = title;
        this.price = price;
        this.priceDrop = priceDrop;
        this.deal = deal;
    }

    // Método para verificar se o produto está em promoção
    public boolean isOnSale() {
        return getPriceDropPercentage() > 45 || getDealPercentage() > 45;
    }

    // Método para extrair a porcentagem do priceDrop
    private int getPriceDropPercentage() {
        if (priceDrop.contains("Preço caiu")) {
            try {
                return Integer.parseInt(priceDrop.replaceAll("[^0-9]", ""));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    // Método para extrair a porcentagem do deal
    private int getDealPercentage() {
        if (deal.contains("% off")) {
            try {
                return Integer.parseInt(deal.replaceAll("[^0-9]", ""));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    public static Integer getIntegerValue(String value) {
        try {
            // Remover símbolos de moeda e converter a string para um valor numérico
            String valueStr = value.replaceAll("[^\\d]", ""); // Remove todos os caracteres não numéricos
            return Integer.parseInt(valueStr); // Converte para Integer
        } catch (NumberFormatException e) {
            return 0; // Retorna 0 em caso de erro
        }
    }

}

