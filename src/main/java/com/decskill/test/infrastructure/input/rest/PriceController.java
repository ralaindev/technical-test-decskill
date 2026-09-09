package com.decskill.test.infrastructure.input.rest;

import com.decskill.test.application.port.in.GetPriceUseCase;
import com.decskill.test.infrastructure.input.rest.api.PricesApi;
import com.decskill.test.infrastructure.input.rest.mapper.PriceRestMapper;
import com.decskill.test.infrastructure.input.rest.model.PriceResponse;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
public class PriceController implements PricesApi {

    private final GetPriceUseCase getPriceUseCase;
    private final PriceRestMapper priceRestMapper;

    public PriceController(
            GetPriceUseCase getPriceUseCase,
            PriceRestMapper priceRestMapper) {
        this.getPriceUseCase = getPriceUseCase;
        this.priceRestMapper = priceRestMapper;
    }

    @Override
    public PriceResponse getPrice(
            OffsetDateTime queryDate,
            Long productId,
            Long brandId) {

        return priceRestMapper.toResponse(
                getPriceUseCase.getPrice(queryDate, productId, brandId)
        );
    }
}