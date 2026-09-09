package com.decskill.test.application.port.out;

import com.decskill.test.domain.model.Price;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface PriceRepositoryPort {

    Optional<Price> findApplicablePrice(
            OffsetDateTime queryDate,
            Long productId,
            Long brandId
    );
}
