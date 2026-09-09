package com.decskill.test.infrastructure.output.database.repository;

import com.decskill.test.infrastructure.output.database.entity.PriceJpaEntity;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface SpringDataPriceRepository
        extends JpaRepository<PriceJpaEntity, Long> {

    @Query("""
            SELECT p FROM PriceJpaEntity p
            WHERE p.brandId = :brandId
              AND p.productId = :productId
              AND p.startDate <= :queryDate
              AND p.endDate >= :queryDate
            ORDER BY p.priority DESC
            """)
    Optional<PriceJpaEntity> findApplicablePrice(
            @Param("brandId") Long brandId,
            @Param("productId") Long productId,
            @Param("queryDate") OffsetDateTime queryDate,
            Limit limit
    );
}
