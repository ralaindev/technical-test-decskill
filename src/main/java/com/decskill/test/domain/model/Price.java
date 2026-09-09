package com.decskill.test.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record Price(
        Long brandId,
        OffsetDateTime startDate,
        OffsetDateTime endDate,
        Long priceList,
        Long productId,
        Integer priority,
        BigDecimal price,
        String currency
) {
}
