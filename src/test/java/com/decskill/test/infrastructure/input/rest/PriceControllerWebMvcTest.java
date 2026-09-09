package com.decskill.test.infrastructure.input.rest;

import com.decskill.test.application.exception.PriceNotFoundException;
import com.decskill.test.application.port.in.GetPriceUseCase;
import com.decskill.test.domain.model.Price;
import com.decskill.test.infrastructure.input.rest.mapper.PriceRestMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PriceController.class)
@Import(PriceRestMapperImpl.class)
class PriceControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetPriceUseCase getPriceUseCase;

    @Test
    void shouldParseParametersAndMapStoredDatesWithoutUsingQueryOffset() throws Exception {
        OffsetDateTime queryDate = OffsetDateTime.parse("2020-06-14T09:00:00-05:00");
        Price price = new Price(1L,
                OffsetDateTime.parse("2020-06-14T15:00:00+02:00"),
                OffsetDateTime.parse("2020-06-14T18:30:00+02:00"),
                2L, 35455L, 1, new BigDecimal("25.45"), "EUR");
        when(getPriceUseCase.getPrice(queryDate, 35455L, 1L)).thenReturn(price);

        mockMvc.perform(request(queryDate.toString(), "35455", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(2))
                .andExpect(jsonPath("$.startDate").value("2020-06-14T15:00:00+02:00"))
                .andExpect(jsonPath("$.endDate").value("2020-06-14T18:30:00+02:00"))
                .andExpect(jsonPath("$.price").value(25.45))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.priority").doesNotExist());

        verify(getPriceUseCase).getPrice(queryDate, 35455L, 1L);
    }

    @ParameterizedTest
    @CsvSource({
            ", 35455, 1",
            "2020-06-14T16:00:00+02:00, , 1",
            "2020-06-14T16:00:00+02:00, 35455, ",
            "invalid-date, 35455, 1",
            "2020-06-14T16:00:00, 35455, 1",
            "2020-06-14T16:00:00+02:00, invalid, 1",
            "2020-06-14T16:00:00+02:00, 35455, invalid",
            "2020-06-14T16:00:00+02:00, 0, 1",
            "2020-06-14T16:00:00+02:00, -1, 1",
            "2020-06-14T16:00:00+02:00, 35455, 0",
            "2020-06-14T16:00:00+02:00, 35455, -1"
    })
    void shouldRejectMissingMalformedOrOutOfRangeParameters(String queryDate, String productId,
                                                           String brandId) throws Exception {
        mockMvc.perform(request(queryDate, productId, brandId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Parámetros de entrada no válidos."));

        verifyNoInteractions(getPriceUseCase);
    }

    @Test
    void shouldMapPriceNotFoundException() throws Exception {
        OffsetDateTime queryDate = OffsetDateTime.parse("2020-06-14T16:00:00+02:00");
        when(getPriceUseCase.getPrice(queryDate, 35455L, 1L)).thenThrow(new PriceNotFoundException());

        mockMvc.perform(request(queryDate.toString(), "35455", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No se ha encontrado un precio aplicable."));
    }

    private MockHttpServletRequestBuilder request(String queryDate, String productId, String brandId) {
        MockHttpServletRequestBuilder request = get("/api/v1/prices");
        if (queryDate != null) {
            request.param("queryDate", queryDate);
        }
        if (productId != null) {
            request.param("productId", productId);
        }
        if (brandId != null) {
            request.param("brandId", brandId);
        }
        return request;
    }
}
