package dev.dex.cash_desk;

import jakarta.validation.constraints.*;

import java.util.*;

public record CashOperationRequest(
        @NotNull(message = "Cash operation cannot be null")
        CashOperations cashOperation,
        @NotNull(message = "Currency cannot be null")
        Currencies currency,
        Map<@Positive(message = "Denomination must be a positive integer") Integer, @Positive(message = "Quantity must be a positive integer")Integer> denominations
) {
}
