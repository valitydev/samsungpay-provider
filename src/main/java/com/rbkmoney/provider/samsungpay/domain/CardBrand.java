package com.rbkmoney.provider.samsungpay.domain;

import com.rbkmoney.damsel.domain.BankCardPaymentSystem;

public enum CardBrand {
    VISA("VI", BankCardPaymentSystem.visa),
    MASTERCARD("MC", BankCardPaymentSystem.mastercard);

    private String id;

    private BankCardPaymentSystem paymentSystem;

    CardBrand(String id, BankCardPaymentSystem paymentSystem) {
        this.id = id;
        this.paymentSystem = paymentSystem;
    }

    public String getId() {
        return id;
    }

    public BankCardPaymentSystem getPaymentSystem() {
        return paymentSystem;
    }

    public static BankCardPaymentSystem findPaymentSystemById(String id) {
        for (CardBrand cardBrand : values()) {
            if (cardBrand.getId().equalsIgnoreCase(id)) {
                return cardBrand.getPaymentSystem();
            }
        }
        return null;
    }
}
