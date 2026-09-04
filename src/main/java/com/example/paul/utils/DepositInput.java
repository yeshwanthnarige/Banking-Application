package com.example.paul.utils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

public class DepositInput {

    @NotBlank(message = "Target account no is mandatory")
    private String targetAccountNo;

    // add banking info of each user & fecting ;

    // Prevent fraudulent transfers attempting to abuse currency conversion errors
    @NotNull(message = "Transfer amount is mandatory")
    @DecimalMin(value = "0.01", message = "Transfer amount must be at least 0.01")
    @Digits(integer = 17, fraction = 2, message = "Transfer amount must have at most 2 decimal places")
    private BigDecimal amount;

    public DepositInput() {
    }

    public String getTargetAccountNo() {
        return targetAccountNo;
    }

    public void setTargetAccountNo(String targetAccountNo) {
        this.targetAccountNo = targetAccountNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "DepositInput{" +
                "targetAccountNo='" + targetAccountNo + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DepositInput that = (DepositInput) o;
        return Objects.equals(targetAccountNo, that.targetAccountNo) &&
                Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetAccountNo, amount);
    }
}
