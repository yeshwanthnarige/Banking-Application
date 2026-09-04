package com.example.paul.utils;

import com.example.paul.constants.constants;

public class InputValidator {

    public static boolean isSearchCriteriaValid(AccountInput accountInput) {
        return accountInput != null &&
                accountInput.getSortCode() != null &&
                accountInput.getAccountNumber() != null &&
                constants.SORT_CODE_PATTERN.matcher(accountInput.getSortCode()).matches() &&
                constants.ACCOUNT_NUMBER_PATTERN.matcher(accountInput.getAccountNumber()).matches();
    }

    public static boolean isAccountNoValid(String accountNo) {
        return accountNo != null && constants.ACCOUNT_NUMBER_PATTERN.matcher(accountNo).matches();
    }

    public static boolean isCreateAccountCriteriaValid(CreateAccountInput createAccountInput) {
        return createAccountInput != null &&
                createAccountInput.getBankName() != null &&
                createAccountInput.getOwnerName() != null &&
                !createAccountInput.getBankName().isBlank() &&
                !createAccountInput.getOwnerName().isBlank();
    }

    public static boolean isSearchTransactionValid(TransactionInput transactionInput) {
        // TODO Add checks for large amounts; consider past history of account holder and location of transfers

        if (transactionInput == null || !isSearchCriteriaValid(transactionInput.getSourceAccount()))
            return false;

        if (!isSearchCriteriaValid(transactionInput.getTargetAccount()))
            return false;

        if (transactionInput.getSourceAccount().equals(transactionInput.getTargetAccount()))
            return false;

        return transactionInput.getAmount() != null && transactionInput.getAmount().signum() > 0;
    }
}
