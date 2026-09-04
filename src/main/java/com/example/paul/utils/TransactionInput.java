package com.example.paul.utils;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class TransactionInput {

    @Valid
    @NotNull(message = "Source account is mandatory")
    private AccountInput sourceAccount;

    @Valid
    @NotNull(message = "Target account is mandatory")
    private AccountInput targetAccount;

    @NotNull(message = "Transfer amount is mandatory")
    @DecimalMin(value = "0.01", message = "Transfer amount must be at least 0.01")
    @Digits(integer = 17, fraction = 2, message = "Transfer amount must have at most 2 decimal places")
    private BigDecimal amount;

    @Size(max = 255, message = "Reference must not exceed 255 characters")
    private String reference;

    @DecimalMin(value = "-90", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90", message = "Latitude must be between -90 and 90")
    private Double latitude;

    @DecimalMin(value = "-180", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180", message = "Longitude must be between -180 and 180")
    private Double longitude;

    public TransactionInput() {}

    public AccountInput getSourceAccount() {
        return sourceAccount;
    }
    public void setSourceAccount(AccountInput sourceAccount) {
        this.sourceAccount = sourceAccount;
    }
    public AccountInput getTargetAccount() {
        return targetAccount;
    }
    public void setTargetAccount(AccountInput targetAccount) {
        this.targetAccount = targetAccount;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public String getReference() {
        return reference;
    }
    public void setReference(String reference) {
        this.reference = reference;
    }
    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    public Double getLongitude() {
        return longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "TransactionInput{" +
                "sourceAccount=" + sourceAccount +
                ", targetAccount=" + targetAccount +
                ", amount=" + amount +
                ", reference='" + reference + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
