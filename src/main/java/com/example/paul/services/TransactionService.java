package com.example.paul.services;

import com.example.paul.constants.ACTION;
import com.example.paul.models.Account;
import com.example.paul.models.Transaction;
import com.example.paul.repositories.AccountRepository;
import com.example.paul.repositories.TransactionRepository;
import com.example.paul.utils.TransactionInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service class responsible for handling financial transactions
 * between accounts, including validations and balance updates.
 */
@Service
public class TransactionService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Initiates a money transfer from a source account to a target account.
     * It checks for account existence and available balance before processing.
     *
     * @param transactionInput the transaction details including source, target, and amount
     * @return true if the transfer was successful, false otherwise
     */
    public boolean makeTransfer(TransactionInput transactionInput) {
        // TODO refactor synchronous implementation with messaging queue
        String sourceSortCode = transactionInput.getSourceAccount().getSortCode();
        String sourceAccountNumber = transactionInput.getSourceAccount().getAccountNumber();
        Optional<Account> sourceAccount = accountRepository
                .findBySortCodeAndAccountNumber(sourceSortCode, sourceAccountNumber);

        String targetSortCode = transactionInput.getTargetAccount().getSortCode();
        String targetAccountNumber = transactionInput.getTargetAccount().getAccountNumber();
        Optional<Account> targetAccount = accountRepository
                .findBySortCodeAndAccountNumber(targetSortCode, targetAccountNumber);

        if (sourceAccount.isPresent() && targetAccount.isPresent()) {
            if (isAmountAvailable(transactionInput.getAmount(), sourceAccount.get().getCurrentBalance())) {
                var transaction = new Transaction();

                transaction.setAmount(transactionInput.getAmount());
                transaction.setSourceAccountId(sourceAccount.get().getId());
                transaction.setTargetAccountId(targetAccount.get().getId());
                transaction.setTargetOwnerName(targetAccount.get().getOwnerName());
                transaction.setInitiationDate(LocalDateTime.now());
                transaction.setCompletionDate(LocalDateTime.now());
                transaction.setReference(transactionInput.getReference());
                transaction.setLatitude(transactionInput.getLatitude());
                transaction.setLongitude(transactionInput.getLongitude());

                updateAccountBalance(sourceAccount.get(), transactionInput.getAmount(), ACTION.WITHDRAW);
                transactionRepository.save(transaction);

                return true;
            }
        }
        return false;
    }

    /**
     * Updates the balance of a given account based on the specified action.
     *
     * @param account the account to update
     * @param amount  the amount to withdraw or deposit
     * @param action  the action to perform (WITHDRAW or DEPOSIT)
     */
    public void updateAccountBalance(Account account, double amount, ACTION action) {
        if (action == ACTION.WITHDRAW) {
            account.setCurrentBalance((account.getCurrentBalance() - amount));
        } else if (action == ACTION.DEPOSIT) {
            account.setCurrentBalance((account.getCurrentBalance() + amount));
        }
        accountRepository.save(account);
    }

    /**
     * Checks if the account has sufficient balance for a withdrawal.
     * Note: Overdraft or credit support is not implemented yet.
     *
     * @param amount         the amount to check for availability
     * @param accountBalance the current balance of the account
     * @return true if sufficient funds are available, false otherwise
     */
    public boolean isAmountAvailable(double amount, double accountBalance) {
        return (accountBalance - amount) > 0;
    }
}
