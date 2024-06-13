package dev.dex.cash_desk;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.io.*;
import java.util.*;

@Service
public class CashService {
    @Value("${api_key}")
    private String API_KEY;
    @Value("${cashier}")
    private String cashier;

    // pair <Denomination, Quantity>
    private final Map<Integer, Integer> denominationsBgn;
    private final Map<Integer, Integer> denominationsEur;
    private final Logger logger;

    public CashService(Logger logger) {
        this.logger = logger;
        this.denominationsBgn = new HashMap<>();
        this.denominationsBgn.put(10, 50);
        this.denominationsBgn.put(50, 10);
        this.denominationsEur = new HashMap<>();
        this.denominationsEur.put(10, 100);
        this.denominationsEur.put(50, 20);
    }

    public void cashOperation(String authHeader, CashOperationRequest cashOperationRequest) throws IOException {
        authenticate(authHeader);

        if (cashOperationRequest.cashOperation() == CashOperations.WITHDRAW) {
            if (cashOperationRequest.currency() == Currencies.BGN) {
                withdrawBgn(cashOperationRequest.denominations());
            } else if (cashOperationRequest.currency() == Currencies.EUR) {
                withdrawEur(cashOperationRequest.denominations());
            } else {
                throw new RuntimeException("We don't process the specified currency");
            }
        } else if (cashOperationRequest.cashOperation() == CashOperations.DEPOSIT) {
            if (cashOperationRequest.currency() == Currencies.BGN) {
                depositBgn(cashOperationRequest.denominations());
            } else if (cashOperationRequest.currency() == Currencies.EUR) {
                depositEur(cashOperationRequest.denominations());
            } else {
                throw new RuntimeException("We don't process the specified currency");
            }
        } else {
            throw new RuntimeException("Invalid Cash Operation");
        }
    }

    public BalanceResponse getBalance(String authHeader) {
        authenticate(authHeader);

        Integer balanceBgn = getBalanceBgn();
        Integer balanceEur = getBalanceEur();
        return new BalanceResponse(denominationsBgn, denominationsEur, balanceBgn, balanceEur);
    }

    private void authenticate(String authHeader) {
        if (!authHeader.equals(API_KEY)) {
            throw new RuntimeException("Unauthenticated");
        }
    }

    private Integer getBalanceBgn() {
        Integer balanceBgn = 0;
        for (Map.Entry<Integer, Integer> entry: denominationsBgn.entrySet()) {
            balanceBgn += entry.getKey() * entry.getValue();
        }
        return balanceBgn;
    }

    private Integer getBalanceEur() {
        Integer balanceEur = 0;
        for (Map.Entry<Integer, Integer> entry: denominationsEur.entrySet()) {
            balanceEur += entry.getKey() * entry.getValue();
        }
        return balanceEur;
    }

    private void withdrawBgn(Map<Integer, Integer> denominationsBgnRequest) throws IOException {
        for (Map.Entry<Integer, Integer> entry: denominationsBgnRequest.entrySet()) {
            if (!this.denominationsBgn.containsKey(entry.getKey())) {
                throw new RuntimeException("We don't have the requested denominationBgn: " + entry.getKey());
            }
            if (this.denominationsBgn.get(entry.getKey()) < entry.getValue()) {
                throw new RuntimeException("Not enough denominations to process request. Requested denominationsBgn: " + denominationsBgnRequest);
            }
        }

        for (Map.Entry<Integer, Integer> entry: denominationsBgnRequest.entrySet()) {
            Integer quantity = this.denominationsBgn.get(entry.getKey());
            quantity -= entry.getValue();
            this.denominationsBgn.put(entry.getKey(), quantity);
        }

        logger.logTransaction("Withdraw BGN " + denominationsBgnRequest);
        logger.logCashBalancesAndDenominations("Cash Balance BGN=" + getBalanceBgn()
                + ", Denominations BGN=" + this.denominationsBgn);
    }

    private void withdrawEur(Map<Integer, Integer> denominationsEurRequest) throws IOException {
        for (Map.Entry<Integer, Integer> entry: denominationsEurRequest.entrySet()) {
            if (!this.denominationsEur.containsKey(entry.getKey())) {
                throw new RuntimeException("We don't have the requested denominationEur: " + entry.getKey());
            }
            if (this.denominationsEur.get(entry.getKey()) < entry.getValue()) {
                throw new RuntimeException("Not enough denominations to process request. Requested denominationsEur: " + denominationsEurRequest);
            }
        }

        for (Map.Entry<Integer, Integer> entry: denominationsEurRequest.entrySet()) {
            Integer quantity = this.denominationsEur.get(entry.getKey());
            quantity -= entry.getValue();
            this.denominationsEur.put(entry.getKey(), quantity);
        }

        logger.logTransaction("Withdraw EUR " + denominationsEurRequest);
        logger.logCashBalancesAndDenominations("Cash Balance EUR=" + getBalanceEur()
                + ", Denominations EUR=" + this.denominationsEur);
    }

    private void depositBgn(Map<Integer, Integer> denominationsBgnRequest) throws IOException {
        for (Map.Entry<Integer, Integer> entry: denominationsBgnRequest.entrySet()) {
            Integer quantity = this.denominationsBgn.getOrDefault(entry.getKey(), 0);
            quantity += entry.getValue();
            this.denominationsBgn.put(entry.getKey(), quantity);
        }
        logger.logTransaction("Deposit BGN " + denominationsBgnRequest);
        logger.logCashBalancesAndDenominations("Cash Balance BGN=" + getBalanceBgn()
                + ", Denominations BGN=" + this.denominationsBgn);
    }

    private void depositEur(Map<Integer, Integer> denominationsEurRequest) throws IOException {
        for (Map.Entry<Integer, Integer> entry: denominationsEurRequest.entrySet()) {
            Integer quantity = this.denominationsEur.getOrDefault(entry.getKey(), 0);
            quantity += entry.getValue();
            this.denominationsEur.put(entry.getKey(), quantity);
        }
        logger.logTransaction("Deposit EUR " + denominationsEurRequest);
        logger.logCashBalancesAndDenominations("Cash Balance EUR=" + getBalanceEur()
                + ", Denominations EUR=" + this.denominationsEur);
    }
}
