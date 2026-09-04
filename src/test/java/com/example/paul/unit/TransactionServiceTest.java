package com.example.paul.unit;

import com.example.paul.models.Account;
import com.example.paul.models.Transaction;
import com.example.paul.repositories.AccountRepository;
import com.example.paul.repositories.TransactionRepository;
import com.example.paul.services.TransactionService;
import com.example.paul.utils.AccountInput;
import com.example.paul.utils.TransactionInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    private TransactionService transactionService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    private Account sourceAccount;
    private Account targetAccount;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService(accountRepository, transactionRepository);
        sourceAccount = new Account(1L, "53-68-92", "78901234", new BigDecimal("458.10"), "Some Bank", "John");
        targetAccount = new Account(2L, "67-41-18", "48573590", new BigDecimal("64.90"), "Some Other Bank", "Major");
    }

    @Test
    void transferDebitsSourceCreditsTargetAndRecordsTransaction() {
        stubAccounts();
        var input = createInput("50.00");

        boolean isComplete = transactionService.makeTransfer(input);

        assertThat(isComplete).isTrue();
        assertThat(sourceAccount.getCurrentBalance()).isEqualByComparingTo("408.10");
        assertThat(targetAccount.getCurrentBalance()).isEqualByComparingTo("114.90");
        verify(accountRepository).save(sourceAccount);
        verify(accountRepository).save(targetAccount);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        Transaction transaction = transactionCaptor.getValue();
        assertThat(transaction.getSourceAccountId()).isEqualTo(sourceAccount.getId());
        assertThat(transaction.getTargetAccountId()).isEqualTo(targetAccount.getId());
        assertThat(transaction.getAmount()).isEqualByComparingTo("50.00");
        assertThat(transaction.getCompletionDate()).isNotNull();
    }

    @Test
    void transferAllowsWithdrawingTheFullBalance() {
        stubAccounts();
        var input = createInput("458.10");

        boolean isComplete = transactionService.makeTransfer(input);

        assertThat(isComplete).isTrue();
        assertThat(sourceAccount.getCurrentBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(targetAccount.getCurrentBalance()).isEqualByComparingTo("523.00");
    }

    @Test
    void transferRejectsInsufficientFundsWithoutChangingEitherAccount() {
        stubAccounts();
        var input = createInput("10000.00");

        boolean isComplete = transactionService.makeTransfer(input);

        assertThat(isComplete).isFalse();
        assertThat(sourceAccount.getCurrentBalance()).isEqualByComparingTo("458.10");
        assertThat(targetAccount.getCurrentBalance()).isEqualByComparingTo("64.90");
        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void transferRejectsNonPositiveAmountBeforeRepositoryAccess() {
        var input = createInput("0.00");

        boolean isComplete = transactionService.makeTransfer(input);

        assertThat(isComplete).isFalse();
        verifyNoInteractions(accountRepository, transactionRepository);
    }

    private TransactionInput createInput(String amount) {
        var source = new AccountInput();
        source.setSortCode("53-68-92");
        source.setAccountNumber("78901234");

        var target = new AccountInput();
        target.setSortCode("67-41-18");
        target.setAccountNumber("48573590");

        var input = new TransactionInput();
        input.setSourceAccount(source);
        input.setTargetAccount(target);
        input.setAmount(new BigDecimal(amount));
        input.setReference("My reference");
        return input;
    }

    private void stubAccounts() {
        when(accountRepository.findBySortCodeAndAccountNumber("53-68-92", "78901234"))
                .thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findBySortCodeAndAccountNumber("67-41-18", "48573590"))
                .thenReturn(Optional.of(targetAccount));
    }
}
