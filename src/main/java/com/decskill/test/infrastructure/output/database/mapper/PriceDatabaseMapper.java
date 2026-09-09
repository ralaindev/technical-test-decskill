package com.decskill.test.infrastructure.output.database.mapper;

import com.decskill.test.domain.model.Price;
import com.decskill.test.infrastructure.output.database.entity.PriceJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PriceDatabaseMapper {

    Price toDomain(PriceJpaEntity entity);
}
