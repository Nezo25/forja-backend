package com.pokeprint.api.dto.response;

import java.math.BigDecimal;

public record MonthlySalesDTO(
    String mes,
    int monthNum,
    BigDecimal vendas
) {}
