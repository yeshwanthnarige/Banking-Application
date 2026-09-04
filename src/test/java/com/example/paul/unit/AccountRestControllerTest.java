package com.example.paul.unit;

import com.example.paul.controllers.AccountRestController;
import com.example.paul.models.Account;
import com.example.paul.services.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AccountRestController.class)
class AccountRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AccountService accountService;

    @Test
    void givenMissingInput_whenCheckingBalance_thenVerifyBadRequest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void givenInvalidInput_whenCheckingBalance_thenVerifyBadRequest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts")
                .content("{\"sortCode\": \"53-68\",\"accountNumber\": \"78934\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void givenNoAccountForInput_whenCheckingBalance_thenVerifyMessage() throws Exception {
        given(accountService.getAccount("53-68-92", "78901234")).willReturn(null);

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts")
                .content("{\"sortCode\": \"53-68-92\",\"accountNumber\": \"78901234\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        "Unable to find an account matching this sort code and account number"));
    }

    @Test
    void givenAccountDetails_whenCheckingBalance_thenVerifyOk() throws Exception {
        given(accountService.getAccount("53-68-92", "78901234")).willReturn(
                new Account(1L, "53-68-92", "78901234", new BigDecimal("10.10"), "Some Bank", "John"));

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts")
                .content("{\"sortCode\": \"53-68-92\",\"accountNumber\": \"78901234\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currentBalance").value(10.10))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ownerName").value("John"));
    }
}
