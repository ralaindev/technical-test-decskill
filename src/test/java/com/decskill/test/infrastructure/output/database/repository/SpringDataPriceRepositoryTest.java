package com.decskill.test.infrastructure.output.database.repository;

import com.decskill.test.infrastructure.output.database.entity.PriceJpaEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Limit;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@Sql("/sql/prices-repository-fixture.sql")
class SpringDataPriceRepositoryTest {

    @Autowired
    private SpringDataPriceRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @ParameterizedTest(name = "brand {0}, product {1} -> priceList {2}")
    @CsvSource({
            "10, 100, 102",
            "20, 100, 104",
            "10, 200, 105"
    })
    void shouldFilterByBrandAndProductAndChooseHighestPriority(Long brandId, Long productId, Long expectedPriceList) {
        PriceJpaEntity price = findPrice(
                brandId,
                productId,
                "2020-06-14T16:00:00+02:00"
        ).orElseThrow();

        assertEquals(expectedPriceList, price.getPriceList());
    }

    @ParameterizedTest(name = "{0} -> applicable={1}")
    @CsvSource({
            "2020-06-14T15:00:00+02:00, true",
            "2020-06-14T14:59:59+02:00, false",
            "2020-06-14T18:30:00+02:00, true",
            "2020-06-14T18:30:01+02:00, false"
    })
    void shouldUseInclusiveTemporalBoundaries(String queryDate, boolean applicable) {
        Optional<PriceJpaEntity> result = findPrice(30L, 300L, queryDate);

        assertEquals(applicable, result.isPresent());
        result.ifPresent(price -> assertEquals(108L, price.getPriceList()));
    }

    @ParameterizedTest(name = "brand {0}, product {1}, date {2} -> empty")
    @CsvSource({
            "99, 100, 2020-06-14T16:00:00+02:00",
            "10, 999, 2020-06-14T16:00:00+02:00",
            "10, 100, 2020-06-13T16:00:00+02:00",
            "10, 100, 2020-06-15T16:00:00+02:00"
    })
    void shouldReturnEmptyWhenNoPriceIsApplicable(Long brandId, Long productId, String queryDate) {
        assertTrue(findPrice(brandId, productId, queryDate).isEmpty());
    }

    @Test
    void shouldSelectSamePriceForEquivalentInstants() {
        PriceJpaEntity offsetPrice = findPrice(
                30L,
                300L,
                "2020-06-14T16:00:00+02:00"
        ).orElseThrow();

        PriceJpaEntity utcPrice = findPrice(
                30L,
                300L,
                "2020-06-14T14:00:00Z"
        ).orElseThrow();

        assertEquals(108L, offsetPrice.getPriceList());
        assertEquals(offsetPrice.getId(), utcPrice.getId());
    }

    @Test
    void shouldRejectPeriodEndingBeforeItsStart() {
        OffsetDateTime invalidEndDate = OffsetDateTime.parse("2020-06-14T12:59:59Z");

        assertThrows(
                DataIntegrityViolationException.class,
                () -> jdbcTemplate.update(
                        "UPDATE prices SET end_date = ? WHERE id = 108",
                        invalidEndDate
                )
        );
    }

    private Optional<PriceJpaEntity> findPrice(Long brandId, Long productId, String date) {
        return repository.findApplicablePrice(
                brandId,
                productId,
                OffsetDateTime.parse(date),
                Limit.of(1)
        );
    }
}