package dev.dex.cash_desk;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cash-balance")
public class BalanceController {
    private final CashService cashService;

    public BalanceController(CashService cashService) {
        this.cashService = cashService;
    }

    @GetMapping
    public ResponseEntity<?> getBalance(@RequestHeader("FIB-X-AUTH") String authHeader) {
        return ResponseEntity.ok(cashService.getBalance(authHeader));
    }
}
