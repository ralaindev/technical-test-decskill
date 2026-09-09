package com.decskill.test.application.service;

import com.decskill.test.application.exception.PriceNotFoundException;
import com.decskill.test.application.port.out.PriceRepositoryPort;
import com.decskill.test.domain.model.Price;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetPriceServiceTest {

    @Mock
    private PriceRepositoryPort priceRepositoryPort;

    @InjectMocks
    private GetPriceService getPriceService;

    @Test
    void shouldReturnApplicablePrice() {
        OffsetDateTime queryDate = OffsetDateTime.of(2020, 6, 14, 16, 0, 0, 0, ZoneOffset.ofHours(2));

        Price expectedPrice = new Price(
                1L,
                OffsetDateTime.parse("2020-06-14T15:00:00+02:00"),
                OffsetDateTime.parse("2020-06-14T18:30:00+02:00"),
                2L,
                35455L,
                1,
                new BigDecimal("25.45"),
                "EUR"
        );

        when(priceRepositoryPort
                .findApplicablePrice(queryDate, 35455L, 1L))
                .thenReturn(Optional.of(expectedPrice));

        Price result = getPriceService.getPrice(queryDate,35455L,1L);

        assertEquals(expectedPrice, result);
        verify(priceRepositoryPort).findApplicablePrice(same(queryDate), eq(35455L), eq(1L));
    }

    @Test
    void shouldThrowPriceNotFoundExceptionWhenNoApplicablePriceExists() {
        OffsetDateTime queryDate =
                OffsetDateTime.of(2020, 6, 14, 16, 0, 0, 0, ZoneOffset.ofHours(2));

        when(priceRepositoryPort.findApplicablePrice(queryDate, 35455L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(PriceNotFoundException.class, () -> getPriceService.getPrice(queryDate, 35455L, 1L));
        verify(priceRepositoryPort).findApplicablePrice(same(queryDate), eq(35455L), eq(1L));
    }
}
