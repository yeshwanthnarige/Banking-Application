package com.example.paul.services;

import com.example.paul.models.Account;
import com.example.paul.repositories.AccountRepository;
import com.example.paul.repositories.TransactionRepository;
import com.example.paul.utils.CodeGenerator;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class that handles operations related to Account entities.
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Constructs an AccountService with the given repositories.
     *
     * @param accountRepository      the repository for account operations
     * @param transactionRepository  the repository for transaction operations
     */
    public AccountService(AccountRepository accountRepository,
                          TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Retrieves an account based on sort code and account number.
     * Also fetches and sets the account's transactions.
     *
     * @param sortCode       the sort code of the account
     * @param accountNumber  the account number
     * @return the matching Account, or null if not found
     */
    public Account getAccount(String sortCode, String accountNumber) {
        Optional<Account> account = accountRepository
                .findBySortCodeAndAccountNumber(sortCode, accountNumber);

        account.ifPresent(value ->
                value.setTransactions(transactionRepository
                        .findBySourceAccountIdOrderByInitiationDate(value.getId())));

        return account.orElse(null);
    }

    /**
     * Retrieves an account based solely on account number.
     *
     * @param accountNumber  the account number
     * @return the matching Account, or null if not found
     */
    public Account getAccount(String accountNumber) {
        Optional<Account> account = accountRepository
                .findByAccountNumber(accountNumber);

        return account.orElse(null);
    }

    /**
     * Creates a new account with generated sort code and account number.
     *
     * @param bankName   the name of the bank
     * @param ownerName  the name of the account owner
     * @return the newly created Account
     */
    public Account createAccount(String bankName, String ownerName) {
        CodeGenerator codeGenerator = new CodeGenerator();
        Account newAccount = new Account(bankName, ownerName, codeGenerator.generateSortCode(), codeGenerator.generateAccountNumber(), 0.00);
        return accountRepository.save(newAccount);
    }
}
