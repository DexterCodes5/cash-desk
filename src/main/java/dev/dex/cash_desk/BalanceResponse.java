package dev.dex.cash_desk;

import java.util.*;

public record BalanceResponse(
        Map<Integer, Integer> denominationsBgn,
        Map<Integer, Integer> denominationsEur,
        Integer balanceBgn,
        Integer balanceEur
) {
}
