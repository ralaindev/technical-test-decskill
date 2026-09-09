package com.decskill.test.infrastructure.output.database;

import com.decskill.test.application.port.out.PriceRepositoryPort;
import com.decskill.test.domain.model.Price;
import com.decskill.test.infrastructure.output.database.entity.PriceJpaEntity;
import com.decskill.test.infrastructure.output.database.mapper.PriceDatabaseMapper;
import com.decskill.test.infrastructure.output.database.repository.SpringDataPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PriceDatabaseAdapter implements PriceRepositoryPort {

    private final SpringDataPriceRepository repository;
    private final PriceDatabaseMapper priceDatabaseMapper;

    @Override
    public Optional<Price> findApplicablePrice(
            OffsetDateTime queryDate,
            Long productId,
            Long brandId) {

        return repository
                .findApplicablePrice(brandId, productId, queryDate, Limit.of(1))
                .map(priceDatabaseMapper::toDomain);
    }
}
