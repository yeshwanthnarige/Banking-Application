package com.example.paul.services;

import com.example.paul.constants.ACTION;
import com.example.paul.models.Account;
import com.example.paul.models.Transaction;
import com.example.paul.repositories.AccountRepository;
import com.example.paul.repositories.TransactionRepository;
import com.example.paul.utils.InputValidator;
import com.example.paul.utils.TransactionInput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service class responsible for handling financial transactions
 * between accounts, including validations and balance updates.
 */
@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository,
                              TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Initiates a money transfer from a source account to a target account.
     * It checks for account existence and available balance before processing.
     *
     * @param transactionInput the transaction details including source, target, and amount
     * @return true if the transfer was successful, false otherwise
     */
    @Transactional
    public boolean makeTransfer(TransactionInput transactionInput) {
        // TODO refactor synchronous implementation with messaging queue
        if (!InputValidator.isSearchTransactionValid(transactionInput)) {
            return false;
        }

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

                updateBalance(sourceAccount.get(), transactionInput.getAmount(), ACTION.WITHDRAW);
                updateBalance(targetAccount.get(), transactionInput.getAmount(), ACTION.DEPOSIT);
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
    @Transactional
    public void updateAccountBalance(Account account, BigDecimal amount, ACTION action) {
        if (account == null || amount == null || amount.signum() <= 0 || action == null) {
            throw new IllegalArgumentException("Account, positive amount, and action are required");
        }

        updateBalance(account, amount, action);
    }

    private void updateBalance(Account account, BigDecimal amount, ACTION action) {
        if (action == ACTION.WITHDRAW) {
            account.setCurrentBalance(account.getCurrentBalance().subtract(amount));
        } else if (action == ACTION.DEPOSIT) {
            account.setCurrentBalance(account.getCurrentBalance().add(amount));
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
    public boolean isAmountAvailable(BigDecimal amount, BigDecimal accountBalance) {
        return amount != null && accountBalance != null &&
                amount.signum() > 0 && accountBalance.compareTo(amount) >= 0;
    }
}
