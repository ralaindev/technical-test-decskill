package com.decskill.test.application.service;

import com.decskill.test.application.exception.PriceNotFoundException;
import com.decskill.test.application.port.in.GetPriceUseCase;
import com.decskill.test.application.port.out.PriceRepositoryPort;
import com.decskill.test.domain.model.Price;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class GetPriceService implements GetPriceUseCase {

    private final PriceRepositoryPort priceRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public Price getPrice(OffsetDateTime queryDate, Long productId, Long brandId) {
        return priceRepositoryPort
                .findApplicablePrice(queryDate, productId, brandId)
                .orElseThrow(PriceNotFoundException::new);
    }
}
