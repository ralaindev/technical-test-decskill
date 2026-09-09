package com.decskill.test.infrastructure.input.rest.mapper;

import com.decskill.test.domain.model.Price;
import com.decskill.test.infrastructure.input.rest.model.PriceResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PriceRestMapper {

    PriceResponse toResponse(Price price);
}
