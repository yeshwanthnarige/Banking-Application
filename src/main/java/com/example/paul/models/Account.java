package com.example.paul.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;
import java.math.BigDecimal;
import java.util.List;

// TODO Add support for multiple account types (business, savings, etc.)
// TODO Add support for foreign currency accounts
@Entity
@Table(name = "account", schema = "online_bank")
@SequenceGenerator(name = "account_seq", sequenceName = "account_sequence", schema = "online_bank", allocationSize = 1)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq")
    private long id;

    @Version
    private long version;

    @Column(length = 8, nullable = false)
    private String sortCode;

    @Column(length = 8, nullable = false)
    private String accountNumber;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal currentBalance;

    @Column(length = 50, nullable = false)
    private String bankName;

    @Column(length = 50, nullable = false)
    private String ownerName;

    private transient List<Transaction> transactions;

    protected Account() {}
    public Account(String bankName, String ownerName, String generateSortCode, String generateAccountNumber, BigDecimal currentBalance) {
        this.sortCode = generateSortCode;
        this.accountNumber = generateAccountNumber;
        this.currentBalance = currentBalance;
        this.bankName = bankName;
        this.ownerName = ownerName;
    }
    public Account(long id, String sortCode, String accountNumber, BigDecimal currentBalance, String bankName, String ownerName) {
        this.id = id;
        this.sortCode = sortCode;
        this.accountNumber = accountNumber;
        this.currentBalance = currentBalance;
        this.bankName = bankName;
        this.ownerName = ownerName;
    }

    public Account(long id, String sortCode, String accountNumber, BigDecimal currentBalance, String bankName, String ownerName, List<Transaction> transactions) {
        this.id = id;
        this.sortCode = sortCode;
        this.accountNumber = accountNumber;
        this.currentBalance = currentBalance;
        this.bankName = bankName;
        this.ownerName = ownerName;
        this.transactions = transactions;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getSortCode() {
        return sortCode;
    }
    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }
    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }
    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }
    public String getOwnerName() {
        return ownerName;
    }
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    public String getBankName() {
        return bankName;
    }
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
    public List<Transaction> getTransactions() {
        return transactions;
    }
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", sortCode='" + sortCode + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", currentBalance=" + currentBalance +
                ", bankName='" + bankName + '\'' +
                ", ownerName='" + ownerName + '\'' +
                '}';
    }
}
