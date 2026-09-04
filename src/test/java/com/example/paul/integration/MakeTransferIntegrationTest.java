package com.example.paul.integration;

import com.example.paul.controllers.TransactionRestController;
import com.example.paul.repositories.AccountRepository;
import com.example.paul.repositories.TransactionRepository;
import com.example.paul.utils.AccountInput;
import com.example.paul.utils.TransactionInput;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "local")
class MakeTransferIntegrationTest {

    @Autowired
    private TransactionRestController transactionRestController;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void givenTransactionDetails_whenMakeTransaction_thenVerifyTransactionIsProcessed() {
        // given
        var sourceAccount = new AccountInput();
        sourceAccount.setSortCode("53-68-92");
        sourceAccount.setAccountNumber("73084635");

        var targetAccount = new AccountInput();
        targetAccount.setSortCode("65-93-37");
        targetAccount.setAccountNumber("21956204");

        var input = new TransactionInput();
        input.setSourceAccount(sourceAccount);
        input.setTargetAccount(targetAccount);
        input.setAmount(new BigDecimal("27.50"));
        input.setReference("My reference");
        input.setLatitude(45.0000000);
        input.setLongitude(90.0000000);

        var sourceBefore = accountRepository
                .findBySortCodeAndAccountNumber("53-68-92", "73084635")
                .orElseThrow()
                .getCurrentBalance();
        var targetBefore = accountRepository
                .findBySortCodeAndAccountNumber("65-93-37", "21956204")
                .orElseThrow()
                .getCurrentBalance();
        var transactionCountBefore = transactionRepository.count();

        // when
        var body = transactionRestController.makeTransfer(input).getBody();

        // then
        var isComplete = (Boolean) body;
        assertThat(isComplete).isTrue();
        assertThat(accountRepository
                .findBySortCodeAndAccountNumber("53-68-92", "73084635")
                .orElseThrow()
                .getCurrentBalance()).isEqualByComparingTo(sourceBefore.subtract(input.getAmount()));
        assertThat(accountRepository
                .findBySortCodeAndAccountNumber("65-93-37", "21956204")
                .orElseThrow()
                .getCurrentBalance()).isEqualByComparingTo(targetBefore.add(input.getAmount()));
        assertThat(transactionRepository.count()).isEqualTo(transactionCountBefore + 1);
    }
}
