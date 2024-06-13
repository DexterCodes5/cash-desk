package dev.dex.cash_desk;

import jakarta.validation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;

@RestController
@RequestMapping("/api/v1/cash-operation")
public class CashOperationController {
    private final CashService cashService;

    public CashOperationController(CashService cashService) {
        this.cashService = cashService;
    }

    @PostMapping
    public ResponseEntity cashOperation(@RequestHeader("FIB-X-AUTH") String authHeader,
                                        @Valid @RequestBody CashOperationRequest cashOperationRequest) throws IOException {
        cashService.cashOperation(authHeader, cashOperationRequest);
        return ResponseEntity.ok("Successful Operation");
    }
}
