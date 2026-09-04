package com.example.paul.unit;

import com.example.paul.models.Account;
import com.example.paul.models.Transaction;
import com.example.paul.repositories.AccountRepository;
import com.example.paul.repositories.TransactionRepository;
import com.example.paul.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;

    public AccountService underTest;

    @BeforeEach
    void setUp() {
        underTest = new AccountService(accountRepository, transactionRepository);
    }

    @Test
    void shouldReturnAccountBySortCodeAndAccountNumberWhenPresent() {
        var account = new Account(1L, "53-68-92", "78901234", new BigDecimal("10.10"), "Some Bank", "John");
        when(accountRepository.findBySortCodeAndAccountNumber("53-68-92", "78901234"))
                .thenReturn(Optional.of(account));

        var result = underTest.getAccount("53-68-92", "78901234");

        assertThat(result.getOwnerName()).isEqualTo(account.getOwnerName());
        assertThat(result.getSortCode()).isEqualTo(account.getSortCode());
        assertThat(result.getAccountNumber()).isEqualTo(account.getAccountNumber());
    }

    @Test
    void shouldReturnTransactionsForAccount() {
        var account = new Account(1L, "53-68-92", "78901234", new BigDecimal("10.10"), "Some Bank", "John");
        when(accountRepository.findBySortCodeAndAccountNumber("53-68-92", "78901234"))
                .thenReturn(Optional.of(account));
        var transaction1 = new Transaction();
        var transaction2 = new Transaction();
        transaction1.setReference("a");
        transaction2.setReference("b");
        when(transactionRepository.findBySourceAccountIdOrderByInitiationDate(account.getId()))
                .thenReturn(List.of(transaction1, transaction2));

        var result = underTest.getAccount("53-68-92", "78901234");

        assertThat(result.getTransactions()).hasSize(2);
        assertThat(result.getTransactions()).extracting("reference").containsExactly("a", "b");
    }

    @Test
    void shouldReturnNullWhenAccountBySortCodeAndAccountNotFound() {
        when(accountRepository.findBySortCodeAndAccountNumber("53-68-92", "78901234"))
                .thenReturn(Optional.empty());

        var result = underTest.getAccount("53-68-92", "78901234");

        assertThat(result).isNull();
    }

    @Test
    void shouldReturnAccountByAccountNumberWhenPresent() {
        var account = new Account(1L, "53-68-92", "78901234", new BigDecimal("10.10"), "Some Bank", "John");
        when(accountRepository.findByAccountNumber("78901234")).thenReturn(Optional.of(account));

        var result = underTest.getAccount("78901234");

        assertThat(result).isSameAs(account);
    }

    @Test
    void shouldReturnNullWhenAccountByAccountNotFound() {
        when(accountRepository.findByAccountNumber("78901234")).thenReturn(Optional.empty());

        var result = underTest.getAccount("78901234");

        assertThat(result).isNull();
    }

    @Test
    void shouldCreateAccount() {
        when(accountRepository.save(org.mockito.ArgumentMatchers.any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = underTest.createAccount("Some Bank", "John");

        assertThat(result.getBankName()).isEqualTo("Some Bank");
        assertThat(result.getOwnerName()).isEqualTo("John");
        assertThat(result.getCurrentBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getSortCode()).matches("\\d{2}-\\d{2}-\\d{2}");
        assertThat(result.getAccountNumber()).matches("\\d{8}");
        verify(accountRepository).save(result);
    }
}
