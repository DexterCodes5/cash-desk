package dev.dex.cash_desk;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.io.*;
import java.util.*;

@Component
public class Logger {
    private BufferedWriter bufferedWriter;
    @Value("${logs.transactions.file_path}")
    private String transactionsLogFilePath;
    @Value("${logs.cash_balances_and_denominations.file_path}")
    private String cashBalancesAndDenominationsLogFilePath;

    public void logTransaction(String log) throws IOException {
        try {
            this.bufferedWriter = new BufferedWriter(new FileWriter(transactionsLogFilePath, true));
            this.bufferedWriter.append(new Date() + "\t" + log + "\n");
        } catch (IOException ex) {
            System.out.println("Can't find file");
        } finally {
            this.bufferedWriter.close();
        }
    }

    public void logCashBalancesAndDenominations(String log) throws IOException {
        try {
            this.bufferedWriter = new BufferedWriter(new FileWriter(cashBalancesAndDenominationsLogFilePath, true));
            this.bufferedWriter.append(new Date() + "\t" + log + "\n");
        } catch (IOException ex) {
            System.out.println("Can't find file");
        } finally {
            this.bufferedWriter.close();
        }
    }
}
