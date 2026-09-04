package com.example.paul.utils;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

public class WithdrawInput extends AccountInput{
    // Prevent fraudulent transfers attempting to abuse currency conversion errors
    @NotNull(message = "Transfer amount is mandatory")
    @DecimalMin(value = "0.01", message = "Transfer amount must be at least 0.01")
    @Digits(integer = 17, fraction = 2, message = "Transfer amount must have at most 2 decimal places")
    private BigDecimal amount;

    public WithdrawInput() {}

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "AccountInput{" +
                "sortCode='" + getSortCode() + '\'' +
                ", accountNumber='" + getAccountNumber() + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WithdrawInput that = (WithdrawInput) o;
        return Objects.equals(getSortCode(), that.getSortCode()) &&
                Objects.equals(getAccountNumber(), that.getAccountNumber()) &&
                Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSortCode(), getAccountNumber(), amount);
    }
}
