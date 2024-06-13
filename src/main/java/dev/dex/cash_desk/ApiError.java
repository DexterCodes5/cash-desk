package dev.dex.cash_desk;

import java.time.*;

public record ApiError(
        String path,
        String message,
        int statusCode,
        LocalDateTime localDateTime
) {
}
